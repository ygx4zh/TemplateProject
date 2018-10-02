package com.example.baselib.modules.impl;

import com.example.baselib.modules.FileLoader;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

/**
 *
 * @author YGX 加载到内存中
 */

public class Load2MemoryStrategy implements FileLoader.ILoadStrategy<byte[]> {

    @Override
    public OutputStream getOutputStream() {
        return new ByteArrayOutputStream();
    }

    @Override
    public void onLoadProgress(int progress, int len) {

    }

    @Override
    public byte[] onLoadFinish(OutputStream os) {
        return ((ByteArrayOutputStream) os).toByteArray();
    }
}
