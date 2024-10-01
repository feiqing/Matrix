package com.alibaba.matrix.base.util;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * @author cunxiao
 * @since 2017-12-05
 */
public class T {

    public static final long OneS = 1000;

    public static final long OneM = 60 * OneS;

    public static final long OneH = 60 * OneM;

    public static final long OneD = 24 * OneH;

    public static final long OneW = 7 * OneD;

    public static long ts(long timestamp, TimeZone zone) {
        return t(timestamp, zone, Calendar.MILLISECOND);
    }

    public static long ts(long timestamp) {
        return ts(timestamp, TimeZone.getDefault());
    }

    public static long tm(long timestamp, TimeZone zone) {
        return t(timestamp, zone, Calendar.SECOND, Calendar.MILLISECOND);
    }

    public static long tm(long timestamp) {
        return tm(timestamp, TimeZone.getDefault());
    }

    public static long th(long timestamp, TimeZone zone) {
        return t(timestamp, zone, Calendar.MINUTE, Calendar.SECOND, Calendar.MILLISECOND);
    }

    public static long th(long timestamp) {
        return th(timestamp, TimeZone.getDefault());
    }

    public static long td(long timestamp, TimeZone zone) {
        return t(timestamp, zone, Calendar.HOUR_OF_DAY, Calendar.MINUTE, Calendar.SECOND, Calendar.MILLISECOND);
    }

    public static long td(long timestamp) {
        return td(timestamp, TimeZone.getDefault());
    }

    private static long t(long ts, TimeZone zone, int... fields) {
        Calendar calendar = Calendar.getInstance(zone);
        calendar.setTimeInMillis(ts);
        for (int field : fields) {
            calendar.set(field, 0);
        }
        return calendar.getTimeInMillis();
    }
}
