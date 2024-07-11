package com.java;

import java.util.ArrayList;

/**
 * Created by Mr.Chan
 * Time 2024-06-20
 * Blog https://www.cnblogs.com/Frank-dev-blog/
 */

class A{}
class B extends A{}
interface C{}
interface D{}

class E <T extends B & C & D> {
    T t;
}

public class Generics<T> {
    private T t;

    public <K> void print(K t) {
        System.out.println(t);
    }

    interface IGenerics<T> {
        T getT();
    }
}

class Test {
    ArrayList<String> list = new ArrayList<>();
    ArrayList<Integer> list1 = new ArrayList<>();

    public void forArray(ArrayList<?> list) {
        for (Object o : list) {
            System.out.println(o);
        }
    }

    public <T> void forArray0(ArrayList<T> list) {
        for (Object o : list) {
            System.out.println(o);
        }
    }


    public void forArray1(ArrayList<? extends Number> list) {
        for (Number o : list) {
            System.out.println(o);
        }
    }

    public void forArray2(ArrayList<? super Number> list) {
        for (Object o : list) {
            System.out.println(o);
        }
    }
    public static void main(String[] args) {

    }

}
