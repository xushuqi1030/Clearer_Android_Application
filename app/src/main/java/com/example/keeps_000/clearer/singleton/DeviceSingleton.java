package com.example.keeps_000.clearer.singleton;

/**
 * Created by keeps_000 on 2018/4/20.
 */

public class DeviceSingleton {

    private String mDeviceID;
    private String isTakePicture = "No";
    private String isSelected = "No";
    private int X;
    private int Y;
    private int screenWidth;

    public String getIsSelected() {
        return isSelected;
    }

    public void setIsSelected(String isSelected) {
        this.isSelected = isSelected;
    }

    public int getScreenWidth() {
        return screenWidth;
    }

    public void setScreenWidth(int screenWidth) {
        this.screenWidth = screenWidth;
    }

    public int getScreenHeight() {
        return screenHeight;
    }

    public void setScreenHeight(int screenHeight) {
        this.screenHeight = screenHeight;
    }

    private int screenHeight;

    public int getX() {
        return X;
    }

    public void setX(int x) {
        X = x;
    }

    public int getY() {
        return Y;
    }

    public void setY(int y) {
        Y = y;
    }

    public String getIsTakePicture() {
        return isTakePicture;
    }

    public void setIsTakePicture(String isTakePicture) {
        this.isTakePicture = isTakePicture;
    }

    public String getmDeviceID() {
        return mDeviceID;
    }

    public void setmDeviceID(String mDeviceID) {
        this.mDeviceID = mDeviceID;
    }

    private static volatile DeviceSingleton instance = null;

    public static DeviceSingleton getInstance(){
        if(instance == null){
            synchronized (DeviceSingleton.class){
                if(instance == null){
                    instance = new DeviceSingleton();
                }
            }
        }
        return instance;
    }


}
