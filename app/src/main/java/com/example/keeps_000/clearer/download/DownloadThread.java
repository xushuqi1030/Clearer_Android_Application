package com.example.keeps_000.clearer.download;

import android.util.Log;
import java.util.concurrent.CountDownLatch;

/*
 * Created by keeps_000 on 2018/4/21.
 */

public class DownloadThread implements Runnable {

    private CountDownLatch countDownLatch;
    private String path;
    private String filename;

    public DownloadThread(CountDownLatch countDownLatch,String path,String filename){
        this.countDownLatch = countDownLatch;
        this.path = path;
        this.filename = filename;
    }

    @Override
    public void run() {
        try {
            Log.i("下载线程内","调用下载前。");
            DownloadUtil.download(path,filename);
            Log.i("下载线程内","调用下载后。");
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            Log.i("message","进入到finally的部分了！");
            countDownLatch.countDown();
        }
    }
}
