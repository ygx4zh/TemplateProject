package com.example.baselib.utils;

import java.util.Random;

/**
 * 数据帮助者
 *
 * @author YGX
 */

public class NumbersUtils {

    private static final Random APP_RANDOM = new Random();

    /**
     * 获取一个随机数产生器
     *
     * @return 随机数产生器
     */
    public static Random random(){
        return APP_RANDOM;
    }

    /**
     * 产生一个随机整数, 范围: [start, start + bound);
     * @param start 起始数
     * @param bound 范围
     * @return 随机数
     */
    public static int randomInt(int start, int bound){
        return random().nextInt(bound) + start;
    }

    public static float getPercentageValue(float startValue, float endValue, float rate) {
        return startValue + ((endValue - startValue) * rate);
    }

    /**
     * 根据fraction从可变数组中取出对应的值
     * @param rate 比例
     * @param values 可变数组值
     * @return 百分比对应的值
     */
    public static float getPercentageValue(float rate, float...values){
        if(values.length == 0) return 0;
        if(values.length == 1) return values[0];

        if(rate == 1f) return values[values.length - 1];
        float gp = 1f / (values.length - 1);
        int index = (int) (rate / gp);
        float section_start = values[index];
        float section_end = values[index + 1];
        float section_fraction = (rate - index * gp) / gp;

        return getPercentageValue(section_start,section_end,section_fraction);
    }
}
