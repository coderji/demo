package com.ji.lib;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadPoolTest {
    private static AtomicInteger runCount = new AtomicInteger();

    private static class NamedRunnable implements Runnable {
        public String name;

        public NamedRunnable(String name) {
            this.name = name;
        }

        @Override
        public void run() {
            System.out.println(new SimpleDateFormat("MM-dd HH:mm:ss.SSS", Locale.getDefault()).format(new Date())
                    + " " + Thread.currentThread().getName()
                    + " > " + name);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(new SimpleDateFormat("MM-dd HH:mm:ss.SSS", Locale.getDefault()).format(new Date())
                    + " " + Thread.currentThread().getName()
                    + " < " + name);
            runCount.incrementAndGet();
        }
    }

    private static class Rejected implements RejectedExecutionHandler {

        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            System.out.println(new SimpleDateFormat("MM-dd HH:mm:ss.SSS", Locale.getDefault()).format(new Date())
                    + " " + Thread.currentThread().getName()
                    + " rejectedExecution " + ((NamedRunnable) r).name);
        }
    }

    public static void main(String[] args) {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                2, 10, 60, TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>(15), new Rejected());

        for (int i = 0; i < 30; i++) {
            threadPoolExecutor.execute(new NamedRunnable(String.valueOf(i)));
        }

        try {
            Thread.sleep(20000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(new SimpleDateFormat("MM-dd HH:mm:ss.SSS", Locale.getDefault()).format(new Date())
                + " runCount:" + runCount.get());
    }
}
