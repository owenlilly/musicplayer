package com.github.raveplayer.utils;


import java.util.Locale;

public class TimeUtils {

    public static String millisToFormatedTime(long millis) {
        long seconds = millis/1000;
        long s = seconds % 60;
        long m = (seconds / 60) % 60;
        long h = (seconds / (60 * 60)) % 24;

        return h > 0 ? String.format(Locale.getDefault(),"%d:%02d:%02d", h,m,s) :
                        String.format(Locale.getDefault(), "%d:%02d", m, s);
    }
}
