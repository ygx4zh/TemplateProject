package com.example.baselib;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.example.baselib.modules.FileLoader;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

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

        assertEquals("com.example.baselib.test", appContext.getPackageName());
    }

    @Test
    public void testFileLoader() throws Exception{
        FileLoader.fromStrategy(new FileLoader.ILoadStrategy<Bitmap>() {
            @Override
            public OutputStream getOutputStream() {
                return new ByteArrayOutputStream();
            }

            @Override
            public void onLoadProgress(int progress, int len) {

            }

            @Override
            public Bitmap onLoadFinish(OutputStream os) {
                return null;
            }


        })
                .url("")
                .subscribeOn(FileLoader.ThreadMode.CUR_THREAD)
                .subscribeOn(FileLoader.ThreadMode.CUR_THREAD)
                .subscribe(new FileLoader.ISubscriber<Bitmap>() {
            @Override
            public void onThrowable(Throwable t) {

            }

            @Override
            public void onFail(int err, Object extra) {

            }

            @Override
            public void onProgress(int progress, int len) {

            }

            @Override
            public void onData(Bitmap bitmap) {

            }

            @Override
            public void onFinish(boolean success) {

            }
        });
    }
}
