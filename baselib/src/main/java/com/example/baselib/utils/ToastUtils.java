package com.example.baselib.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.StringRes;
import android.widget.Toast;

/**
 * 显示吐司的工具类
 *
 * @author YGX
 */

public class ToastUtils {

    private static Toast sToastInstance;
    private static Handler sMainHandler;

    public static void showToast(Context context, String msg){

        checkToastInstance(context);

        if(AppUtils.isMainThread()) {
            sToastInstance.setText(msg);
            sToastInstance.show();
        }else{
            if(sMainHandler == null){
                sMainHandler = new Handler(Looper.getMainLooper());
            }

            sMainHandler.post(() -> {
                sToastInstance.setText(msg);
                sToastInstance.show();
            });
        }
    }

    public static void showToast(Context context, @StringRes int strRes){

        checkToastInstance(context);

        if(AppUtils.isMainThread()) {
            sToastInstance.setText(strRes);
            sToastInstance.show();
        }else{
            if(sMainHandler == null){
                sMainHandler = new Handler(Looper.getMainLooper());
            }

            sMainHandler.post(() -> {
                sToastInstance.setText(strRes);
                sToastInstance.show();
            });
        }
    }

    @SuppressLint("ShowToast")
    private static void checkToastInstance(Context context){
        if(sToastInstance == null) {
            sToastInstance = Toast.makeText(context.getApplicationContext(), "", Toast.LENGTH_SHORT);
        }
    }

}
