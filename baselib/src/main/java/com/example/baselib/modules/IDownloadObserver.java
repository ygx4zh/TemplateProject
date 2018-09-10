package com.example.baselib.modules;



public interface IDownloadObserver {

    void onStart();

    void onProgress();

    void onEnd();

    void onThrowable(Throwable t);
}
