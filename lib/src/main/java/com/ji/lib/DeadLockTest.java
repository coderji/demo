package com.ji.lib;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class DeadLockTest {
    private static Lock aLock = new ReentrantLock();
    private static Lock bLock = new ReentrantLock();

    private static Runnable aRunnable = new Runnable() {
        @Override
        public void run() {
            System.out.println("aRunnable run begin");
            aLock.lock();
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            bLock.lock();
            bLock.unlock();
            aLock.unlock();
            System.out.println("aRunnable run end");
        }
    };

    private static Runnable bRunnable = new Runnable() {
        @Override
        public void run() {
            System.out.println("bRunnable run begin");
            bLock.lock();
            aLock.lock();
            aLock.unlock();
            bLock.unlock();
            System.out.println("bRunnable run end");
        }
    };

    public static void main(String[] args) {
        new Thread(aRunnable).start();
        new Thread(bRunnable).start();
    }
}
