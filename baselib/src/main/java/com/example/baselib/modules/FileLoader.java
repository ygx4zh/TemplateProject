package com.example.baselib.modules;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.IntDef;
import android.support.annotation.IntRange;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;

/**
 * 文件下载
 */

public class FileLoader<Data> {
    private static final String TAG = "FileLoader";
    private final ILoadStrategy<Data> handler;

    private FileLoader(ILoadStrategy<Data> handler) {
        this.handler = handler;
    }

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

    public interface ISubscriber<Data> {

        /**
         * 抛出异常时, 在{@link FileLoader#observeOn} 指定的线程回调
         *
         * @param t throwable
         */
        void onThrowable(Throwable t);


        void onFail(int err, Object extra);

        void onProgress(int progress, int len);


        void onData(Data data);

        /**
         * 执行结束, 在{@link FileLoader#observeOn} 指定的线程回调 在onData后执行
         *
         * @param success true, 请求成功; false, 请求失败
         */
        void onFinish(boolean success);

        void onStart();
    }

    public static <Data> FileLoader<Data> fromStrategy(ILoadStrategy<Data> handler) {
        return new FileLoader<Data>(handler);
    }

    public interface ILoadStrategy<Result> {

        OutputStream getOutputStream();

        void onLoadProgress(int progress, int len);

        Result onLoadFinish(OutputStream os);
    }

    private String mReqUrl;
    private ExecutorService mPool;
    private boolean debug;
    private int subscribeOn;
    private int observeOn;
    private ISubscriber<Data> mSubscriber;

    public FileLoader<Data> url(String url) {
        mReqUrl = url;
        return this;
    }

    public FileLoader<Data> assignThreadPool(ExecutorService pool) {
        mPool = pool;
        return this;
    }

    public FileLoader<Data> debug(boolean debug) {
        this.debug = debug;
        return this;
    }

    public FileLoader<Data> subscribeOn(
            @ThreadModeRange
            @IntRange(from = ThreadMode.CUR_THREAD,
                    to = ThreadMode.NEW_THREAD) int subscribeOn) {
        this.subscribeOn = subscribeOn;
        return this;
    }

    public FileLoader<Data> observeOn(@ThreadModeRange
                                      @IntRange(from = ThreadMode.MAIN,
                                              to = ThreadMode.CUR_THREAD) int observeOn) {
        this.observeOn = observeOn;
        return this;
    }


    public FileLoader<Data> subscribe(ISubscriber<Data> subscriber) {
        mSubscriber = subscriber;
        switch (subscribeOn) {
            case FileLoader.ThreadMode.NEW_THREAD:
                if (mPool != null) {
                    mPool.execute(this::req);
                } else {
                    new Thread(this::req, TAG + "@" + FileLoader.this.hashCode() + "-request").start();
                }
                break;
            case FileLoader.ThreadMode.CUR_THREAD:
            default:
                req();
                break;
        }
        return this;
    }

    private void req() {
        final Handler h = new Handler(Looper.getMainLooper());
        HttpURLConnection conn = null;
        InputStream is = null;
        OutputStream os = null;
        try {

            if (subscribeOn == ThreadMode.CUR_THREAD) {
                if (mSubscriber != null) {
                    mSubscriber.onStart();
                }
            }else{
                if(mSubscriber != null) {
                    h.post(new Runnable() {
                        @Override
                        public void run() {
                            mSubscriber.onStart();
                        }
                    });
                }
            }

            URL wsUrl = new URL(mReqUrl);

            conn = (HttpURLConnection) wsUrl.openConnection();

            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            // conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");

            os = conn.getOutputStream();
            String responseMessage = conn.getResponseMessage();
            int responseCode = conn.getResponseCode();

            if (debug) {
                Log.d(TAG, "responseCode: " + responseCode + ", responseMessage: " + responseMessage);
            }

            if (responseCode == 200) {
                is = conn.getInputStream();

                byte[] b = new byte[1024];
                int len = 0;
                int contentLength = conn.getContentLength();
                os = (handler == null) ? null : handler.getOutputStream();
                int progress = 0;
                while ((len = is.read(b)) != -1) {
                    if (os != null) {
                        os.write(b, 0, len);
                        os.flush();
                    }
                    progress += len;
                    if (handler != null) {
                        handler.onLoadProgress(progress, contentLength);
                    }

                    if (subscribeOn == ThreadMode.CUR_THREAD) {
                        if (mSubscriber != null) {
                            mSubscriber.onProgress(progress, contentLength);
                        }
                    }else{
                        if(mSubscriber != null) {
                            final int finalProgress = progress;
                            h.post(new Runnable() {
                                @Override
                                public void run() {
                                    mSubscriber.onProgress(finalProgress, contentLength);
                                }
                            });
                        }
                    }
                }
                Data data;
                if (handler != null) {
                    data = handler.onLoadFinish(os);
                } else {
                    data = null;
                }

                if (debug) {
                    Log.d(TAG, "recv-trans:\n" + data);
                }

                if (mSubscriber != null) {
                    if (observeOn == FileLoader.ThreadMode.MAIN) {
                        new Handler(Looper.getMainLooper())
                                .post(() -> {
                                    mSubscriber.onData(data);
                                    mSubscriber.onFinish(true);
                                });
                    } else {
                        mSubscriber.onData(data);
                        mSubscriber.onFinish(true);
                    }
                }
            } else {
                if (mSubscriber != null) {
                    if (observeOn == FileLoader.ThreadMode.MAIN) {
                        new Handler(Looper.getMainLooper())
                                .post(() -> {
                                    mSubscriber.onFail(responseCode, responseMessage);
                                    mSubscriber.onFinish(false);
                                });
                    } else {
                        mSubscriber.onFail(responseCode, responseMessage);
                        mSubscriber.onFinish(false);
                    }
                }
            }
        } catch (final Exception e) {
            if (mSubscriber != null) {
                if (observeOn == FileLoader.ThreadMode.MAIN) {
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
