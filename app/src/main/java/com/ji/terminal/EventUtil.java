package com.ji.terminal;

import com.android.internal.logging.MetricsProto;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class EventUtil {
    private static final String TAG = "EventUtil";

    private static String eventToString(String event) {
        String tag = "sysui_multi_action";
        if (event.contains(tag)) {
            StringBuilder stringBuilder = new StringBuilder(event).append('\n');
            stringBuilder.append(event.substring(0, event.indexOf(tag) + tag.length())).append(".parse: [");
            String[] events = event.substring(event.indexOf(tag) + tag.length() + 3, event.length() - 1).split(",");
            for (int i = 0; i < events.length; i++) {
                try {
                    int proto = Integer.parseInt(events[i]);
                    stringBuilder.append(MetricsProto.valueOf(proto));
                } catch (NumberFormatException exception) {
                    stringBuilder.append(events[i]);
                }
                if (i < events.length - 1) stringBuilder.append(',');
            }
            stringBuilder.append(']');
            return stringBuilder.toString();
        } else {
            return event;
        }
    }

    private static int parseFile(String path) {
        int parse = 0;
        try {
            FileReader fr = new FileReader(path);
            BufferedReader br = new BufferedReader(fr);
            FileWriter fw = new FileWriter(path + ".parse");
            BufferedWriter bw = new BufferedWriter(fw);
            String line;
            while (br.ready()) {
                line = br.readLine();
                bw.write(eventToString(line));
                bw.newLine();
            }
            bw.flush();
            bw.close();
            fw.close();
            br.close();
            fr.close();
        } catch (IOException e) {
            e.printStackTrace();
            parse = -1;
        }
        return parse;
    }

    private static void log(String s) {
        System.out.println(new SimpleDateFormat("MM-dd HH:mm:ss.SSS", Locale.getDefault()).format(new Date())
                + " " + TAG + ": " + s);
    }

    public static int main(String[] args) {
        if (args == null || args.length != 1) {
            System.out.println("Usage: java -jar app.jar [input]");
        } else {
            long begin = System.currentTimeMillis();
            parseFile(args[0]);
            long end = System.currentTimeMillis();
            log("parseFile " + args[0] + " -> " + args[0] + ".parse time:" + (end - begin) / 1000);
        }
        return 0;
    }
}
