package com.ji.algorithm;

import java.util.Arrays;

public class Sort {
    private static void quickSort(int[] array, int left, int right) {
        if (left < right) {
            int partition = getPartition(array, left, right);
            System.out.println(Arrays.toString(array));
            quickSort(array, left, partition - 1);
            quickSort(array, partition + 1, right);
        }
    }

    private static int getPartition(int[] array, int left, int right) {
        int x = array[left];
        for (int index = left + 1; index <= right; index++) {
            if (x > array[index]) {
                swap(array, left, index);
                System.out.println("swap x:" + x + " " + Arrays.toString(array));
                left = index;
            }
        }
        return left;
    }

    private static void swap(int[] array, int left, int right) {
        int x = array[left];
        array[left] = array[right];
        array[right] = x;
    }

    public static void main(String[] args) {
        int[] array = new int[]{2, 1, 4, 3, 5, 9, 6, 8, 0};
        quickSort(array, 0, array.length - 1);
        System.out.println(Arrays.toString(array));
    }
}
