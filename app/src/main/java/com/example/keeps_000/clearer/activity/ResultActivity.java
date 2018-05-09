package com.example.keeps_000.clearer.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.keeps_000.clearer.R;
import com.example.keeps_000.clearer.image.ImageUtils;
import com.example.keeps_000.clearer.singleton.ImageSingleton;
import com.example.keeps_000.clearer.view.ZoomImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ResultActivity extends AppCompatActivity {

    private Button btn_back;
    private ZoomImageView result_view_1;
    private ZoomImageView result_view_2;
    private Button btn_save_picture;
    private Button btn_dont_save;
    private String picture_name = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        final ImageSingleton imageSingleton = ImageSingleton.getInstance();

        result_view_1 = findViewById(R.id.result_view_1);
        result_view_2 = findViewById(R.id.result_view_2);
        result_view_1.setImageBitmap(imageSingleton.getBitmap());
        result_view_2.setImageBitmap(imageSingleton.getBitmap_result());

        btn_back = findViewById(R.id.btn_back_6);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btn_dont_save = findViewById(R.id.btn_dont_save);
        btn_dont_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                outdialog();
            }
        });

        btn_save_picture = findViewById(R.id.btn_save_picture);
        btn_save_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //保存图片到本地的操作
                inputdialog();
            }
        });
    }

    private void outdialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ResultActivity.this);
        builder.setMessage("您不保存图片到本地吗？");
        builder.setTitle("提示");
        builder.setPositiveButton("不保存", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });
        builder.setNegativeButton("稍等", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    private void inputdialog(){
        final EditText editText = new EditText(ResultActivity.this);
        AlertDialog.Builder builder = new AlertDialog.Builder(ResultActivity.this);
        builder.setTitle("请为组图命名").setView(editText);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                //Toast.makeText(ResultActivity.this, editText.getText().toString(), Toast.LENGTH_SHORT).show();
                picture_name = editText.getText().toString();
                Log.i("pic_name",picture_name);

                if(picture_name.equals("")){
                    Log.i("pic_name121",picture_name);
                    Toast.makeText(ResultActivity.this,"组图命名不能为空！", Toast.LENGTH_SHORT).show();
                }else{
                    Log.i("pic_name_212",picture_name);
                    File sdcardPath = Environment.getExternalStorageDirectory();
                    File file = new File(sdcardPath,"Clearer/Pictures");
                    if(!file.exists()){
                        boolean flag = file.mkdirs();
                        Log.i("flag", String.valueOf(flag));
                    }else {
                        Log.i("msg","目标文件夹已存在");
                    }
                    File file1 = new File(file,picture_name+"_原图.png");
                    if(file1.exists()){
                        //命名重复了
                        picture_name = null;
                        Toast.makeText(ResultActivity.this,"该命名已存在，请重新命名！", Toast.LENGTH_SHORT).show();
                    }else{
                        try {
                            file1.createNewFile();
                            ImageSingleton imageSingleton = ImageSingleton.getInstance();
                            FileOutputStream fos;
                            byte[] pic1 = ImageUtils.Bitmap2Bytes(imageSingleton.getBitmap());
                            fos = new FileOutputStream(file1);
                            fos.write(pic1);
                            fos.close();

                            File file2 = new File(file,picture_name+"_效果图.png");
                            file2.createNewFile();
                            byte[] pic2 = ImageUtils.Bitmap2Bytes(imageSingleton.getBitmap_result());
                            fos = new FileOutputStream(file2);
                            fos.write(pic2);
                            fos.close();
                            Toast.makeText(ResultActivity.this,"组图保存成功，可以在“我的图片”查看！", Toast.LENGTH_SHORT).show();
                            finish();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }
}
