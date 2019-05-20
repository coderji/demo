package com.ji.test;

public class OverrideTest {
    public static class Obj {
        public static int i = 0;

        public static int getI() {
            return i;
        }
    }

    public static class ObjChild extends Obj {
        public static int i = 1;

        // @Override // error
        // public static int getI() {
        //     return i;
        // }
    }

    public static void main(String args[]) {
        System.out.println("Obj.getI:" + Obj.getI() + " Obj.i:" + Obj.i);
        System.out.println("ObjChild.getI:" + ObjChild.getI() + " ObjChild.i:" + ObjChild.i);
    }
}
