package com.ji.lib;

import java.util.concurrent.atomic.AtomicInteger;

public class Atomic {
    private static volatile int atomicFail = 0; // volatile 不能解决atomicFail++的非原子性
    private static final int MAX = 1000 * 1000;
    private static AtomicInteger atomicInteger = new AtomicInteger(0);

    public static void main(String[] args) {
        new Thread(() -> {
            for (int i = 0; i < MAX; i++) {
                atomicFail++;
                atomicInteger.getAndIncrement();
                System.out.println("thread1 atomicFail:" + atomicFail + " atomicInteger:" + atomicInteger.get());
            }
        }).start();
        new Thread(() -> {
            for (int i = 0; i < MAX; i++) {
                atomicFail++;
                atomicInteger.getAndIncrement();
                System.out.println("thread2 atomicFail:" + atomicFail + " atomicInteger:" + atomicInteger.get());
            }
        }).start();
    }
}
