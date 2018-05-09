package com.example.keeps_000.clearer.splash;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;

import com.example.keeps_000.clearer.activity.MainActivity;
import com.example.keeps_000.clearer.R;

/**
 * Created by keeps_000 on 2018/3/21.
 */

public class SplashActivity extends Activity{

    private static final long DELAY_TIME = 1000L;
    private Button btn_pass;
    private boolean once = false;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                btn_pass.setText("2:跳过");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        btn_pass.setText("1:跳过");
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                btn_pass.setText("0:跳过");
                                nextMain();
                            }
                        },DELAY_TIME);
                    }
                },DELAY_TIME);
            }
        },DELAY_TIME);

        btn_pass = findViewById(R.id.btn_pass);
        btn_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextMain();
            }
        });
    }

    private void nextMain() {
        if(!once){
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            finish();
        }
        once = true;
    }
}
