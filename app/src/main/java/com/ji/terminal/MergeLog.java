package com.ji.terminal;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class MergeLog {
    private static final String TAG = "MergeLog";
    private static boolean V = false;
    private static String mPath;
    private static volatile String[] mLines;
    private static Object[] mLocks;

    private static void mergeFile(String path) {
        log("mergeFile path:" + path);
        mPath = path + (path.endsWith(File.separator) ? "" : File.separator);
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
                mergeFile(key, Objects.requireNonNull(map.getOrDefault(key, "")));
            }
        } else {
            log("mergeFile get directory fail");
        }
    }

    private static void mergeFile(String name, String attr) {
        String[] attrs = attr.substring(1).split("-");
        log("mergeFile name:" + name + " attrs:" + Arrays.toString(attrs));
        mLines = new String[attrs.length];
        mLocks = new Object[attrs.length];
        for (int i = 0; i < attrs.length; i++) {
            mLines[i] = "begin";
            mLocks[i] = new Object();
        }
        for (int i = 0; i < attrs.length; i++) {
            new Thread(new FileRunnable(i, name, attrs[i])).start();
        }
        try {
            FileWriter fw = new FileWriter(mPath + name + "-all.txt");
            BufferedWriter bw = new BufferedWriter(fw);
            while (true) {
                if (!merge(bw)) break;
            }
            bw.flush();
            bw.close();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class FileRunnable implements Runnable {
        int index;
        String name;
        String attr;

        public FileRunnable(int index, String name, String attr) {
            this.index = index;
            this.name = name;
            this.attr = attr;
        }

        @Override
        public void run() {
            log("[" + index + "] run");
            try {
                FileReader fr = new FileReader(mPath + name + "-" + attr + ".txt");
                BufferedReader br = new BufferedReader(fr);
                int line = 0;
                while (br.ready()) {
                    line++;
                    mLines[index] = br.readLine();
                    if (V) log("[" + index + "] line" + line + " " + mLines[index]);

                    // wait for compare and merge
                    synchronized (mLocks[index]) {
                        if (V) log("[" + index + "] wait");
                        try {
                            mLocks[index].wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                mLines[index] = "end";
                br.close();
                fr.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static synchronized boolean merge(BufferedWriter bw) throws IOException {
        int begin = 0, end = 0, index = 0, number = 0;
        for (int i = 0; i < mLines.length; i++) {
            if ("begin".equals(mLines[i])) {
                begin++;
            } else if ("end".equals(mLines[i])) {
                end++;
            } else {
                number++;
                index = i;
            }
        }
        if (V) log("merge all:" + mLines.length + " begin:" + begin + " end:" + end + " number:"+ number + " index:" + index);
        if (begin > 0) {
            if (V) log("merge wait begin");
        } else if (end == mLocks.length) {
            log("merge done");
            return false;
        } else {
            if (number > 1) {
                for (int i = index - 1; i >= 0; i--) {
                    if (!"begin".equals(mLines[i]) && !"end".equals(mLines[i]) && compare(mLines[i], mLines[index]) < 0) {
                        index = i;
                    }
                }
            }
            if (V) log("merge write [" + index + "] " + mLines[index]);
            bw.write(mLines[index]);
            bw.newLine();
            synchronized (mLocks[index]) {
                if (V) log("merge notify [" + index + "]");
                mLocks[index].notify();
            }
        }
        return true;
    }

    private static int compare(String a, String b) {
        if (V) log("compare \n\t\ta:" + a + "\n\t\tb:" + b);
        for (int i = 0; i < 18 /* format date */ && i < a.length() && i < b.length(); i++) {
            if (a.charAt(i) > b.charAt(i)) {
                return 1;
            } else if (a.charAt(i) < b.charAt(i)) {
                return -1;
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
