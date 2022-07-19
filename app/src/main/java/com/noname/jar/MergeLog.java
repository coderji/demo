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

public class MergeLog {
    private static final String TAG = "MergeLog";
    private static boolean DEBUG = false;

    private static void mergeFile(String[] files, String outDir) {
        log("mergeFile files:" + Arrays.toString(files));
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
            FileWriter fw = new FileWriter(outDir + File.separator + "all_log_" + allLogIndex + ".txt");
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
                    fw = new FileWriter(outDir + File.separator + "all_log_" + allLogIndex + ".txt");
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

    private static int compare(String a, String b) {
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
        log("mergeDir dir:" + dir);
        File[] apLogs = new File(dir).listFiles();
        if (apLogs != null) {
            for (File apLog : apLogs) {
                if (apLog.isDirectory() && apLog.getName().startsWith("APLog")) {
                    log("mergeDir ap:" + apLog);
                    File[] logs = apLog.listFiles();
                    if (logs != null) {
                        ArrayList<String> mergeLogList = new ArrayList<>();
                        for (File log : logs) {
                            if (DEBUG) log("mergeDir log:" + log);
                            if (needMerge(log.getName())) {
                                mergeLogList.add(log.toString());
                            }
                        }
                        String[] mergeLogs = mergeLogList.toArray(new String[0]);
                        mergeFile(mergeLogs, apLog.toString());
                    }
                }
            }
        }
    }

    private static boolean needMerge(String file) {
        String[] types = new String[]{"events_log", "kernel_log", "main_log", "sys_log"};
        for (String type : types) {
            if (file.startsWith(type)) {
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) {
        long begin = System.currentTimeMillis();
        log("merge.jar version 1.0.20220701");
        log("merge events_log, kernel_log, main_log, sys_log to all_log");
        log("Usage: java -jar [debuglogger path]");
        log("                 (debuglogger absolute path)");
        log("       java -jar");
        log("                 (move merge.jar to debuglogger, cd debuglogger, java -jar)");
        log("");
        log("main args:" + Arrays.toString(args));
        String userDir = System.getProperty("user.dir");
        if (args.length == 0) {
            if (userDir == null) {
                log("main get user.dir fail");
            } else if (userDir.endsWith("debuglogger")) {
                mergeDir(userDir + File.separator + "mobilelog");
            } else if (userDir.endsWith("mobilelog")) {
                mergeDir(userDir);
            } else {
                log("main userDir:" + userDir + ", see Usage");
            }
        } else if (args.length == 1) {
            userDir = args[0];
            if (userDir == null) {
                log("main get user.dir fail");
            } else if (userDir.endsWith("debuglogger")) {
                mergeDir(userDir + File.separator + "mobilelog");
            } else if (userDir.endsWith("mobilelog")) {
                mergeDir(userDir);
            } else {
                log("main userDir:" + userDir + ", see Usage");
            }
        } else {
            mergeFile(args, userDir);
        }
        long end = System.currentTimeMillis();
        log("main done, time:" + (end - begin) / 1000 + "s," + (end - begin) + "ms");
    }
}
