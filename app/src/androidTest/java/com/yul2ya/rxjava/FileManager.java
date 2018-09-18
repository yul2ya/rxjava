package com.yul2ya.rxjava;

import com.yul2ya.rxjava.common.Log;

import java.io.File;

public class FileManager {

    public static void deleteFile(File file) {
        if (!file.exists()) {
            Log.e("file is not exist!");
            return;
        }

        if (file.isDirectory()) {
            for (File f : file.listFiles()) {
                deleteFile(f);
            }
            return;
        }

        if (file.delete()) {
            Log.i("success, " + file.getName());
        } else {
            Log.e("fail, " + file.getName());
        }
    }
}
