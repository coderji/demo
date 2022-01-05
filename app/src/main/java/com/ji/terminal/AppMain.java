package com.ji.terminal;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;

public class AppMain {
    public static void main(String[] args) {
        System.out.println("AppMain");
        printAscii();
    }

    private static void readFile() {
        try {
            InputStream is = new FileInputStream("D:\\Logs\\ota\\ab_delta-Ota_Version.32.2.35-32.2.36.hawaiip_g.retail.en.US.zip");
            BufferedInputStream bis = new BufferedInputStream(is);

            bis.skip(0);
            byte[] bytes = new byte[4];
            int len = bis.read(bytes);
            for (int i = 0; i < len; i++) {
                System.out.println("readFile bytes[" + i + "]:" + Integer.toHexString(bytes[i] & 0xFF));
            }

            bis.close();
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void printAscii() {
        char[] str1 = new char[]{0x0, 0x0, 0x8, 0x0, 0x21, 0x34, 0x4c, 0x7d, 0xc9, 0xe5, 0x82, 0xe2, 0xdb, 0x76, 0x91, 0xf7};
        char[] str2 = new char[]{0x0, 0x0, 0x8, 0x0, 0xc9, 0xd8, 0x7d, 0x7b, 0xc6, 0x55, 0x51, 0xdd, 0x32, 0x24, 0xa2, 0xe0};
        for (int i = 0; i < str1.length; i++) {
            System.out.print(str1[i] + " ");
        }
        System.out.println(" .");
        for (int i = 0; i < str2.length; i++) {
            System.out.print(str2[i] + " ");
        }
        System.out.println(" .");
    }
}
