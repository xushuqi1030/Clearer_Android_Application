package com.example.keeps_000.clearer.view;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewTreeObserver;

/**
 * Created by keeps_000 on 2018/4/3.
 */

public class EditImageView extends android.support.v7.widget.AppCompatImageView implements ViewTreeObserver.OnGlobalLayoutListener{

    private boolean mOnce = false;

    //初始化时缩放的值
    private float mInitScale;

    private Matrix mScaleMatrix;

    public EditImageView(Context context) {
        this(context,null);
    }

    public EditImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EditImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //init
        mScaleMatrix = new Matrix();
        setScaleType(ScaleType.MATRIX);

    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        getViewTreeObserver().addOnGlobalLayoutListener(this);

    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        getViewTreeObserver().removeGlobalOnLayoutListener(this);
    }

    @Override
    public void onGlobalLayout() {
        if(!mOnce){
            //Log.e("msg","hello~");
            //得到控件的宽和高
            int width = getWidth();
            int height = getHeight();
            Log.e("控件的宽和高",width+" "+height);
            //得到我们的图片，以及宽和高
            Drawable d = getDrawable();
            if(d == null){
                Log.e("异常点","111");
                return;
            }

            int dw = d.getIntrinsicWidth();
            int dh = d.getIntrinsicHeight();
            Log.e("图片的宽和高",dw+" "+dh);

            float scale = 1.0f;

            /**
             * 如果图片的宽度大于控件宽度，但是高度小于控件高度；我们将其缩小
             */
            if(dw > width && dh < height){
                scale = width * 1.0f / dw;
            }

            /**
             * 如果图片的高度大于控件高度，但是宽度小于控件宽度；我们将其缩小
             */
            if(dh > height && dw < width){
                scale = height * 1.0f / dh;
            }

            if((dw > width && dh > height) || (dw < width && dh < height)){
                scale = Math.min(width * 1.0f / dw, height * 1.0f / dh);
            }

            //得到初始化时缩放的比例
            mInitScale = scale;
            //将图片移动至控件的中心
            int dx = getWidth() / 2 - dw / 2;
            int dy = getHeight() / 2 - dh / 2;

            mScaleMatrix.postTranslate(dx,dy);
            mScaleMatrix.postScale(mInitScale,mInitScale,width/2,height/2);
            setImageMatrix(mScaleMatrix);
            mOnce = true;
        }else{
            //已经初始化过一次了
            //这里需要判断图片是否居中，是否缩放到合适的大小
            int width = getWidth();
            int height = getHeight();
            Log.e("控件的宽和高",width+" "+height);
            //得到我们的图片，以及宽和高
            Drawable d = getDrawable();
            if(d == null){
                Log.e("异常点","222");
                return;
            }

            int dw = d.getIntrinsicWidth();
            int dh = d.getIntrinsicHeight();
            Log.e("图片的宽和高",dw+" "+dh);

            float scale = 1.0f;

            /**
             * 如果图片的宽度大于控件宽度，但是高度小于控件高度；我们将其缩小
             */
            if(dw > width && dh < height){
                scale = width * 1.0f / dw;
            }

            /**
             * 如果图片的高度大于控件高度，但是宽度小于控件宽度；我们将其缩小
             */
            if(dh > height && dw < width){
                scale = height * 1.0f / dh;
            }

            if((dw > width && dh > height) || (dw < width && dh < height)){
                scale = Math.min(width * 1.0f / dw, height * 1.0f / dh);
            }

            //得到初始化时缩放的比例
            mInitScale = scale;
            //将图片移动至控件的中心
            int dx = getWidth() / 2 - dw / 2;
            int dy = getHeight() / 2 - dh / 2;

            mScaleMatrix = new Matrix();
            setScaleType(ScaleType.MATRIX);
            mScaleMatrix.postTranslate(dx,dy);
            mScaleMatrix.postScale(mInitScale,mInitScale,width/2,height/2);
            setImageMatrix(mScaleMatrix);
        }


    }
}

