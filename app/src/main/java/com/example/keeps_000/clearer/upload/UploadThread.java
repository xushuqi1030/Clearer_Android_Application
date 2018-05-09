package com.example.keeps_000.clearer.upload;

import android.util.Log;

import com.example.keeps_000.clearer.singleton.DeviceSingleton;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * Created by keeps_000 on 2018/1/11.
 */

public class UploadThread implements Runnable{
    private CountDownLatch countDownLatch;
    private static String requestURL = "http://139.199.153.122:666/androidServer/androidUploadImg";  //这里应该是某个servlet
    private List<File> files;

    public UploadThread(CountDownLatch countDownLatch,List<File> files){
        this.countDownLatch = countDownLatch;
        this.files = files;
    }

    public void run(){
        DeviceSingleton deviceSingleton = DeviceSingleton.getInstance();
        String mDeviceID = deviceSingleton.getmDeviceID();
        final Map<String, String> params = new HashMap<String, String>();
        params.put("send_userId", "xushuqi");
        params.put("mDeviceID",mDeviceID);
        final Map<String, File> upfiles = new HashMap<String, File>();
        for(int p = 0; p<files.size();p++){
            upfiles.put("uploadfile"+String.valueOf(p+1),files.get(p));
            Log.e("filename:",files.get(p).getName());
        }
        Log.e("text:","2");
        String UploadFileResult = "666666";
        try {
            UploadFileResult = UploadParaUtil.post(requestURL,params,upfiles);
            Log.e("text:","3");
            Log.e("传输图片的结果是：",UploadFileResult);
            Log.e("text:","4");
        }catch (Exception e){
            e.printStackTrace();
        } finally {
            Log.e("msg:","上传的线程结束了！");
            countDownLatch.countDown();
        }

    }
}
