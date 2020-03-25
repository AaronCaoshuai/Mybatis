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

import java.util.Deque;
import java.util.LinkedList;
import java.util.concurrent.locks.ReadWriteLock;

import org.apache.ibatis.cache.Cache;

/**
 * FIFO (first in, first out) cache decorator
 *
 * @author Clinton Begin
 * 为了控制缓存大小 系统需要按照一定的规则清理缓存
 * 先入先出版本的Cache装饰器
 * 向缓存添加数据时,如果缓存项的个数已经到达了上限
 * 则会将缓存中最老(及最早进入缓存的)缓存项删除
 */
public class FifoCache implements Cache {
  //底层被装饰的Cache对象
  private final Cache delegate;
  //用于记录key进入缓存的先后顺序
  //使用的是LinkedList<Object> 类型的集合对象
  private final Deque<Object> keyList;
  //记录了缓存项的上线,超过改值,则需要清理最老的缓存项
  private int size;

  public FifoCache(Cache delegate) {
    this.delegate = delegate;
    this.keyList = new LinkedList<>();
    this.size = 1024;
  }

  @Override
  public String getId() {
    return delegate.getId();
  }

  @Override
  public int getSize() {
    return delegate.getSize();
  }

  public void setSize(int size) {
    this.size = size;
  }

  @Override
  public void putObject(Object key, Object value) {
    cycleKeyList(key);//检测并清理缓存
    delegate.putObject(key, value);//添加缓存项
  }

  @Override
  public Object getObject(Object key) {
    return delegate.getObject(key);
  }

  @Override
  public Object removeObject(Object key) {
    return delegate.removeObject(key);
  }

  @Override
  public void clear() {
    delegate.clear();
    keyList.clear();
  }

  @Override
  public ReadWriteLock getReadWriteLock() {
    return null;
  }
  //检测并清除缓存项
  private void cycleKeyList(Object key) {
    //记录key
    keyList.addLast(key);
    if (keyList.size() > size) {//如果缓存达到上线 清理最老的缓存
      Object oldestKey = keyList.removeFirst();
      delegate.removeObject(oldestKey);
    }
  }

}
