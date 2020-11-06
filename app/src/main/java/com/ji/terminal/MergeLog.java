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
    private static FileWriter mFw;
    private static BufferedWriter mBw;
    private static char[] FILTER = new char[]{'c', 'e', 'k', 'm', 'r', 's'};

    private static void mergeFile(String path, int attributes) {
        if (V) log("mergeFile path:" + path + " attributes:" + Integer.toBinaryString(attributes));
        mPath = path + (path.endsWith(File.separator) ? "" : File.separator);

        File directory = new File(path);
        if (directory.exists() && directory.isDirectory()) {
            HashMap<String, Integer> map = new HashMap<>();
            for (String name : Objects.requireNonNull(directory.list())) {
                if (name.contains("-e.txt")) {
                    String key = name.substring(0, name.indexOf("-e.txt"));
                    Integer attr = map.get(key);
                    map.put(key, attr == null ? _E : attr | _E);
                } else if (name.contains("-m.txt")) {
                    String key = name.substring(0, name.indexOf("-m.txt"));
                    Integer attr = map.get(key);
                    map.put(key, attr == null ? _M : attr | _M);
                } else if (name.contains("-s.txt")) {
                    String key = name.substring(0, name.indexOf("-s.txt"));
                    Integer attr = map.get(key);
                    map.put(key, attr == null ? _S : attr | _S);
                }
            }
            for (String key : map.keySet()) {
                Integer attr = map.get(key);
                if (attr != null && (attr & _E) != 0 && (attr & _M) != 0 && (attr & _S) != 0) {
                    mergeFile(key, new String[]{key + "-e.txt", key + "-m.txt", key + "-s.txt"});
                }
            }
        }
    }

    private static void mergeFile(String name, String[] names) {
        if (V) log("mergeFile names:" + Arrays.toString(names));
        try {
            mFw = new FileWriter(mPath + name + "-all.txt");
            mBw = new BufferedWriter(mFw);
        } catch (IOException e) {
            e.printStackTrace();
        }

        mLines = new String[names.length];
        mLocks = new Object[names.length];
        for (int i = 0; i < names.length; i++) {
            mLines[i] = "begin";
            mLocks[i] = new Object();
        }
        for (int i = 0; i < names.length; i++) {
            new Thread(new FileRunnable(i, names[i])).start();
        }

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        while (merge() == 0) {
        }
        try {
            mBw.flush();
            mBw.close();
            mFw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class FileRunnable implements Runnable {
        int index;
        String name;

        public FileRunnable(int index, String name) {
            this.index = index;
            this.name = name;
        }

        @Override
        public void run() {
            if (V) log("[" + index + "] run");
            try {
                FileReader fr = new FileReader(mPath + name);
                BufferedReader br = new BufferedReader(fr);
                int line = 0;
                while (br.ready()) {
                    line++;
                    mLines[index] = br.readLine();
                    log("[" + index + "] line" + line + " " + mLines[index]);

                    // wait for compare and merge
                    synchronized (mLocks[index]) {
                        log("[" + index + "] wait");
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

    private synchronized static int merge() {
        if (V) log("merge" + " " + Arrays.toString(mLines));
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
        log("merge all:" + mLines.length + " begin:" + begin + " end:" + end + " number:"+ number + " index:" + index);
        if (begin > 0) {
            log("merge wait begin");
        } else if (end == mLocks.length) {
            log("merge done");
            return -1;
        } else {
            if (number > 1) {
                for (int i = index - 1; i >= 0; i--) {
                    if (!"begin".equals(mLines[i]) && !"end".equals(mLines[i]) && compare(mLines[i], mLines[index]) < 0) {
                        index = i;
                    }
                }
            }
            try {
                log("merge write [" + index + "] " + mLines[index]);
                mBw.write(mLines[index]);
                mBw.newLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            synchronized (mLocks[index]) {
                log("merge notify [" + index + "]");
                mLocks[index].notify();
            }
        }
        return 0;
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
            System.out.println("Usage: java -jar app.jar [option] [directory]\noption:\n    -e: event\n    -m: mobile \n    ... \n    detail see logcat filter");
        } else {
            String path = "";
            int attributes = 0;
            for (String arg : args) {
                if (arg.startsWith("-")) {
                    for (int i = 1; i < arg.length(); i++) {
                        if (arg.charAt(i) == 'c') {
                            attributes = attributes | _C;
                        } else if (arg.charAt(i) == 'e') {
                            attributes = attributes | _E;
                        } else if (arg.charAt(i) == 'k') {
                            attributes = attributes | _K;
                        } else if (arg.charAt(i) == 'm') {
                            attributes = attributes | _M;
                        } else if (arg.charAt(i) == 'R') {
                            attributes = attributes | _R;
                        } else if (arg.charAt(i) == 'S') {
                            attributes = attributes | _S;
                        } else {
                            System.out.println("what are you doing?");
                        }
                    }
                } else {
                    path = arg;
                }
            }
            long begin = System.currentTimeMillis();
            mergeFile(path, attributes);
            long end = System.currentTimeMillis();
            log("mergeFile time:" + (end - begin) / 1000);
        }
        return 0;
    }
}
