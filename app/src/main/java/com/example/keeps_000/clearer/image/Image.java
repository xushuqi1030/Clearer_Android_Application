package com.example.keeps_000.clearer.image;

import android.graphics.Bitmap;

/**
 * Created by keeps_000 on 2018/4/16.
 */

public class Image {
    private String name;
    private Bitmap bitmap;
    private String path;

    public Image(String name, Bitmap bitmap, String path) {
        this.name = name;
        this.bitmap = bitmap;
        this.path = path;
    }

    public Image() {
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {

        return name;

    }

    public void setName(String name) {
        this.name = name;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
}
