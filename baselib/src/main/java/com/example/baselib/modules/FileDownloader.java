package com.example.baselib.modules;

/**
 * 文件下载者
 *
 * @author YGX
 */

public class FileDownloader {

    public static FileDownloader create(){
        return new FileDownloader();
    }

    public FileDownloader url(String url){
        return this;
    }

    public FileDownloader intoDir(String targetDir){
        return this;
    }

    public FileDownloader name(String name){
        return this;
    }

    public void download(IDownloadObserver observer){

    }
}
