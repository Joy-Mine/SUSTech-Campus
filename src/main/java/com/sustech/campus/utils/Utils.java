package com.sustech.campus.utils;

import java.util.Random;

public class Utils {
    static Random random = new Random();

    static String chars = "1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    public static String generateToken(String oldToken) {
        int length = 32;
        StringBuilder builder;
        do {
            builder = new StringBuilder();
            for (int i = 0; i < length; ++i) {
                builder.append((char) random.nextInt(33, 127));
            }
        } while (builder.toString().equals(oldToken));
        return builder.toString();
    }

    public static String generateFileName(String oldName) {
        int length = 10;
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < length; ++i) {
            builder.append(chars.charAt(random.nextInt(chars.length())));
        }
        String suffix = "";
        if (oldName != null && oldName.contains(".")) {
            suffix = oldName.substring(oldName.lastIndexOf("."));
        }
        return builder + suffix;
    }
}
