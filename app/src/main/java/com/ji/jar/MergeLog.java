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

    private static void mergeFile(String[] files) {
        log("mergeFile files:" + Arrays.toString(files));
        int length = files.length;
        FileReader[] frs = new FileReader[length];
        BufferedReader[] brs = new BufferedReader[length];
        String[] strings = new String[length];

        for (int i = 0; i < length; i++) {
            try {
                frs[i] = new FileReader(files[i]);
                brs[i] = new BufferedReader(frs[i]);
                strings[i] = brs[i].readLine();
            } catch (IOException e) {
                log("mergeFile read fail, " + e);
            }
        }

        try {
            FileWriter fw = new FileWriter("mergeLog.txt");
            BufferedWriter bw = new BufferedWriter(fw);
            while (true) {
                int min = findMin(strings);
                if (min == -1) {
                    break;
                }
                bw.write(strings[min]);
                bw.newLine();
                bw.flush();
                strings[min] = brs[min].readLine();
            }
            bw.close();
            fw.close();
        } catch (IOException e) {
            log("mergeFile write fail" + e);
        }

        for (int i = 0; i < length; i++) {
            try {
                brs[i].close();
                frs[i].close();
            } catch (IOException e) {
                log("mergeFile close fail, " + e);
            }
        }
    }

    private static int findMin(String[] strings) {
        int length = strings.length;
        int min = -1;
        for (int i = 0; i < length; i++) {
            if (strings[i] != null) {
                min = i;
                break;
            }
        }
        if (min >= 0) {
            for (int i = min + 1; i < length; i++) {
                if (strings[i] != null && compare(strings[min], strings[i]) > 0) {
                    min = i;
                }
            }
        }
        return min;
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
