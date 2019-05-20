package com.ji.test;

import java.util.Comparator;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

public class BlockingQueueTest {
    public static void main(String[] args) {
        BlockingQueue<Integer> blockingQueue = new LinkedBlockingQueue<>(3);
        for (int i = 0; i < 5; i++) {
            boolean success = blockingQueue.offer(i);
            System.out.println("LinkedBlockingQueue offer:" + i + (success ? " success" : " fail"));
        }
        System.out.println("LinkedBlockingQueue blockingQueue:" + blockingQueue);
        while (true) {
            Integer integer = blockingQueue.poll();
            if (integer != null) {
                System.out.println("LinkedBlockingQueue poll:" + integer);
            } else {
                break;
            }
        }

        blockingQueue = new ArrayBlockingQueue<>(3);
        for (int i = 0; i < 5; i++) {
            boolean success = blockingQueue.offer(i);
            System.out.println("ArrayBlockingQueue offer:" + i + (success ? " success" : " fail"));
        }
        System.out.println("ArrayBlockingQueue blockingQueue:" + blockingQueue);
        while (true) {
            Integer integer = blockingQueue.poll();
            if (integer != null) {
                System.out.println("ArrayBlockingQueue poll:" + integer);
            } else {
                break;
            }
        }

        blockingQueue = new PriorityBlockingQueue<>(3, new Comparator<Integer>() {
            @Override
            public int compare(Integer t1, Integer t2) {
                return t1 - t2;
            }
        });
        for (int i = 0; i < 5; i++) {
            boolean success = blockingQueue.offer(i);
            System.out.println("PriorityBlockingQueue offer:" + i + (success ? " success" : " fail"));
        }
        System.out.println("PriorityBlockingQueue blockingQueue:" + blockingQueue);
        while (true) {
            Integer integer = blockingQueue.poll();
            if (integer != null) {
                System.out.println("PriorityBlockingQueue poll:" + integer);
            } else {
                break;
            }
        }
    }
}
