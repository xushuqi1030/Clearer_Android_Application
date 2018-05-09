package com.example.keeps_000.clearer.tensorflow;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.os.Trace;
import android.util.Log;

import org.tensorflow.contrib.android.TensorFlowInferenceInterface;

import java.util.Date;

/**
 * Created by keeps_000 on 2018/4/19.
 */

public class ImageTensorflow {
    private static final String MODEL_FILE_X8 = "file:///android_asset/cele.pb"; //模型存放路径
    private static final int pix = 255;
    private float[] floatValues = null;
    private int[] intValues = null;
    private float[] outputs = null;

    TensorFlowInferenceInterface inferenceInterface;

    static {
        System.loadLibrary("tensorflow_inference");
    }

    public ImageTensorflow(final AssetManager assetManager){
        inferenceInterface = new TensorFlowInferenceInterface(assetManager,MODEL_FILE_X8);
    }

    public Bitmap getResultForHigh8(Bitmap bitmap,Bitmap bitmap_new){
        Log.i("msg:","apoint1");
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Log.i("msg:","apoint2");
        intValues = new int[width*height];
        bitmap.getPixels(intValues,0,bitmap.getWidth(),0,0, bitmap.getWidth(),bitmap.getHeight());
//        int p = 0;
//        for(int i = 0;i<height;i++){
//            for(int j = 0 ; j< width;j++){
//                System.out.print(intValues[p]+"");
//                p++;
//            }
//            System.out.println();
//        }
        Log.i("msg:","apoint3");
        floatValues = new float[width*height*3];
        for(int i=0;i<intValues.length;i++){
            final int val = intValues[i];
            floatValues[i*3+0] = (float)((val & 0xff0000) >> 16);
            floatValues[i*3+1] = (float)((val & 0xff00) >> 8);
            floatValues[i*3+2] = (float)((val & 0xff));
        }
        Log.i("msg:","apoint3_1");
        Bitmap bitmap_new_new = Bitmap.createScaledBitmap(bitmap_new,width*8,height*8,true);
        Log.i("bitmap_new_status",""+bitmap_new.isMutable());
        Log.i("bitmap_new_width",""+bitmap_new.getWidth());
        Log.i("bitmap_new_height",""+bitmap_new.getHeight());
        Log.i("bitmap_new_new_width",""+bitmap_new_new.getWidth());
        Log.i("bitmap_new_new_height",""+bitmap_new_new.getHeight());
        Log.i("bitmap_new_new_status",""+bitmap_new_new.isMutable());

        Log.i("msg:","apoint4");
        Trace.beginSection("feed");
        inferenceInterface.feed("input",floatValues,1,height,width,3);
        Trace.endSection();
        Log.i("msg:","apoint5");
        Date date1 = new Date();
        Trace.beginSection("run");
        String[] outputNames = new String[]{"output"};
        inferenceInterface.run(outputNames,false);
        Trace.endSection();
        Date date2 = new Date();
        long time = (date2.getTime() - date1.getTime());
        Log.i("模型时间",""+time);
        Log.i("msg:","apoint6");
        outputs = new float[(width*8)*(height*8)*3];
        Trace.beginSection("fetch");
        inferenceInterface.fetch("output",outputs);
        Trace.endSection();
        Log.i("msg:","apoint7");

        for(int j = 0;j<outputs.length;j++){
            outputs[j] = outputs[j] * pix;
        }
        Log.i("msg:","apoint8");
        int[] rgb = new int[outputs.length/3];
        Log.i("msg:","apoint9");
        for(int j = 0;j<outputs.length/3;j++){
            final float val1 = outputs[j*3+0];
            final float val2 = outputs[j*3+1];
            final float val3 = outputs[j*3+2];
            rgb[j] = (turnFloatToInt(val1) << 16) | (turnFloatToInt(val2) << 8) | turnFloatToInt(val3);
            if(rgb[j] > 8388608){
                rgb[j] = rgb[j] - 16777216;
            }
        }
        Log.i("msg:","apoint10");
        Log.i("msg:","apoint11");

        Log.i("msg:","apoint12");
        bitmap_new_new.setPixels(rgb,0,width*8,0,0,width*8,height*8);

        Log.i("msg:","apoint13");
        return bitmap_new_new;
    }

    private static int turnFloatToInt(float value){
        return (int)(value+0.5);
    }
}
