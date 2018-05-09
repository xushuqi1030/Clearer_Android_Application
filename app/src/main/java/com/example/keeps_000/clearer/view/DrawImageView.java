package com.example.keeps_000.clearer.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.WindowManager;

import com.example.keeps_000.clearer.singleton.DeviceSingleton;

/**
 * Created by keeps_000 on 2018/4/24.
 */

public class DrawImageView extends android.support.v7.widget.AppCompatImageView {

    private int lastX;
    private int lastY;
    private int thisX;
    private int thisY;
    private long lastDownTime;
    private long thisEventTime;
    private boolean isLongPressed;
    private int myleft;
    private int mytop;
    private int mScreenWidth;
    private int mScreenHeight;
    private Paint paint;
    private boolean flag;

    public DrawImageView(Context context) {
        this(context,null);
    }

    public DrawImageView(Context context, AttributeSet attrs) {
        this(context,attrs,0);
        // TODO Auto-generated constructor stub
    }

    public DrawImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        getScreenMetrix(context);
        initView(context);
    }

    private void getScreenMetrix(Context context) {
        WindowManager WM = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        WM.getDefaultDisplay().getMetrics(outMetrics);
        mScreenWidth = outMetrics.widthPixels;
        mScreenHeight = outMetrics.heightPixels;
    }

    private void initView(Context context) {
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(5);
        paint.setStyle(Paint.Style.STROKE);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        super.onDraw(canvas);

        Log.i("myleft",myleft+"");
        Log.i("mytop",mytop+"");
        if(flag){
            canvas.drawRect(new Rect(myleft, mytop, myleft+100, mytop+100), paint);//绘制矩形
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                flag = true;
                DeviceSingleton deviceSingleton = DeviceSingleton.getInstance();
                deviceSingleton.setIsSelected("Yes");
                lastX = (int) event.getX();
                lastY = (int) event.getY();
                lastDownTime = event.getDownTime();
                int x = (int) event.getX();
                int y = (int) event.getY();
                x -= 50;
                y -= 50;
                if(x < 0){
                    x = 0;
                }
                if(y < 0){
                    y = 0;
                }
                if(x + 100 > mScreenWidth){
                    x = mScreenWidth - 100;
                }
                if(y + 100 > mScreenHeight){
                    y = mScreenHeight - 100;
                }
                myleft = x;
                mytop = y;
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                thisX = (int) event.getX();
                thisY = (int) event.getY();
                x = (int) event.getX();
                y = (int) event.getY();
                thisEventTime = event.getEventTime();
                int num = 0;
                if(!isLongPressed){
                    isLongPressed = isLongPressed(lastX,lastY,thisX,thisY,lastDownTime,thisEventTime,1000);
                    num = 1;
                }
                if(isLongPressed){
                    //长按时间
                    if(num == 1){
                        Log.i("长按事件！","hello");
                        x -= 50;
                        y -= 50;
                        deviceSingleton = DeviceSingleton.getInstance();
                        deviceSingleton.setIsTakePicture("Yes");
                        deviceSingleton.setX(x);
                        deviceSingleton.setY(y);
                        deviceSingleton.setScreenWidth(mScreenWidth);
                        deviceSingleton.setScreenHeight(mScreenHeight);
                        System.out.println("XY点分别是："+x+" "+y);
                        System.out.println("空间宽高分别是："+mScreenWidth+" "+mScreenHeight);
                    }else{

                    }
                }else{
                    x -= 50;
                    y -= 50;
                    if(x < 0){
                        x = 0;
                    }
                    if(y < 0){
                        y = 0;
                    }
                    if(x + 100 > mScreenWidth){
                        x = mScreenWidth - 100;
                    }
                    if(y + 100 > mScreenHeight){
                        y = mScreenHeight - 100;
                    }
                    myleft = x;
                    mytop = y;
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
                lastX = 0;
                lastY = 0;
                thisX = 0;
                thisY = 0;
                lastDownTime = 0;
                thisEventTime = 0;
                isLongPressed = false;
                x = (int) event.getX();
                y = (int) event.getY();
                x -= 50;
                y -= 50;
                deviceSingleton = DeviceSingleton.getInstance();
                deviceSingleton.setX(x);
                deviceSingleton.setY(y);
                deviceSingleton.setScreenWidth(mScreenWidth);
                deviceSingleton.setScreenHeight(mScreenHeight);
                if(x < 0){
                    x = 0;
                }
                if(y < 0){
                    y = 0;
                }
                if(x + 100 > mScreenWidth){
                    x = mScreenWidth - 100;
                }
                if(y + 100 > mScreenHeight){
                    y = mScreenHeight - 100;
                }
                myleft = x;
                mytop = y;
                invalidate();
                return true;
        }
        return true;
    }

    private boolean isLongPressed(float lastX, float lastY, float thisX, float thisY, long lastDownTime, long thisEventTime, long longPressTime) {
        float offsetX = Math.abs(thisX - lastX);
        float offsetY = Math.abs(thisY - lastY);
        long intervalTime = thisEventTime - lastDownTime;
        if (offsetX <= 10 && offsetY <= 10 && intervalTime >= longPressTime) {
            return true;
        }
        return false;
    }

}
