package com.ji.test;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

public class LeakTest {
    private static ReferenceQueue<byte[]> rq = new ReferenceQueue<>();
    private static final int _1M = 1024 * 1024;

    public static void main(String[] args) {
        Object value = new Object();
        Map<Object, Object> map = new HashMap<>();
        Thread thread = new Thread(() -> {
            try {
                int cnt = 0;
                WeakReference k;
                while ((k = (WeakReference) rq.remove()) != null) {
                    System.out.println((cnt++) + "回收了:" + k);
                }
            } catch (InterruptedException e) {
                // 结束循环
                e.printStackTrace();
            }
        });
        thread.setDaemon(true);
        thread.start();

        for (int i = 0; i < 80; i++) {
            byte[] bytes = new byte[_1M];
            // 注意构造弱引用时传入rq
            WeakReference<byte[]> weakReference = new WeakReference<>(bytes, rq);
            map.put(weakReference, value);
        }
        System.out.println("map.size->" + map.size());
    }
}
