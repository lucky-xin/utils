package com.xin.utils.test;

import java.util.Arrays;

/**
 * @author Luchaoxin
 * @version V1.0
 * @Description: sss(用一句话描述该类做什么)
 * @date 2018-07-26 11:41
 * @Copyright (C)2017 , Luchaoxin
 */
public class A {
    static class Human {
        public void sayHi(Human human) {
            System.out.println("Hi,Human! " + human);
        }

        @Override
        public String toString() {
            return "Human";
        }
    }

    static class Man extends Human {
        @Override
        public void sayHi(Human human) {
            System.out.println("Hi,Man! " + human);
        }

        @Override
        public String toString() {
            return "Man";
        }
    }

    static class Woman extends Human {
        @Override
        public void sayHi(Human human) {
            System.out.println("Hi,Woman! " + human);
        }

        @Override
        public String toString() {
            return "Woman";
        }
    }

    public void sayHello(Human human) {
        System.out.println("Hello,guys!");
    }

    public void sayHello(Man man) {
        System.out.println("Hello,gentleman!");
    }

    public void sayHello(Woman woman) {
        System.out.println("Hello,lady!");
    }

    public static void main(String[] args) {
        Human man = new Man();
        Human woman = new Woman();
        A a = new A();
        a.sayHello(man);
        a.sayHello(woman);

        System.out.println("-----------");
        man.sayHi(man);
        woman.sayHi(woman);

        Man man1 = (Man) man;
        System.out.println(man1);

        byte b = 127;
        Byte by = Byte.valueOf((byte) 0);
        System.out.println(by);

        Byte cache[] = new Byte[-(-128) + 127 + 1];
        for (int i = 0; i < cache.length; i++)
            cache[i] = new Byte((byte) (i - 128));
        System.out.println(cache.length);
        System.out.println(Arrays.toString(cache));
        System.out.println(cache[200]);

        Byte a1 = 100;
        Byte a2 = new Byte((byte) 100);
        Byte a3 = 100;
        System.out.println(a1 == a2);
        System.out.println(a1 == a3);
    }
}
