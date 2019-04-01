package com.ji.lib;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Stack;

public class Queue {
    private static class StackQueue<E> {
        private Stack<E> stack = new Stack<>();
        private Stack<E> helpStack = new Stack<>();

        void push(E e) {
            stack.push(e);
        }

        E pop() {
            E result = null;
            helpStack.clear();
            while (!stack.empty()) {
                result = helpStack.push(stack.pop());
            }
            helpStack.pop();
            while (!helpStack.empty()) {
                stack.push(helpStack.pop());
            }
            return result;
        }

        boolean empty() {
            return stack.empty();
        }
    }

    public static void main(String[] args) {
        int[] array = new int[10];
        for (int i = 0; i < 10; i++) {
            array[i] = 1 + (int) (Math.random() * 50);
        }
        System.out.println("Array " + Arrays.toString(array));

        ArrayList<Integer> arrayList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            arrayList.add(array[i]);
        }
        arrayList.remove(0);
        for (int i = 3; i < 7; i++) {
            arrayList.add(array[i]);
        }
        arrayList.remove(0);
        arrayList.remove(0);
        for (int i = 7; i < 10; i++) {
            arrayList.add(array[i]);
        }
        arrayList.remove(0);
        System.out.println("ArrayList " + arrayList);

        StackQueue<Integer> stackQueue = new StackQueue<>();
        for (int i = 0; i < 3; i++) {
            stackQueue.push(array[i]);
        }
        stackQueue.pop();
        for (int i = 3; i < 7; i++) {
            stackQueue.push(array[i]);
        }
        stackQueue.pop();
        stackQueue.pop();
        for (int i = 7; i < 10; i++) {
            stackQueue.push(array[i]);
        }
        stackQueue.pop();
        System.out.print("StackQueue [");
        while (!stackQueue.empty()) {
            Integer integer = stackQueue.pop();
            System.out.print(integer);
            if (!stackQueue.empty()) {
                System.out.print(", ");
            }
        }
        System.out.println("]");
    }
}
