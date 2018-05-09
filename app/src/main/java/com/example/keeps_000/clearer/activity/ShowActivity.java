package com.example.keeps_000.clearer.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.keeps_000.clearer.R;
import com.example.keeps_000.clearer.dialog.SendingDialogUtils;
import com.example.keeps_000.clearer.image.ImageUtils;
import com.example.keeps_000.clearer.singleton.ImageSingleton;
import com.example.keeps_000.clearer.tensorflow.ImageTensorflow;
import com.example.keeps_000.clearer.view.CutFixedImageView;
import com.example.keeps_000.clearer.view.EditImageView;
import com.example.keeps_000.clearer.view.ZoomAndSmallImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ShowActivity extends AppCompatActivity {

    private ZoomAndSmallImageView showImageView;
    private CutFixedImageView cutImageView;
    private EditImageView editImageView;
    private Button btn_next;
    private Button btn_last;
    private int status = 1;
    private Bitmap bitmap;
    private Bitmap tempBitmap;
    private Dialog mSendingDialog;
    private Bitmap resultBitmap;
    private Bitmap tempBitmap2;
    private Bitmap tempBitmap3;
    private int flag = 0;
    private File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);

        final ImageSingleton imageSingleton = ImageSingleton.getInstance();
        bitmap = imageSingleton.getBitmap();

        showImageView = findViewById(R.id.ivShowPicture);
        showImageView.setImageBitmap(bitmap);

        cutImageView = findViewById(R.id.ivCutPicture);
        editImageView = findViewById(R.id.ivSurePicture);

        btn_next = findViewById(R.id.btn_next);
        btn_last = findViewById(R.id.btn_last);
        if(bitmap.getWidth() <= 100 && bitmap.getHeight() <= 100){
            Toast.makeText(ShowActivity.this,"您选择的图片大小符合处理条件！",Toast.LENGTH_SHORT).show();
            flag = 1;
            tempBitmap2 = bitmap;
            btn_next.setText("清晰处理");
        }else{
            Toast.makeText(ShowActivity.this,"请先调整好相片合适大小后进入下一步！",Toast.LENGTH_SHORT).show();
        }

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(flag == 1){
                    //首先告诉用户正在处理中
                    Toast.makeText(ShowActivity.this,"图片清晰化中，请耐心等候。",Toast.LENGTH_SHORT).show();
                    //黑屏、转圈动画
                    mSendingDialog = SendingDialogUtils.createSendingDialog(ShowActivity.this, "图片处理中...");
                    mSendingDialog.show();
                    //功能调用
                    Log.i("要处理的图片宽度",""+tempBitmap2.getWidth());
                    Log.i("要处理的图片高度",""+tempBitmap2.getHeight());
                    Log.i("当前图片可否修改？",""+tempBitmap2.isMutable());
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            ImageTensorflow imageTensorflow = new ImageTensorflow(getAssets());
                            resultBitmap = imageTensorflow.getResultForHigh8(tempBitmap2,tempBitmap2);
                            SendingDialogUtils.closeDialog(mSendingDialog);
                            Toast.makeText(ShowActivity.this,"图片清晰化完成！",Toast.LENGTH_SHORT).show();
                            imageSingleton.setBitmap(tempBitmap2);
                            imageSingleton.setBitmap_result(resultBitmap);
                            Intent intent = new Intent(ShowActivity.this,ResultActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }).start();
                    //调用结束了
                }else{
                    if(status == 1){
                        //截取控件大小的bitmap
                        showImageView.setDrawingCacheEnabled(true);
                        showImageView.buildDrawingCache();
                        tempBitmap = showImageView.getDrawingCache(false);
                        cutImageView.setImageBitmap(tempBitmap);
                        showImageView.setVisibility(View.INVISIBLE);
                        cutImageView.setVisibility(View.VISIBLE);
                        status = 2;
                        btn_next.setText("剪切");
                        btn_last.setText("上一步");
                        Toast.makeText(ShowActivity.this,"目前仅支持100*100大小的图片！",Toast.LENGTH_SHORT).show();
                    }else if(status == 2){
                        tempBitmap = cutImageView.getCutBitmap();
                        editImageView.setImageBitmap(tempBitmap);
                        cutImageView.setVisibility(View.INVISIBLE);
                        editImageView.setVisibility(View.VISIBLE);
                        status = 3;
                        btn_next.setText("清晰处理");
                        btn_last.setText("回头重做");
                    }else{
                        try{
                            File sdcardPath = Environment.getExternalStorageDirectory();
                            file = new File(sdcardPath,"xushuqi_temp.png");
                            if(!file.exists()){
                                try {
                                    file.createNewFile();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            byte[] bytes = ImageUtils.Bitmap2Bytes(tempBitmap);
                            FileOutputStream fos = new FileOutputStream(file);
                            fos.write(bytes);
                            fos.close();
                            BitmapFactory.Options options = new BitmapFactory.Options();
                            options.inMutable = true;
                            tempBitmap3 = BitmapFactory.decodeFile(file.getPath(),options);
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                        //Toast.makeText(ShowActivity.this,"该功能正在开发中...",Toast.LENGTH_SHORT).show();
                        //首先告诉用户正在处理中
                        Toast.makeText(ShowActivity.this,"图片清晰化中，请耐心等候。",Toast.LENGTH_SHORT).show();
                        //黑屏、转圈动画
                        mSendingDialog = SendingDialogUtils.createSendingDialog(ShowActivity.this, "图片处理中...");
                        mSendingDialog.show();
                        //功能调用
                        Log.i("要处理的图片宽度",""+tempBitmap3.getWidth());
                        Log.i("要处理的图片高度",""+tempBitmap3.getHeight());
                        Log.i("当前图片可否修改？",""+tempBitmap3.isMutable());
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                ImageTensorflow imageTensorflow = new ImageTensorflow(getAssets());
                                resultBitmap = imageTensorflow.getResultForHigh8(tempBitmap3,tempBitmap3);
                                SendingDialogUtils.closeDialog(mSendingDialog);
                                Toast.makeText(ShowActivity.this,"图片清晰化完成！",Toast.LENGTH_SHORT).show();
                                imageSingleton.setBitmap(tempBitmap3);
                                imageSingleton.setBitmap_result(resultBitmap);
                                Intent intent = new Intent(ShowActivity.this,ResultActivity.class);
                                startActivity(intent);
                                file.delete();
                                finish();
                            }
                        }).start();
                        //调用结束了
                    }
                }
            }
        });

        btn_last.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(status == 1){
                    finish();
                }else if(status == 2){
                    Log.i("pointkkk","1");
                    tempBitmap = null;
                    Log.i("pointkkk","2");
                    cutImageView.setImageBitmap(bitmap);
                    Log.i("pointkkk","3");
                    cutImageView.setVisibility(View.INVISIBLE);
                    Log.i("pointkkk","4");
                    showImageView.setVisibility(View.VISIBLE);
                    Log.i("pointkkk","5");
                    status = 1;
                    Log.i("pointkkk","6");
                    btn_next.setText("下一步");
                    Log.i("pointkkk","7");
                    btn_last.setText("取消");
                    Log.i("pointkkk","8");
                    Toast.makeText(ShowActivity.this,"请先调整好相片合适大小后进入下一步！",Toast.LENGTH_SHORT).show();
                    Log.i("pointkkk","9");
                    showImageView.destroyDrawingCache();
                    Log.i("pointkkk","10");
                }else{
                    status = 1;
                    tempBitmap = null;
                    btn_next.setText("下一步");
                    btn_last.setText("取消");
                    editImageView.setImageBitmap(null);
                    cutImageView.setImageBitmap(bitmap);
                    editImageView.setVisibility(View.INVISIBLE);
                    showImageView.setVisibility(View.VISIBLE);
                    Toast.makeText(ShowActivity.this,"请先调整好相片合适大小后进入下一步！",Toast.LENGTH_SHORT).show();
                    showImageView.destroyDrawingCache();
                }
            }
        });

    }
}
