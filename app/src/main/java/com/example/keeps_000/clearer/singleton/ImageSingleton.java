package com.example.keeps_000.clearer.singleton;

import android.graphics.Bitmap;

/**
 * Created by keeps_000 on 2018/3/25.
 */

public class ImageSingleton {
    Bitmap bitmap = null;
    Bitmap bitmap_result = null;

    public Bitmap getBitmap_result() {
        return bitmap_result;
    }

    public void setBitmap_result(Bitmap bitmap_result) {
        this.bitmap_result = bitmap_result;
    }

    private ImageSingleton(){


    }

    private static volatile ImageSingleton instance = null;

    public static ImageSingleton getInstance(){
        if(instance == null){
            synchronized (ImageSingleton.class){
                if(instance == null){
                    instance = new ImageSingleton();
                }
            }
        }
        return instance;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
}
