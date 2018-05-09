package com.example.keeps_000.clearer.teaminfo;

import android.graphics.Bitmap;

/**
 * Created by keeps_000 on 2018/4/17.
 */

public class Worker {
    private String name;
    private Bitmap bitmap;

    public Worker() {
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

    public Worker(String name, Bitmap bitmap) {

        this.name = name;
        this.bitmap = bitmap;
    }
}
