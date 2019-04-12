package com.ji.lib;

import java.util.Arrays;

public class SortTest {
    // quickSort
    private static void quickSort(int[] array, int partition, int right) {
        if (partition < right) {
            int newPartition = getNewPartition(array, partition, right);
            quickSort(array, partition, newPartition - 1);
            quickSort(array, newPartition + 1, right);
        }
    }

    private static int getNewPartition(int[] array, int partition, int right) {
        int x = array[partition];
        for (int index = partition + 1; index <= right; index++) {
            if (array[index] < x) {
                swap(array, partition, index);
                partition = partition + 1;
            }
        }
        return partition;
    }

    private static void swap(int[] array, int partition, int right) {
        int x = array[right];
        System.arraycopy(array, partition, array, partition + 1, right - partition);
        array[partition] = x;
    }

    public static void main(String[] args) {
        int[] array = new int[10];
        for (int count = 0; count < 8; count++) {
            for (int i = 0; i < 10; i++) {
                array[i] = 1 + (int) (Math.random() * 50);
            }
            System.out.print(Arrays.toString(array));

            quickSort(array, 0, array.length - 1);
            String quickSortArray = Arrays.toString(array);
            System.out.print(" -> " + quickSortArray);

            Arrays.sort(array);
            String sortArray = Arrays.toString(array);
            if (quickSortArray.equals(sortArray)) {
                System.out.print(" OK");
            } else {
                System.err.print(" ERROR");
            }
            System.out.println();
        }

    }
}
