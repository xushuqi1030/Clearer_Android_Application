package com.example.keeps_000.clearer.download;

import android.os.Environment;

import java.io.File;
import java.io.IOException;

/**
 * Created by keeps_000 on 2018/4/21.
 */

public class FileUtils {
    private String path = Environment.getExternalStorageDirectory().toString() + "/Clearer/Pictures";

    public FileUtils() {
        File file = new File(path);
        /**
         *如果文件夹不存在就创建
         */
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    /**
     * 创建一个文件
     * @param FileName 文件名
     * @return
     */
    public File createFile(String FileName) {
        File file = new File(path,FileName);
        return file;
    }
}
