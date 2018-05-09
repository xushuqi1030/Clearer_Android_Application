package com.example.keeps_000.clearer.activity;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.keeps_000.clearer.R;

public class AboutUsActivity extends AppCompatActivity {

    private Button btn_back;
    private Button btn_how_to_use;
    private Button btn_fankui;
    private Button btn_team;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        btn_back = findViewById(R.id.btn_back_3);
        btn_fankui = findViewById(R.id.btn_fankui);
        btn_how_to_use = findViewById(R.id.btn_how_to_use);
        btn_team = findViewById(R.id.btn_team);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btn_how_to_use.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AboutUsActivity.this,FunctionIntroduceActivity.class);
                startActivity(intent);
                //Toast.makeText(AboutUsActivity.this, "该功能正在开发中...", Toast.LENGTH_SHORT).show();
            }
        });

        btn_team.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AboutUsActivity.this,TeamInfoActivity.class);
                startActivity(intent);
                // Toast.makeText(AboutUsActivity.this, "该功能正在开发中...", Toast.LENGTH_SHORT).show();
            }
        });

        btn_fankui.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:15980927287"));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
    }
}
