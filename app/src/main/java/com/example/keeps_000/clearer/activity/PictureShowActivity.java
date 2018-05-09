package com.example.keeps_000.clearer.activity;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.keeps_000.clearer.R;
import com.example.keeps_000.clearer.singleton.ImageSingleton;

public class PictureShowActivity extends AppCompatActivity {

    private ImageView imageView;
    private Button btn_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_show);

        ImageSingleton imageSingleton = ImageSingleton.getInstance();
        Bitmap bitmap = imageSingleton.getBitmap();

        imageView = (com.example.keeps_000.clearer.view.ZoomImageView) findViewById(R.id.ivShow);
        imageView.setImageBitmap(bitmap);

        btn_back = findViewById(R.id.btn_back_2);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
