package com.alibaba.matrix.base.util;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.TimeZones;
import org.joda.time.DateTimeZone;

import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * @author cunxiao@taobao.com
 * @since 2017-12-05
 */
public class T {

    public static final long OneS = 1000;

    public static final long _1_S = OneS;

    public static final long OneM = 60 * OneS;

    public static final long _1_M = OneM;

    public static final long OneH = 60 * OneM;

    public static final long _1_H = OneH;

    public static final long OneD = 24 * OneH;

    public static final long _1_D = OneD;

    public static final long OneW = 7 * OneD;

    public static final long _1_W = OneW;

    public static final String DATE_PATTERN = "yyyy-MM-dd";

    public static final String TIME_PATTERN = "HH:mm:ss";

    public static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    public static final String DATE_TIME_MILLIS_PATTERN = "yyyy-MM-dd HH:mm:ss,SSS";

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

    public static void main(String[] args) {
        test0();
        test1();
        test2();
        test3();
        test4();
    }

    private static void test0() {
        long td = T.td(System.currentTimeMillis(), TimeZone.getDefault());
        System.out.println(DateFormatUtils.format(td, DATE_TIME_MILLIS_PATTERN, TimeZone.getDefault()));
    }

    private static void test1() {
        long td = T.td(System.currentTimeMillis(), TimeZone.getTimeZone(TimeZones.GMT_ID));
        System.out.println(DateFormatUtils.format(td, DATE_TIME_MILLIS_PATTERN, TimeZone.getDefault()));
    }

    private static void test2() {
        long td = T.td(System.currentTimeMillis(), DateTimeZone.UTC.toTimeZone());
        System.out.println(DateFormatUtils.format(td, DATE_TIME_MILLIS_PATTERN, TimeZone.getDefault()));
    }

    private static void test3() {
        long td = T.td(System.currentTimeMillis(), DateTimeZone.UTC.toTimeZone());
        System.out.println(DateFormatUtils.format(td, DATE_TIME_MILLIS_PATTERN, DateTimeZone.forID("+08:00").toTimeZone()));
    }

    private static void test4() {
        long td = T.td(System.currentTimeMillis(), DateTimeZone.UTC.toTimeZone());
        System.out.println(DateFormatUtils.format(td, DATE_TIME_MILLIS_PATTERN, DateTimeZone.forID("+08:00").toTimeZone(), Locale.getDefault()));
    }
}
