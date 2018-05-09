package com.example.keeps_000.clearer.download;

import android.util.Log;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by keeps_000 on 2018/4/21.
 */

public class DownloadUtil {

    public static void download(final String path,final String filename){
        try {
            Log.i("DownloadUtil里面","1");
            URL url = new URL(path);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            Log.i("DownloadUtil里面","2");
            con.setReadTimeout(5000);
            con.setConnectTimeout(5000);
            con.setRequestProperty("Charset", "UTF-8");
            con.setRequestMethod("GET");
            Log.i("此时的path是：",path);
            try{
                Log.i("con的编码是：",con.getResponseCode()+"");
            }catch (Exception e){
                e.printStackTrace();
            }
            if (con.getResponseCode() == 200) {
                Log.i("DownloadUtil里面","3");
                InputStream is = con.getInputStream();//获取输入流
                FileOutputStream fileOutputStream = null;//文件输出流
                if (is != null) {
                    FileUtils fileUtils = new FileUtils();
                    Log.i("DownloadUtil里面","4");
                    fileOutputStream = new FileOutputStream(fileUtils.createFile(filename));//指定文件保存路径，代码看下一步
                    Log.i("DownloadUtil里面","5");
                    byte[] buf = new byte[1024];
                    int ch;
                    while ((ch = is.read(buf)) != -1) {
                        fileOutputStream.write(buf, 0, ch);//将获取到的流写入文件中
                    }
                    Log.i("DownloadUtil里面","下载图片好了");
                }
                if (fileOutputStream != null) {
                    fileOutputStream.flush();
                    fileOutputStream.close();
                }
            }else{
                Log.i("DownloadUtil里面","6");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
