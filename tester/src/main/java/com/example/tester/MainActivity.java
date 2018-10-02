package com.example.tester;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.baselib.modules.FileLoader;
import com.example.baselib.modules.impl.Load2DiskStrategy;
import com.example.baselib.modules.impl.Load2MemoryStrategy;
import com.example.baselib.utils.AppUtils;
import com.example.baselib.utils.ToastUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity";
    private ImageView mIv;
    private String[] urlArray = new String[]{
            "https://ww1.sinaimg.cn/large/0065oQSqly1ftu6gl83ewj30k80tites.jpg",
            "https://ws1.sinaimg.cn/large/0065oQSqly1fv5n6daacqj30sg10f1dw.jpg",
    };
    private TextView mTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findView();
    }

    void findView() {
        mIv = findViewById(R.id.main_iv);

        findViewById(R.id.main_btn_load2disk).setOnClickListener(this);
        findViewById(R.id.main_btn_load2memory).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_iv:
                loadBitmap();
                break;
            case R.id.main_btn_load2disk:
                load2Disk();
                break;
            case R.id.main_btn_load2memory:
                loadPic();
                break;

        }
    }

    private void load2Disk() {
        FileLoader.fromStrategy(new Load2DiskStrategy("zzw", "ldh.jpg"))
                .debug(true)
                .url("http://10.6.0.65:8080/file/11.jpg")
                .subscribeOn(AppUtils.isMainThread() ? FileLoader.ThreadMode.NEW_THREAD : FileLoader.ThreadMode.CUR_THREAD)
                .observeOn(FileLoader.ThreadMode.MAIN)
                .subscribe(new FileLoader.ISubscriber<File>() {
                    @Override
                    public void onThrowable(Throwable t) {
                        t.printStackTrace();
                    }

                    @Override
                    public void onFail(int err, Object extra) {
                        Log.e(TAG, "onFail: " + err + ", " + extra);
                    }

                    @Override
                    public void onProgress(int progress, int len) {
                        Log.e(TAG, "onProgress: " + progress + ", " + len);
                    }

                    @Override
                    public void onData(File file) {
                        ToastUtils.showToast(getApplicationContext(), file.getAbsoluteFile().getAbsolutePath());
                    }

                    @Override
                    public void onFinish(boolean success) {
                        ToastUtils.showToast(getApplicationContext(), "success: " + success);
                    }

                    @Override
                    public void onStart() {
                        Log.e(TAG, "onStart: ");
                    }
                });

    }

    private void loadPic() {
        FileLoader.fromStrategy(new Load2MemoryStrategy())
                .url("http://10.6.0.65:8080/file/11.jpg")
                .subscribeOn(AppUtils.isMainThread() ? FileLoader.ThreadMode.NEW_THREAD : FileLoader.ThreadMode.CUR_THREAD)
                .observeOn(FileLoader.ThreadMode.MAIN)
                .subscribe(new FileLoader.ISubscriber<byte[]>() {
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
                    public void onData(byte[] bytes) {
                        long start = System.currentTimeMillis();

                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        long end = System.currentTimeMillis();
                        // 5毫秒
                        Log.e(TAG, "onData: " + (end - start));
                        mIv.setImageBitmap(bitmap);
                    }

                    @Override
                    public void onFinish(boolean success) {

                    }

                    @Override
                    public void onStart() {

                    }
                });
    }

    private void loadBitmap() {
        FileLoader.fromStrategy(new FileLoader.ILoadStrategy<Bitmap>() {

            @Override
            public OutputStream getOutputStream() {
                return new ByteArrayOutputStream();
            }

            @Override
            public void onLoadProgress(int progress, int len) {
                Log.e(TAG, "onLoadProgress: " + progress + " ~ " + len);
            }

            @Override
            public Bitmap onLoadFinish(OutputStream os) {
                byte[] buf = ((ByteArrayOutputStream) os).toByteArray();
                return BitmapFactory.decodeByteArray(buf, 0, buf.length);
            }
        }).url(urlArray[1])
                .observeOn(FileLoader.ThreadMode.MAIN)
                .subscribeOn(FileLoader.ThreadMode.NEW_THREAD)
                .debug(true)
                .subscribe(new FileLoader.ISubscriber<Bitmap>() {
                    @Override
                    public void onThrowable(Throwable t) {
                        t.printStackTrace();
                    }

                    @Override
                    public void onFail(int err, Object extra) {
                        Log.e(TAG, "onFail: " + err);
                    }

                    @Override
                    public void onProgress(int progress, int len) {
                        Log.e(TAG, "onProgress: " + progress + ", " + len);
                    }

                    @Override
                    public void onData(Bitmap bitmap) {
                        mIv.setImageBitmap(bitmap);
                    }

                    @Override
                    public void onFinish(boolean success) {
                        Log.e(TAG, "onFinish: " + success);
                        mTv.setVisibility(View.GONE);
                    }

                    @Override
                    public void onStart() {
                        mTv.setVisibility(View.VISIBLE);
                    }
                });
    }
}
