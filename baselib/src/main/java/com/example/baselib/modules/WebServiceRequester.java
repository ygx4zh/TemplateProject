package com.example.baselib.modules;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.IntDef;
import android.support.annotation.IntRange;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;

/**
 * WebService请求帮助类
 *
 * @author YGX
 */
public class WebServiceRequester {
    private static final String TAG = "WebServiceRequester";
    private int subscribeOn;
    private int observeOn;
    private ISubscriber<String, String> mSubscriber;
    private ExecutorService mPool;

    public interface ThreadMode {
        int MAIN = -1;
        int CUR_THREAD = 0;
        int NEW_THREAD = 1;
    }

    @IntDef({
            ThreadMode.MAIN,
            ThreadMode.NEW_THREAD,
            ThreadMode.CUR_THREAD,
    })
    @interface ThreadModeRange {
    }

    public interface ISubscriber<RecvData, TransData> {

        /**
         * 抛出异常时, 在{@link WebServiceRequester#observeOn} 指定的线程回调
         * @param t throwable
         */
        void onThrowable(Throwable t);

        /**
         * 接收到消息时, 在{@link WebServiceRequester#subscribeOn} 线程调用,
         * 将最先收到的数据转换为想要的数据
         * @param recv 从网络IO中读到的数据
         * @return 转换为的数据
         */
        TransData onRecvResult(RecvData recv);

        /**
         * 处理数据时, 在{@link WebServiceRequester#observeOn} 指定的线程回调
         * @param data 由onRecvResult方法转换而来
         */
        void onData(TransData data);

        /**
         * 执行结束, 在{@link WebServiceRequester#observeOn} 指定的线程回调 在onData后执行
         * @param success true, 请求成功; false, 请求失败
         */
        void onFinish(boolean success);
    }

    private String mReqUrl;
    private String raw;

    public static WebServiceRequester create() {
        return new WebServiceRequester();
    }

    public WebServiceRequester url(String url) {
        mReqUrl = url;
        return this;
    }

    public WebServiceRequester reqRaw(String raw) {
        this.raw = raw;
        return this;
    }

    public WebServiceRequester assignThreadPool(ExecutorService pool) {
        mPool = pool;
        return this;
    }

    public WebServiceRequester subscribeOn(
            @ThreadModeRange
            @IntRange(from = ThreadMode.CUR_THREAD,
                    to = ThreadMode.NEW_THREAD) int subscribeOn) {
        this.subscribeOn = subscribeOn;
        return this;
    }

    public WebServiceRequester observeOn(@ThreadModeRange
                                       @IntRange(from = ThreadMode.MAIN,
                                               to = ThreadMode.CUR_THREAD) int observeOn) {
        this.observeOn = observeOn;
        return this;
    }

    public WebServiceRequester subscribe(ISubscriber<String, String> subscriber) {
        mSubscriber = subscriber;
        switch (subscribeOn) {
            case ThreadMode.NEW_THREAD:
                if (mPool != null) {
                    mPool.execute(new Runnable() {
                        @Override
                        public void run() {
                            req();
                        }
                    });
                } else {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            req();
                        }
                    }, TAG + "@" + WebServiceRequester.this.hashCode() + "-request").start();
                }
                break;
            case ThreadMode.CUR_THREAD:
            default:
                req();
                break;
        }
        return this;
    }

    private void req() {
        HttpURLConnection conn = null;
        InputStream is = null;
        OutputStream os = null;
        try {
            URL wsUrl = new URL(mReqUrl);

            conn = (HttpURLConnection) wsUrl.openConnection();

            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "text/xml;charset=UTF-8");

            os = conn.getOutputStream();

            os.write(raw.getBytes("UTF-8"));

            is = conn.getInputStream();

            byte[] b = new byte[1024];
            int len = 0;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            while ((len = is.read(b)) != -1) {
                baos.write(b, 0, len);
                baos.flush();
            }
            String s = baos.toString();
            final String data;
            if (mSubscriber != null) {
                data = mSubscriber.onRecvResult(s);
            } else {
                data = s;
            }

            if (mSubscriber != null) {
                if (observeOn == ThreadMode.MAIN) {
                    new Handler(Looper.getMainLooper())
                            .post(new Runnable() {
                                @Override
                                public void run() {
                                    mSubscriber.onData(data);
                                    mSubscriber.onFinish(true);
                                }
                            });
                } else {
                    mSubscriber.onData(data);
                    mSubscriber.onFinish(true);
                }
            }
        } catch (final Exception e) {
            if (mSubscriber != null) {
                if (observeOn == ThreadMode.MAIN) {
                    new Handler(Looper.getMainLooper())
                            .post(new Runnable() {
                                @Override
                                public void run() {
                                    mSubscriber.onThrowable(e);
                                    mSubscriber.onFinish(false);
                                }
                            });
                } else {
                    mSubscriber.onThrowable(e);
                    mSubscriber.onFinish(false);
                }
            }
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                }
            }

            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                }
            }

            if (conn != null) {
                conn.disconnect();
            }
        }
    }
}
