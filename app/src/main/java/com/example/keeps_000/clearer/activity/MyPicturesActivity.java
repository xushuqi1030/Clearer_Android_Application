package com.example.keeps_000.clearer.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.example.keeps_000.clearer.R;
import com.example.keeps_000.clearer.image.Image;
import com.example.keeps_000.clearer.image.ImageAdapter;
import com.example.keeps_000.clearer.singleton.ImageSingleton;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MyPicturesActivity extends AppCompatActivity {

    private List<Image> imageList = new ArrayList<Image>();
    private Button btn_back;
    private File[] files;
    private ImageAdapter imageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_pictures);

        initFiles();
        imageAdapter = new ImageAdapter(MyPicturesActivity.this,R.layout.image_item,imageList);
        ListView listView = (ListView) findViewById(R.id.list_view);
        listView.setAdapter(imageAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Image image = imageList.get(position);
                ImageSingleton imageSingleton = ImageSingleton.getInstance();
                imageSingleton.setBitmap(image.getBitmap());
                Intent intent = new Intent(MyPicturesActivity.this,PictureShowActivity.class);
                startActivity(intent);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                dialog(position);
                return true;
            }
        });

        btn_back = findViewById(R.id.btn_back_1);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void dialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MyPicturesActivity.this);
        builder.setMessage("确认要删除该图片吗？");
        builder.setTitle("提示");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Image image = imageList.get(position);
                File file = new File(image.getPath());
                if(!file.exists()){
                    Log.i("要删除的文件","不存在");
                }else{
                    Log.i("要删除的文件","存在");
                    boolean flag = file.delete();
                    Log.i("调用删除后的状态",""+flag);
                }
                imageList.remove(position);
                imageAdapter.notifyDataSetChanged();
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

    private void initFiles() {
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            getExternalFilesDirs(null).toString();
            File sdcardPath = Environment.getExternalStorageDirectory();
            File file = new File(sdcardPath,"Clearer/Pictures");
            if(!file.exists()){
                boolean flag = file.mkdirs();
                Log.i("flag", String.valueOf(flag));
            }else {
                Log.i("msg","目标文件夹已存在");
            }
            files = file.listFiles();
            Log.i("文件夹下文件数量", String.valueOf(files.length));
            for(int i = 0; i<files.length;i++){
                Bitmap bitmap = BitmapFactory.decodeFile(files[i].getPath());
                Image image = new Image(files[i].getName(),bitmap,files[i].getPath());
                imageList.add(image);
            }
        }
    }
}
