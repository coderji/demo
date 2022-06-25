package com.ji.jar;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

public class MergeLog {
    private static final String TAG = "MergeLog";
    private static boolean DEBUG = false;

    private static void mergeFile(String[] paths) {
        log("mergeFile paths:" + Arrays.toString(paths));
        int length = paths.length;
        FileReader[] frs = new FileReader[length];
        BufferedReader[] brs = new BufferedReader[length];
        String[] strings = new String[length];
        int[] lines = new int[length];
        int mergeLine = 0;
        try {
            int minIndex = 0;
            for (int i = 0; i < length; i++) {
                frs[i] = new FileReader(paths[i]);
                brs[i] = new BufferedReader(frs[i]);
                if (brs[i].ready()) {
                    strings[i] = brs[i].readLine();
                    lines[i] = 1;
                    if (i != minIndex && compare(strings[i], strings[minIndex]) < 0) {
                        minIndex = i;
                    }
                } else {
                    log("mergeFile " + paths[i] + " not ready");
                    brs[i].close();
                    frs[i].close();
                    lines[i] = 0;
                }
            }
            FileWriter fw = new FileWriter("mergeLog.txt");
            BufferedWriter bw = new BufferedWriter(fw);

            while (true) {
                if (DEBUG) log("mergeFile write minIndex:" + minIndex + " line:" + lines[minIndex] + " string:" + strings[minIndex]);
                bw.write(strings[minIndex]);
                bw.newLine();
                mergeLine++;
                if (brs[minIndex].ready()) {
                    strings[minIndex] = brs[minIndex].readLine();
                    attrLines[minIndex]++;
                    for (int i = 0; i < attrs.length; i++) {
                        if (i != minIndex && strings[i] != null && compare(strings[i], strings[minIndex]) < 0) {
                            minIndex = i;
                        }
                    }
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
        System.out.println(new SimpleDateFormat("MM-dd HH:mm:ss.SSS", Locale.getDefault()).format(new Date()) + " " + TAG + ": " + s);
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: java -jar mergeLog.jar file1 file2 ...");
        } else {
            long begin = System.currentTimeMillis();
            mergeFile(args);
            long end = System.currentTimeMillis();
            log("mergeFile time:" + (end - begin) / 1000);
        }
    }
}
