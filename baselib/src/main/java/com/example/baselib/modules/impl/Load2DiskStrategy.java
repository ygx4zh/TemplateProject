package com.example.baselib.modules.impl;

import android.os.Environment;
import android.text.TextUtils;

import com.example.baselib.modules.FileLoader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

/**
 * 加载到磁盘;
 *
 * 高系统版本常常因为权限问题不能创建文件, 导致创建文件失败;
 *
 * @author YGX
 */

public class Load2DiskStrategy implements FileLoader.ILoadStrategy<File> {

    private String dir;
    private String name;
    private File file;

    public Load2DiskStrategy(String dir, String name){
        this.dir = dir;
        this.name = name;
    }

    @Override
    public OutputStream getOutputStream() {
        try {
            File parent;
            if(!TextUtils.isEmpty(dir)){
                parent = new File(Environment.getExternalStorageDirectory(),dir);
                if(!parent.exists()){
                    parent.mkdir();
                }
            }else{
                parent = Environment.getExternalStorageDirectory();
            }
            file = new File(parent, name);
            // todo 适配高系统版本创建文件
            return new FileOutputStream(file);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onLoadProgress(int progress, int len) {

    }

    @Override
    public File onLoadFinish(OutputStream os) {
        return file;
    }
}
