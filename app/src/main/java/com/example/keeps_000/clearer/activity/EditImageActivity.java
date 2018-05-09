package com.example.keeps_000.clearer.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


import com.example.keeps_000.clearer.R;
import com.example.keeps_000.clearer.singleton.ImageSingleton;
import com.example.keeps_000.clearer.view.CutFixedImage300View;
import com.example.keeps_000.clearer.view.EditImageView;

public class EditImageActivity extends AppCompatActivity {

    private CutFixedImage300View imageView;
    private EditImageView editImageView;
    private Button btn_yes;
    private Button btn_no;
    private int btn_yes_state = 1;
    private int btn_no_state = 1;
    private Bitmap bitmaptemp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_image);

        Toast.makeText(EditImageActivity.this, "目前仅支持300*300大小的照片", Toast.LENGTH_SHORT).show();

        Intent intent = getIntent();
        btn_yes = findViewById(R.id.btn_yes);
        btn_no = findViewById(R.id.btn_no);

        final ImageSingleton imageSingleton = ImageSingleton.getInstance();
        Bitmap bitmap = imageSingleton.getBitmap();

        imageView = findViewById(R.id.ivEditPicture);

        editImageView = findViewById(R.id.ivEdit2Picture);

        imageView.setImageBitmap(bitmap);

        btn_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(btn_no_state == 1){
                    finish();
                }else if(btn_no_state == 2){
                    btn_no_state = 1;
                    btn_yes_state = 1;
                    btn_yes.setText("剪切");
                    btn_no.setText("取消");
                    editImageView.setVisibility(View.INVISIBLE);
                    imageView.setVisibility(View.VISIBLE);
                    editImageView.setImageBitmap(null);
                }
            }
        });

        btn_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(btn_yes_state == 1){
                    bitmaptemp = imageView.getCutBitmap();
                    editImageView.setImageBitmap(bitmaptemp);
                    imageView.setVisibility(View.INVISIBLE);
                    editImageView.setVisibility(View.VISIBLE);
                    btn_yes.setText("确定");
                    btn_no.setText("返回");
                    btn_yes_state = 2;
                    btn_no_state = 2;
                }else if(btn_yes_state == 2){
                    //保存剪切后的图
                    imageSingleton.setBitmap(bitmaptemp);
                    setResult(RESULT_OK);
                    finish();
                }
            }
        });


    }
}
