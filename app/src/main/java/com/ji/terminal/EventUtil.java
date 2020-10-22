package com.ji.terminal;

import com.android.internal.logging.MetricsProto;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class EventUtil {
    private static final String TAG = "EventUtil";

    private static String eventToString(String event) {
        StringBuilder stringBuilder = new StringBuilder();
        int length = event.length();
        int number = -1;
        for (int i = 0; i < length; i++) {
            if (event.charAt(i) == '[') {
                stringBuilder.append(event.substring(0, i + 1));
                number = 0;
            } else if (event.charAt(i) == ',' && number >= 0) {
                stringBuilder.append(MetricsProto.valueOf(number)).append(',');
                number = 0;
            } else if (event.charAt(i) == ']' && number >= 0) {
                stringBuilder.append(MetricsProto.valueOf(number)).append(']');
            } else if (number >= 0 && event.charAt(i) >= '0' && event.charAt(i) <= '9') {
                number = number * 10 + event.charAt(i) - '0';
            }
        }
        return stringBuilder.toString();
    }

    private static void log(String s) {
        System.out.println(String.format(Locale.getDefault(), "%-10s", Thread.currentThread().getName())
                + new SimpleDateFormat("MM-dd HH:mm:ss.SSS", Locale.getDefault()).format(new Date())
                + " " + TAG + ": " + s);
    }

    public static void main(String[] args) {
        log(eventToString("01-08 17:47:41.811022  1120  1139 I sysui_multi_action: [757,803,799,window_time_0,802,0]"));
    }
}
