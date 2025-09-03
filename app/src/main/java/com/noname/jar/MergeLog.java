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
    private static final boolean DEBUG = false;

    private static void mergeFile(String[] files, String dir) {
        int length = files.length;
        FileReader[] frs = new FileReader[length];
        BufferedReader[] brs = new BufferedReader[length];
        String[] strings = new String[length];
        String head;

        for (int i = 0; i < length; i++) {
            try {
                frs[i] = new FileReader(files[i]);
                brs[i] = new BufferedReader(frs[i]);
                do {
                    head = brs[i].readLine();
                } while (head != null && !containTime(head));
                strings[i] = head;
            } catch (IOException e) {
                log("mergeFile read fail", e);
            }
        }

        try {
            int line = 0;
            int fileIndex = 0;
            String fileIndexString = "000";
            String time = strings[0] != null ? strings[0] : (strings[1] != null ? strings[1] : strings[2]);
            FileWriter fw = new FileWriter(dir + File.separator + "log_" + fileIndexString + "_" + formatTime(time) + ".txt");
            BufferedWriter bw = new BufferedWriter(fw);
            while (true) {
                int min = findMin(strings);
                if (min == -1) {
                    break;
                }
                bw.write(strings[min]);
                bw.newLine();
                while (true) {
                    strings[min] = brs[min].readLine();
                    if (containTime(strings[min]) || strings[min] == null) {
                        break;
                    } else {
                        if (DEBUG) log("mergeFile " + files[min] + " skip " + strings[min]);
                    }
                }

                line++;
                if (line >= 500000) {
                    line = 0;
                    bw.flush();
                    bw.close();
                    fw.close();

                    fileIndex++;
                    if (fileIndex < 10) {
                        fileIndexString = "00" + fileIndex;
                    } else if (fileIndex < 100) {
                        fileIndexString = "0" + fileIndex;
                    } else {
                        fileIndexString = String.valueOf(fileIndex);
                    }

                    fw = new FileWriter(dir + File.separator + "log_" + fileIndexString + "_" + formatTime(strings[min]) + ".txt");
                    bw = new BufferedWriter(fw);
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

    private static boolean containTime(String s) {
        String format = "07-04 13:02:29.906803";
        return s != null && s.length() > format.length()
                && s.charAt(0) >= '0' && s.charAt(0) <= '9'
                && s.charAt(1) >= '0' && s.charAt(2) <= '9'
                && s.charAt(2) == '-' && s.charAt(5) == ' ';
    }

    private static int compare(String a, String b) {
        String format = "07-04 13:02:29.906803";
        for (int i = 0; i < format.length() && i < a.length() && i < b.length(); i++) {
            if (a.charAt(i) > b.charAt(i)) {
                return 1;
            } else if (a.charAt(i) < b.charAt(i)) {
                return -1;
            }
        }
        return 0;
    }

    private static String formatTime(String start) {
        return start.substring(0, 14)
                .replace(" ", "")
                .replace("-", "")
                .replace(":", "");
    }

    private static void log(String s) {
        System.out.println(new SimpleDateFormat("MM-dd HH:mm:ss.SSS", Locale.getDefault()).format(new Date()) + " " + TAG + ": " + s);
    }

    private static void log(String s, Exception e) {
        log(s);
        e.printStackTrace();
    }

    private static void mergeDir(String dir) {
        File[] logs = new File(dir).listFiles();
        if (logs != null) {
            ArrayList<String> mergeLogList = new ArrayList<>();
            File bugreport = null;
            for (File log : logs) {
                if (needMerge(log.getName())) {
                    mergeLogList.add(log.toString());
                } else if (log.getName().contains("bugreport")) {
                    bugreport = log;
                }
            }
            if (mergeLogList.size() > 0) {
                log("mergeDir:" + mergeLogList);
                String[] mergeLogs = mergeLogList.toArray(new String[0]);
                mergeFile(mergeLogs, dir);
            } else if (bugreport != null) {
                log("mergeDir bugreport");
                try {
                    FileWriter fw = new FileWriter(dir + "\\merge.bat");
                    BufferedWriter bw = new BufferedWriter(fw);
                    bw.write("mkdir merge");
                    bw.newLine();
                    bw.write("sed -n '/------ EVENT LOG .*/,/duration.*EVENT LOG/p' " + bugreport + " > " + dir + "\\merge\\events_log.txt");
                    bw.newLine();
                    bw.write("sed -n '/------ KERNEL LOG .*/,/duration.*KERNEL LOG/p' " + bugreport + " > " + dir + "\\merge\\kernel_log.txt");
                    bw.newLine();
                    bw.write("sed -n '/------ SYSTEM LOG .*/,/duration.*SYSTEM LOG/p' " + bugreport + " > " + dir + "\\merge\\sys_log.txt");
                    bw.newLine();
                    bw.flush();
                    bw.close();
                    fw.close();
                } catch (Exception e) {
                    log("mergeDir", e);
                }
            }
        }
    }

    private static boolean needMerge(String file) {
        String[] types = new String[]{
                "crash", "events", "main", "kernel", "radio", "sys",
                "-c", "-e", "-m", "-k", "-r", "-s",
                "ANDROID_LOG", "EVENT_LOG", "RADIO_LOG",
        };
        for (String type : types) {
            if (file.contains(type)) {
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) {
        log("version 20250903");
        log("args:" + Arrays.toString(args));
        long begin = System.currentTimeMillis();
        if (args.length == 1) {
            mergeDir(args[0]);
        }
        long end = System.currentTimeMillis();
        log("done " + (end - begin) / 1000 + "s" + (end - begin) % 1000 + "ms");
    }
}
