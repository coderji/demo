package com.ji.algorithm;

public class Tree {
    private static class Node {
        public Node left, right;
        public int data;

        public void firstTraversals() {
            System.out.print(data + " ");
            if (left != null) {
                left.firstTraversals();
            }
            if (right != null) {
                right.firstTraversals();
            }
        }

        public void middleTraversals() {
            if (left != null) {
                left.middleTraversals();
            }
            System.out.print(data + " ");
            if (right != null) {
                right.middleTraversals();
            }
        }

        public void lastTraversals() {
            if (left != null) {
                left.lastTraversals();
            }
            if (right != null) {
                right.lastTraversals();
            }
            System.out.print(data + " ");
        }
    }

    public static void main(String[] args) {
        /*
              1
             / \
            2   3
           /\   /\
          4  5 6  7
         /\   \
        8  9  10
        */
        Node node = new Node();
        node.data = 1;

        node.left = new Node();
        node.left.data = 2;
        node.right = new Node();
        node.right.data = 3;

        node.left.left = new Node();
        node.left.left.data = 4;
        node.left.right = new Node();
        node.left.right.data = 5;
        node.right.left = new Node();
        node.right.left.data = 6;
        node.right.right = new Node();
        node.right.right.data = 7;

        node.left.left.left = new Node();
        node.left.left.left.data = 8;
        node.left.left.right = new Node();
        node.left.left.right.data = 9;
        node.left.right.right = new Node();
        node.left.right.right.data = 10;

        System.out.print("firstTraversals ");
        node.firstTraversals();
        System.out.println();
        System.out.print("middleTraversals ");
        node.middleTraversals();
        System.out.println();
        System.out.print("lastTraversals ");
        node.lastTraversals();
        System.out.println();
    }
}
