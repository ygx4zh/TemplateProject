package com.example.dblib;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.example.dblib.test", appContext.getPackageName());
    }

    @Test
    public void testDb() throws Exception {
        DbManager dbManager = DbManager.getDefault(InstrumentationRegistry.getContext());

        String key = "key";
        String defValue = "defValue";

        // 同步从数据库中获取到配置信息
        String config = dbManager.queryConfig(key, defValue);

        String[] keys = new String[]{"webIC","ajb"};
        // 同步从数据库中获取到配置数组的值
        String[] configs = dbManager.queryConfigs(keys, null);

        // 异步获取单个配置的值
        dbManager.queryConfig(new Handler(Looper.getMainLooper()), new Querier<String, String>(key, defValue) {
            @Override
            protected void onQuery(String s) {

            }
        });

        // 异步获取配置集合的值
        dbManager.queryConfigs(new Handler(Looper.getMainLooper()),
                new Querier<String[], String[]>(keys) {
            @Override
            protected void onQuery(String[] configs) {

            }
        });
    }
}
