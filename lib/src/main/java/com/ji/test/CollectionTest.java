package com.ji.test;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

public class CollectionTest {
    private static Object getField(Class c, Object o, String f) {
        try {
            Field field = c.getDeclaredField(f);
            field.setAccessible(true);
            return field.get(o);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String args[]) {
        Map<String, String> hashMap = new HashMap<>();
        hashMap.put("d", "d");
        hashMap.put("a", "a");
        hashMap.put("c", "c");
        hashMap.put("b", "b");
        hashMap.put("b", "b");
        hashMap.put("Aa", "Aa");
        hashMap.put("BB", "BB");
        System.out.println("hash a:" + "a".hashCode() % 32 + " 12:" + "12".hashCode() % 32
                + " b:" + "b".hashCode() % 32 + " 13:" + "13".hashCode() % 32
                + " Aa:" + "Aa".hashCode() + " BB:" + "BB".hashCode());
        for (int i = 0; i < 18; i++) {
            hashMap.put(String.valueOf(i), String.valueOf(i));
        }
        System.out.println("hashMap keySet:" + Arrays.toString(hashMap.keySet().toArray()));
        Map.Entry[] table = (Map.Entry[]) getField(HashMap.class, hashMap, "table");
        if (table != null) {
            int length = table.length;
            for (int i = 0; i < length; i++) {
                System.out.println(String.format(Locale.getDefault(), "table[%2d] hash:0x%4x %8s next:%s ",
                        i, table[i] == null ? 0 : (int) getField(table[i].getClass(), table[i], "hash"), table[i], table[i] == null ? "null" : getField(table[i].getClass(), table[i], "next")));
            }
        }

        Map<String, String> treeMap = new TreeMap<>();
        treeMap.put("d", "d");
        treeMap.put("a", "a");
        treeMap.put("c", "c");
        treeMap.put("b", "b");
        System.out.println("treeMap keySet:" + Arrays.toString(treeMap.keySet().toArray()));

        Map<String, String> hashTable = new Hashtable<>();
        hashTable.put("d", "d");
        hashTable.put("a", "a");
        hashTable.put("c", "c");
        hashTable.put("b", "b");
        System.out.println("hashTable keySet:" + Arrays.toString(hashTable.keySet().toArray()));

        Map<String, String> linkedHashMap = new LinkedHashMap<>();
        linkedHashMap.put("d", "d");
        linkedHashMap.put("a", "a");
        linkedHashMap.put("c", "c");
        linkedHashMap.put("b", "b");
        System.out.println("linkedHashMap keySet:" + Arrays.toString(linkedHashMap.keySet().toArray()));
    }
}
