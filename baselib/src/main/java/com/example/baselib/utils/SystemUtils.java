package com.example.baselib.utils;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.util.Log;

import com.example.baselib.BaseConstants;

import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 *
 * 系统帮助工具类
 *
 * @author YGX
 */

public class SystemUtils {
    private static final String TAG = "SystemUtils";
    public static String getIpAddress(){
        try {
            Enumeration<NetworkInterface> nifs = NetworkInterface.getNetworkInterfaces();
            while (nifs.hasMoreElements()) {
                NetworkInterface nif = nifs.nextElement();
                Enumeration<InetAddress> ias = nif.getInetAddresses();
                if(ias == null) continue;
                while (ias.hasMoreElements()) {
                    InetAddress ia = ias.nextElement();
                    if(ia != null && !ia.isLoopbackAddress()){
                        return ia.getHostAddress();
                    }
                }
            }
        } catch (SocketException ignored) {

        }
        return BaseConstants.LOOPBACK_ADDRESS;
    }

    public static String getSpecialNetworkInterfaceMac(String nifName, boolean toUpperCase){
        if(TextUtils.isEmpty(nifName)) return null;

        try {
            NetworkInterface nif = NetworkInterface.getByName(nifName);
            byte[] hardwareAddress = nif.getHardwareAddress();
            StringBuilder sBuf = new StringBuilder();
            for (byte b : hardwareAddress) {
                String hex = Integer.toHexString(b & 0xFF);
                hex = hex.length() < 2 ? "0"+hex:hex;
                sBuf.append(toUpperCase?hex.toUpperCase():hex).append(":");
            }
            sBuf.substring(0,sBuf.length()-1);
        } catch (SocketException e) {

        }
        return null;
    }

    public static List<String> getSupportNetworkInterfaces(){
        List<String> nifs = new ArrayList<>();
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                if(networkInterface == null) continue;

                nifs.add(networkInterface.getDisplayName());
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "getSupportNetworkInterfaces: "+e.getMessage());
        }
        return nifs;
    }

    /**
     * 获取system/build.prop 下的编译信息
     *
     * @param key key
     * @return null if not exist;
     */
    public static String getBuildModel(String key){
        try {
            @SuppressLint("PrivateApi") @SuppressWarnings("unchecked")
            Class<?> systemPrepertiesClz =
                    Class.forName("android.os.SystemProperties");
            Method getMethod = systemPrepertiesClz.getDeclaredMethod("get", String.class);
            Object buildModel = getMethod.invoke(null, key);
            return (String) buildModel;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
