package com.example.keeps_000.clearer.singleton;

/**
 * Created by keeps_000 on 2018/4/21.
 */

public class MessageSingleton {

    private String pictures_list_name;
    private int pictures_num;

    public String getPictures_list_name() {
        return pictures_list_name;
    }

    public void setPictures_list_name(String pictures_list_name) {
        this.pictures_list_name = pictures_list_name;
    }

    public int getPictures_num() {
        return pictures_num;
    }

    public void setPictures_num(int pictures_num) {
        this.pictures_num = pictures_num;
    }

    private static volatile MessageSingleton instance = null;

    public static MessageSingleton getInstance(){
        if(instance == null){
            synchronized (MessageSingleton.class){
                if(instance == null){
                    instance = new MessageSingleton();
                }
            }
        }
        return instance;
    }
}
