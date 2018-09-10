package com.example.baselib.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 *
 * 时间工具类
 *
 * @author YGX
 */

public class TimeUtils {

    /**
     * 获取当前指定格式的时间
     *
     * @param timeFormat 时间格式, 例如: yyyy-MM-dd HH:mm:ss
     * @return 格式化后的时间字符串
     */
    public static String getTime(String timeFormat){
        return formatTime(System.currentTimeMillis(), timeFormat);
    }

    /**
     * 用指定的时间格式格式化时间戳
     * @param timeMillis  时间戳, 单位是毫秒
     * @param timeFormat  时间格式
     * @return 格式化后的时间字符串
     */
    public static String formatTime(long timeMillis, String timeFormat){
        SimpleDateFormat formatter = new SimpleDateFormat(timeFormat, Locale.CHINA);
        return formatter.format(new Date(timeMillis));
    }

    /**
     * 递增时间
     * @param time 当前时间
     * @param timeFormat 当前时间的格式
     * @param increaseMillis 要递增的毫秒数; 如果是正数, 则为加, 为负数, 则为减
     * @return 递增后的时间
     */
    public static String increaseTime(String time, String timeFormat, long increaseMillis)
            throws ParseException {
        final SimpleDateFormat formatter = new SimpleDateFormat(timeFormat, Locale.CHINA);
        Date date = formatter.parse(time);
        long newMillis = date.getTime() + increaseMillis;
        date.setTime(newMillis);
        return formatter.format(date);
    }

    /**
     * 计算两个时间相差的毫秒值
     *
     * @param time1 时间1
     * @param format1 时间1对应的格式
     * @param time2 时间2
     * @param format2 时间2对应的格式
     * @return  相差的毫秒值
     * @throws ParseException format1和formar2错误的时候
     */
    public static long calcTimemGap(String time1, String format1,
                                    String time2, String format2) throws ParseException {
        final SimpleDateFormat formatter = new SimpleDateFormat(format1, Locale.CHINA);
        Date date1 = formatter.parse(time1);
        formatter.applyPattern(format2);
        Date date2 = formatter.parse(time2);
        return date1.getTime() - date2.getTime();
    }
}
