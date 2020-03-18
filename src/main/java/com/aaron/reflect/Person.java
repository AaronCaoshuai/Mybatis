package com.aaron.reflect;

import java.io.Serializable;

@SuppressWarnings("all")
public class Person implements Serializable {
    public String username;
    private String password;

    public Person() {
    }

    private int age;

    public Person(String username, String password,int age) {
        this.username = username;
        this.password = password;
        this.age = age;
    }

    private Person( String password,int age) {
        this.password = password;
        this.age = age;
    }

    private void action(){
        System.out.println("action");
    }

    public void run(){
        System.out.println("run");
    }
}
