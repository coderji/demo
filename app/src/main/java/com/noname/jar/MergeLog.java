package com.noname.jar;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

/*
 * v1.0
 *   java -jar [debuglogger path]
 *             (see all_log in your debuglogger path)
 *   java -jar [file1 file2 file3 ...]
 *             (see all_log in your current path)
 *
 * v1.1
 *   APLog
 *
 * v1.2
 *   crash_log
  */
public class MergeLog {
    private static final String TAG = "MergeLog";
    private static boolean DEBUG = false;

    private static void mergeFile(String[] files, String outDir) {
        if (DEBUG) log("mergeFile files:" + Arrays.toString(files));
        int length = files.length;
        FileReader[] frs = new FileReader[length];
        BufferedReader[] brs = new BufferedReader[length];
        String[] strings = new String[length];
        String timezone = "----- timezone";

        for (int i = 0; i < length; i++) {
            try {
                frs[i] = new FileReader(files[i]);
                brs[i] = new BufferedReader(frs[i]);
                timezone = brs[i].readLine();
                strings[i] = brs[i].readLine();
            } catch (IOException e) {
                log("mergeFile read fail", e);
            }
        }

        try {
            int allLogIndex = 0;
            int line = 0;
            FileWriter fw = new FileWriter(outDir + File.separator + "all_log_" + allLogIndex + "_" + getTime(strings[0]) + ".txt");
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(timezone);
            while (true) {
                int min = findMin(strings);
                if (min == -1) {
                    break;
                }
                bw.write(strings[min]);
                bw.newLine();
                strings[min] = brs[min].readLine();

                line++;
                if (line >= 500000) {
                    line = 0;
                    bw.flush();
                    bw.close();
                    fw.close();

                    allLogIndex++;
                    fw = new FileWriter(outDir + File.separator + "all_log_" + allLogIndex + "_" + getTime(strings[min]) + ".txt");
                    bw = new BufferedWriter(fw);
                    bw.write(timezone);
                }
            }
            bw.flush();
            bw.close();
            fw.close();
        } catch (IOException e) {
            log("mergeFile write fail", e);
        }

        for (int i = 0; i < length; i++) {
            try {
                brs[i].close();
                frs[i].close();
            } catch (IOException e) {
                log("mergeFile close fail", e);
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

    private static String getTime(String s) {
        // 07-04 13:02:29.039012
        if (DEBUG) log("getTime s:" + s);
        StringBuilder time = new StringBuilder();
        time.append(s.charAt(0));
        time.append(s.charAt(1));
        time.append(s.charAt(3));
        time.append(s.charAt(4));
        time.append('-');
        time.append(s.charAt(6));
        time.append(s.charAt(7));
        time.append(s.charAt(9));
        time.append(s.charAt(10));
        time.append(s.charAt(12));
        time.append(s.charAt(13));
        if (DEBUG) log("getTime time:" + time.toString());
        return time.toString();
    }

    private static int compare(String a, String b) {
        // 07-04 13:02:29.039012
        for (int i = 0; i < 21 /* format date */ && i < a.length() && i < b.length(); i++) {
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

    private static void log(String s, Exception e) {
        log(s);
        e.printStackTrace();
    }

    private static void mergeDir(String dir) {
        if (dir.endsWith("debuglogger")) {
            mergeMobileLog(dir + File.separator + "mobilelog");
        } else if (dir.endsWith("mobilelog")) {
            mergeMobileLog(dir);
        } else if (dir.contains("APLog")) {
            mergeApLog(dir);
        } else {
            log("mergeDir dir unknown");
        }
    }

    private static void mergeMobileLog(String dir) {
        log("mergeMobileLog dir:" + dir);
        File[] apLogs = new File(dir).listFiles();
        if (apLogs != null) {
            for (File apLog : apLogs) {
                if (apLog.isDirectory() && apLog.getName().startsWith("APLog")) {
                    mergeApLog(apLog.toString());
                }
            }
        }
    }

    private static void mergeApLog(String dir) {
        log("mergeApLog dir:" + dir);
        File[] logs = new File(dir).listFiles();
        if (logs != null) {
            ArrayList<String> mergeLogList = new ArrayList<>();
            for (File log : logs) {
                if (DEBUG) log("mergeApLog log:" + log);
                if (needMerge(log.getName())) {
                    mergeLogList.add(log.toString());
                }
            }
            String[] mergeLogs = mergeLogList.toArray(new String[0]);
            mergeFile(mergeLogs, dir);
        }
    }

    private static boolean needMerge(String file) {
        String[] types = new String[]{"events_log", "kernel_log", "main_log", "sys_log", "crash_log"};
        for (String type : types) {
            if (file.startsWith(type)) {
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) {
        long begin = System.currentTimeMillis();
        log("merge.jar version 1.1.20220728");
        log("merge events_log, kernel_log, main_log, sys_log, crash_log to all_log");
        log("Usage: java -jar [debuglogger path]");
        log("                 (see all_log in your debuglogger path)");
        log("       java -jar [file1 file2 file3 ...]");
        log("                 (see all_log in your current dir)");
        log("");
        log("main args:" + Arrays.toString(args));
        String userDir = System.getProperty("user.dir");
        if (args.length == 0) {
            mergeDir(userDir);
        } else if (args.length == 1) {
            mergeDir(args[0]);
        } else {
            mergeFile(args, userDir);
        }
        long end = System.currentTimeMillis();
        log("main done, time:" + (end - begin) / 1000 + "s," + (end - begin) + "ms");
    }
}
