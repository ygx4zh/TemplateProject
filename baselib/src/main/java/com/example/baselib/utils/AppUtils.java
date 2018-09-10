package com.example.baselib.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

/**
 * APP相关的工具类
 *
 * @author YGX
 */

public class AppUtils {

    /**
     * 获取APP的版本名
     * @param ctx 上下文
     * @return 版本名
     */
    public static String getAppVersionName(Context ctx){
        PackageManager pm = ctx.getPackageManager();
        try {
            PackageInfo packageInfo = pm.getPackageInfo(ctx.getPackageName(), 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取APP的版本码
     * @param ctx 上下文
     * @return 版本码
     */
    public static int getAppVersionCode(Context ctx){
        PackageManager pm = ctx.getPackageManager();
        try {
            PackageInfo packageInfo = pm.getPackageInfo(ctx.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return -1;
    }

}
