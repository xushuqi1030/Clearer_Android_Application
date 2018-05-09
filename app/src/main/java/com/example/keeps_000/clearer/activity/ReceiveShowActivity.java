package com.example.keeps_000.clearer.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.keeps_000.clearer.R;
import com.example.keeps_000.clearer.dialog.SendingDialogUtils;
import com.example.keeps_000.clearer.download.DownloadThread;
import com.example.keeps_000.clearer.download.DownloadUtil;
import com.example.keeps_000.clearer.singleton.DeviceSingleton;
import com.example.keeps_000.clearer.singleton.MessageSingleton;
import com.example.keeps_000.clearer.view.EditImageView;
import com.example.keeps_000.clearer.viewpage.MyViewPager;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class ReceiveShowActivity extends AppCompatActivity {

    private String IMAGEURL = "http://139.199.153.122:666/";
    private Dialog mSendingDialog;
    private MyViewPager myViewPager;
    private String pictures_list_name;
    private int pictures_num;
    private String device_id;
    private String[] temp;
    private String time;
    private String path;
    private List<Bitmap> bitmapList = new ArrayList<Bitmap>();
    private Button btn_back;
    private TextView textView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive_show);

        Log.i("hello","ReceiveShowActivity");

        btn_back = findViewById(R.id.btn_back_7);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                messageDialog();
            }
        });

        //testmethod();

        MessageSingleton messageSingleton = MessageSingleton.getInstance();
        pictures_list_name = messageSingleton.getPictures_list_name();
        pictures_num = messageSingleton.getPictures_num();
        DeviceSingleton deviceSingleton = DeviceSingleton.getInstance();
        device_id = deviceSingleton.getmDeviceID();

        Log.i("进到里面后num",pictures_num+"");
        Log.i("进到里面后name",pictures_list_name);
        Log.i("进到里面后device_id",device_id);

        temp = pictures_list_name.split("-");
        Log.i("temp里的时间是",temp[2]);
        time = temp[2];

        path = Environment.getExternalStorageDirectory().toString() + "/Clearer/Pictures";

//        //在这里触发上传的动图
//        mSendingDialog = SendingDialogUtils.createSendingDialog(ReceiveShowActivity.this, "图片加载中...");
//        mSendingDialog.show();

        //从这里下载图片
        downloadPictures();
        //这里是把图片放到布局上

        Log.i("到这里","图片下载完毕了且加载到list里面");
        Log.i("list里面数量",""+bitmapList.size());

        myViewPager = findViewById(R.id.id_viewPager);
        myViewPager.setAdapter(new PagerAdapter() {

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView((View)object);
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                EditImageView imageView = new EditImageView(ReceiveShowActivity.this);
                //这里是设置图片资源
                imageView.setImageBitmap(bitmapList.get(position));
                //imageView.setImageResource(mImgIds[position]);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                container.addView(imageView);
                myViewPager.setObjectForPosition(imageView, position);
                return imageView;
            }

            @Override
            public int getCount() {
                return bitmapList.size();
            }
        });

        textView = findViewById(R.id.text_title_5);
        textView.setText("图片展示(共"+bitmapList.size()+"张)");

//        SendingDialogUtils.closeDialog(mSendingDialog);

    }

    private void testmethod() {
        Bitmap bitmap1 = BitmapFactory.decodeResource(getResources(),R.mipmap.main_background1);
        bitmapList.add(bitmap1);
        Bitmap bitmap2 = BitmapFactory.decodeResource(getResources(),R.mipmap.main_background2);
        bitmapList.add(bitmap2);
        Bitmap bitmap3 = BitmapFactory.decodeResource(getResources(),R.mipmap.main_background3);
        bitmapList.add(bitmap3);
    }

    private void downloadPictures() {
        int i = 0;
        while(i < pictures_num){
            CountDownLatch countDownLatch2 = new CountDownLatch(1);
            DownloadThread downloadThread = new DownloadThread(countDownLatch2,IMAGEURL+pictures_list_name+"-"+String.valueOf(i)+".jpg",time+"-"+String.valueOf(i+1)+"-原图.jpg");
            Thread thread = new Thread(downloadThread);
            thread.start();
            try {
                countDownLatch2.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //DownloadUtil.download(IMAGEURL+pictures_list_name+"-"+String.valueOf(i)+".jpg", time+"-"+String.valueOf(i+1)+"-原图.jpg");
            CountDownLatch countDownLatch1 = new CountDownLatch(1);
            DownloadThread downloadThread1 = new DownloadThread(countDownLatch1,IMAGEURL+"out-"+pictures_list_name+"-"+String.valueOf(i)+".jpg",time+"-"+String.valueOf(i+1)+"-效果图.jpg");
            Thread thread1 = new Thread(downloadThread1);
            thread1.start();
            try {
                countDownLatch1.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //DownloadUtil.download(IMAGEURL+"out-"+pictures_list_name+"-"+String.valueOf(i)+".jpg",time+"-"+String.valueOf(i+1)+"-效果图.jpg");
            Log.i("下载第"+String.valueOf(i+1)+"组图片","done.");
            try {
                Bitmap bmp1 = BitmapFactory.decodeStream(new FileInputStream(new File(path, time+"-"+String.valueOf(i+1)+"-原图.jpg")));
                bitmapList.add(bmp1);
                Bitmap bmp2 = BitmapFactory.decodeStream(new FileInputStream(new File(path, time+"-"+String.valueOf(i+1)+"-效果图.jpg")));
                bitmapList.add(bmp2);
            }catch (Exception e){
                e.printStackTrace();
            }
            Log.i("加载第"+String.valueOf(i+1)+"组图片到控件","done.");
            i++;
        }
    }

    private void messageDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(ReceiveShowActivity.this);
        builder.setMessage("图片已保存，可以在“我的图片”里查看！");
        builder.setTitle("提示");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });
        builder.create().show();
    }

}
