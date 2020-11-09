package com.ji.terminal;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class MergeLog {
    private static final String TAG = "MergeLog";
    private static boolean V = false;

    private static void mergeFile(String path) {
        log("mergeFile path:" + path);
        if (!path.endsWith(File.separator)) {
            path = path + File.separator;
        }
        File directory = new File(path);
        if (directory.exists() && directory.isDirectory()) {
            HashMap<String, String> map = new HashMap<>();
            for (String name : Objects.requireNonNull(directory.list())) {
                if (name.endsWith("-e.txt")) {
                    String key = name.substring(0, name.indexOf("-e.txt"));
                    map.put(key, map.getOrDefault(key, "") + "-e");
                } else if (name.endsWith("-m.txt")) {
                    String key = name.substring(0, name.indexOf("-m.txt"));
                    map.put(key, map.getOrDefault(key, "") + "-m");
                } else if (name.endsWith("-r.txt")) {
                    String key = name.substring(0, name.indexOf("-r.txt"));
                    map.put(key, map.getOrDefault(key, "") + "-r");
                } else if (name.endsWith("-s.txt")) {
                    String key = name.substring(0, name.indexOf("-s.txt"));
                    map.put(key, map.getOrDefault(key, "") + "-s");
                }
            }
            for (String key : map.keySet()) {
                mergeFile(path, key, Objects.requireNonNull(map.getOrDefault(key, "")));
            }
        } else {
            log("mergeFile get directory fail");
        }
    }

    private static void mergeFile(String path, String name, String attr) {
        String[] attrs = attr.substring(1).split("-");
        log("mergeFile name:" + name + " attrs:" + Arrays.toString(attrs));
        FileReader[] frs = new FileReader[attrs.length];
        BufferedReader[] brs = new BufferedReader[attrs.length];
        String[] strings = new String[attrs.length];
        int[] attrLines = new int[attrs.length];
        int mergeLine = 0;
        try {
            int minIndex = 0;
            for (int i = 0; i < attrs.length; i++) {
                frs[i] = new FileReader(path + name + "-" + attrs[i] + ".txt");
                brs[i] = new BufferedReader(frs[i]);
                if (brs[i].ready()) {
                    strings[i] = brs[i].readLine();
                    attrLines[i] = 1;
                    if (i != minIndex && compare(strings[i], strings[minIndex]) < 0) {
                        minIndex = i;
                    }
                } else {
                    log("mergeFile " + attrs[i] + " not ready");
                    brs[i].close();
                    frs[i].close();
                    attrLines[i] = 0;
                }
            }
            FileWriter fw = new FileWriter(path + name + "-all.txt");
            BufferedWriter bw = new BufferedWriter(fw);

            while (true) {
                for (int i = 0; i < attrs.length; i++) {
                    if (i != minIndex && strings[i] != null && compare(strings[i], strings[minIndex]) < 0) {
                        minIndex = i;
                    }
                }
                if (V) log("mergeFile write " + attrs[minIndex] + " " + attrLines[minIndex]);
                bw.write(strings[minIndex]);
                bw.newLine();
                mergeLine++;
                if (brs[minIndex].ready()) {
                    strings[minIndex] = brs[minIndex].readLine();
                    attrLines[minIndex]++;
                } else {
                    log("mergeFile " + attrs[minIndex] + " done");
                    strings[minIndex] = null;
                    brs[minIndex].close();
                    frs[minIndex].close();
                    for (int i = 0; i < attrs.length; i++) {
                        if (strings[i] != null) {
                            minIndex = i;
                        }
                    }
                    if (strings[minIndex] == null) {
                        break;
                    }
                    for (int i = 0; i < attrs.length; i++) {
                        if (i != minIndex && strings[i] != null && compare(strings[i], strings[minIndex]) < 0) {
                            minIndex = i;
                        }
                    }
                }
            }
            bw.flush();
            bw.close();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        int allLine = 0;
        StringBuilder stringBuilder = new StringBuilder("mergeFile ");
        for (int i = 0; i < attrs.length; i++) {
            allLine = allLine + attrLines[i];
            stringBuilder.append(attrs[i]).append(":").append(attrLines[i]).append(" ");
        }
        stringBuilder.append("allLine:").append(allLine).append(" mergeLine:").append(mergeLine);
        log(stringBuilder.toString());
    }

    private static int compare(String a, String b) {
        for (int i = 0; i < 18 /* format date */ && i < a.length() && i < b.length(); i++) {
            if (a.charAt(i) > b.charAt(i)) {
                return 1;
            } else if (a.charAt(i) < b.charAt(i)) {
                return  -1;
            }
        }
        return 0;
    }

    private static void log(String s) {
        System.out.println(new SimpleDateFormat("MM-dd HH:mm:ss.SSS", Locale.getDefault()).format(new Date())
                + " " + TAG + ": " + s);
    }

    public static int main(String[] args) {
        if (args == null || args.length != 1) {
            System.out.println("Usage: java -jar app.jar [directory]");
        } else {
            long begin = System.currentTimeMillis();
            mergeFile(args[0]);
            long end = System.currentTimeMillis();
            log("mergeFile time:" + (end - begin) / 1000);
        }
        return 0;
    }
}
