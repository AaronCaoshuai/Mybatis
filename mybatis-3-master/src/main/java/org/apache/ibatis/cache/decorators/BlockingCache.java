/**
 *    Copyright 2009-2018 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.apache.ibatis.cache.decorators;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.ibatis.cache.Cache;
import org.apache.ibatis.cache.CacheException;

/**
 * Simple blocking decorator
 *
 * Simple and inefficient version of EhCache's BlockingCache decorator.
 * It sets a lock over a cache key when the element is not found in cache.
 * This way, other threads will wait until this element is filled instead of hitting the database.
 *
 * @author Eduardo Macarron
 * 阻塞版本的缓存装饰器
 * 保证只有一个线程到数据库中查找指定key对象的数据
 * 假设线程A在BlockingCache中未查找到keyA对应的缓存项时,线程A会获取keyA对应的锁
 * 这样后续线程在查找keyA时,会发生阻塞
 *
 * 假设线程A从数据库中查找到keyA对应的结果对象后,将结果对象放入到BlockingCache中
 * 此时线程A会释放keyA对应的锁,唤醒阻塞在该锁上的线程,其他线程即可从BlockingCache中
 * 获取keyA对应的数据,而不是再次访问数据库
 *
 */
public class BlockingCache implements Cache {
  //阻塞超时时长
  private long timeout;
  //被装饰的底层Cache对象
  private final Cache delegate;
  //每个key都有对应的ReentrantLock对象
  private final ConcurrentHashMap<Object, ReentrantLock> locks;

  public BlockingCache(Cache delegate) {
    this.delegate = delegate;
    this.locks = new ConcurrentHashMap<>();
  }

  @Override
  public String getId() {
    return delegate.getId();
  }

  @Override
  public int getSize() {
    return delegate.getSize();
  }

  @Override
  public void putObject(Object key, Object value) {
    try {
      delegate.putObject(key, value);//想缓存中添加缓存项
    } finally {
      releaseLock(key);//释放锁
    }
  }

  @Override
  public Object getObject(Object key) {
    acquireLock(key);//获取该key的对应的锁
    Object value = delegate.getObject(key);
    if (value != null) {
      releaseLock(key);
    }
    return value;
  }

  @Override
  public Object removeObject(Object key) {
    // despite of its name, this method is called only to release locks
    releaseLock(key);
    return null;
  }

  @Override
  public void clear() {
    delegate.clear();
  }

  @Override
  public ReadWriteLock getReadWriteLock() {
    return null;
  }

  private ReentrantLock getLockForKey(Object key) {
    return locks.computeIfAbsent(key, k -> new ReentrantLock());
  }
  //获取该key对应的锁
  private void acquireLock(Object key) {
    //获取ReentrantLock锁 如果没有锁则新建一个返回
    Lock lock = getLockForKey(key);
    if (timeout > 0) {
      try {
        //获取锁,带超时时长
        boolean acquired = lock.tryLock(timeout, TimeUnit.MILLISECONDS);
        if (!acquired) {//超时 抛出异常
          throw new CacheException("Couldn't get a lock in " + timeout + " for the key " +  key + " at the cache " + delegate.getId());
        }
      } catch (InterruptedException e) {
        throw new CacheException("Got interrupted while trying to acquire lock for key " + key, e);
      }
    } else {
      lock.lock();//获取锁,不带超时时长
    }
  }
  //释放锁
  private void releaseLock(Object key) {
    ReentrantLock lock = locks.get(key);//获取锁
    if (lock.isHeldByCurrentThread()) {//锁是否被当前线程持有
      lock.unlock();//释放锁
    }
  }

  public long getTimeout() {
    return timeout;
  }

  public void setTimeout(long timeout) {
    this.timeout = timeout;
  }
}
