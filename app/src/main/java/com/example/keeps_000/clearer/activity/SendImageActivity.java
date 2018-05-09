package com.example.keeps_000.clearer.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.example.keeps_000.clearer.R;
import com.example.keeps_000.clearer.dialog.SendingDialogUtils;
import com.example.keeps_000.clearer.image.ImageUtils;
import com.example.keeps_000.clearer.singleton.ImageSingleton;
import com.example.keeps_000.clearer.upload.UploadThread;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class SendImageActivity extends AppCompatActivity {

    private GridView gridView1;              //网格显示缩略图
    private Button buttonPublish;            //发送图片按钮
    private Button buttonCancel;             //取消发送按钮
    private final int IMAGE_OPEN = 1;        //打开图片标记
    private final int IMAGE_EDIT = 2;        //图片局部剪切
    private final int IMAGE_CAMERA = 3;      //拍摄照片标记
    private String pathImage;                //选择图片路径
    private Bitmap bmp;                      //导入临时图片
    private ArrayList<HashMap<String, Object>> imageItem;
    private SimpleAdapter simpleAdapter;     //适配器
    private int tempimagetap = -1;           //临时照片position标记
    private List<Bitmap> bitmaps;
    private boolean isSendSuccess = false;
    private List<File> files;                //用于存放临时的照片
    private Dialog mSendingDialog;
    private Button btn_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_image);

        btn_back = findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        bitmaps = new ArrayList<Bitmap>();
        files = new ArrayList<File>();
        buttonPublish = findViewById(R.id.btn_send_picture);
        buttonCancel = (Button) findViewById(R.id.btn_cancel_send);

//        //锁定屏幕
//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//        setContentView(R.layout.activity_send_image);
        //获取控件对象
        gridView1 = (GridView) findViewById(R.id.gridView1);

        /*
         * 载入默认图片添加图片加号
         * 通过适配器实现
         * SimpleAdapter参数imageItem为数据源 R.layout.griditem_addpic为布局
         */
        //获取资源图片加号
        bmp = BitmapFactory.decodeResource(getResources(), R.drawable.gridview_addpic);
        imageItem = new ArrayList<HashMap<String, Object>>();
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("itemImage", bmp);
        imageItem.add(map);
        simpleAdapter = new SimpleAdapter(this,
                imageItem, R.layout.griditem_addpic,
                new String[] { "itemImage"}, new int[] { R.id.imageView1});

        /*
         * HashMap载入bmp图片在GridView中不显示,但是如果载入资源ID能显示 如
         * map.put("itemImage", R.drawable.img);
         * 解决方法:
         *              1.自定义继承BaseAdapter实现
         *              2.ViewBinder()接口实现
         *  参考 http://blog.csdn.net/admin_/article/details/7257901
         */
        simpleAdapter.setViewBinder(new SimpleAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Object data,
                                        String textRepresentation) {
                // TODO Auto-generated method stub
                if(view instanceof ImageView && data instanceof Bitmap){
                    ImageView i = (ImageView)view;
                    i.setImageBitmap((Bitmap) data);
                    return true;
                }
                return false;
            }
        });
        gridView1.setAdapter(simpleAdapter);

        /*
         * 监听GridView点击事件
         * 报错:该函数必须抽象方法 故需要手动导入import android.view.View;
         */
        gridView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id)
            {
                if( imageItem.size() == 8 && position == 0) { //第一张为默认图片
                    Toast.makeText(SendImageActivity.this, "图片数7张已满", Toast.LENGTH_SHORT).show();
                }
                else if(position == 0) { //点击图片位置为+ 0对应0张图片
                    Toast.makeText(SendImageActivity.this, "添加图片", Toast.LENGTH_SHORT).show();
                    //选择图片
                    dialogChoice();
                    //通过onResume()刷新数据
                }
                else {
                    //点击到图片上了
                    Log.e("msg", String.valueOf(position));
                    HashMap<String, Object> hashmap = new HashMap<String, Object>();
                    hashmap = imageItem.get(position);
                    Bitmap bitmap = (Bitmap) hashmap.get("itemImage");
                    ImageSingleton imageSingleton = ImageSingleton.getInstance();
                    imageSingleton.setBitmap(bitmap);
                    tempimagetap = position;
                    Intent intent = new Intent(SendImageActivity.this,EditImageActivity.class);
                    startActivityForResult(intent, IMAGE_EDIT);
                    //通过onResume()刷新数据
                }
            }
        });

        gridView1.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long l) {
                if( position != 0 ) { //第一张为默认图片
                    dialog(position);
                }
                return true;
            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        buttonPublish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.testtest);
//                        bitmaps.add(bitmap);
//                        sendImageFiles(bitmaps);
//                    }
//                }).start();
                if(imageItem.size() == 1){
                    Toast.makeText(SendImageActivity.this, "请选择至少一张待处理图片", Toast.LENGTH_SHORT).show();
                }else{
                    //发布图片到服务器上
                    //首先创建临时文件！遍历适配器里的bitmap
                    int flag = 0;
                    for(int i = 1; i<imageItem.size();i++){
                        HashMap<String, Object> hashmap = new HashMap<String, Object>();
                        hashmap = imageItem.get(i);
                        Bitmap bitmap = (Bitmap) hashmap.get("itemImage");
                        if(bitmap.getHeight() > 300 || bitmap.getWidth() > 300){
                            flag = 1;
                        }
                        bitmaps.add(bitmap);
                    }
                    //先检查是否每张图片大小都是小于300*300的
                    if(flag == 1){
                        //有一张甚至多张图片大小超出限制
                        Toast.makeText(SendImageActivity.this, "至少一张图片超出大小，请点击图片进行裁剪！", Toast.LENGTH_SHORT).show();
                    }else{
                        Log.i("TestPoint","10");
                        Log.i("应该有几张图？", String.valueOf(bitmaps.size()));
                        //在这里触发上传的动图
                        mSendingDialog = SendingDialogUtils.createSendingDialog(SendImageActivity.this, "图片传送中...");
                        mSendingDialog.show();
                        Log.i("dialog_status", String.valueOf(mSendingDialog.isShowing()));
                        //在这里进入上传的函数
                        Log.i("TestPoint","5");
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                isSendSuccess = sendImageFiles(bitmaps);
                                Log.i("TestPoint","6");
                                if(isSendSuccess){
                                    //上传成功了
                                    //结束上传的动图
                                    Log.i("TestPoint","send,success");
                                    SendingDialogUtils.closeDialog(mSendingDialog);
                                    Toast.makeText(SendImageActivity.this, "图片已上传到服务器处理，请耐心等待并留意通知！", Toast.LENGTH_SHORT).show();
                                    finish();
                                }else{
                                    //上传失败了
                                    //结束上传的动图
                                    Log.i("TestPoint","send,false");
                                    SendingDialogUtils.closeDialog(mSendingDialog);
                                    Toast.makeText(SendImageActivity.this, "图片上传失败！请检查网络后重试！", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }).start();
                    }
                }
            }
        });
    }

    private void dialogChoice() {
        final String items[] = {"拍摄一张照片", "从相册中选取"};
        final int[] cho = {0};
        AlertDialog.Builder builder = new AlertDialog.Builder(SendImageActivity.this);
        builder.setTitle("请选择添加图片方式");
        builder.setSingleChoiceItems(items, 0,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.i("which",""+which);
                        cho[0] = which;
                        Log.i("cho[0]",""+cho[0]);
                    }
                });
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if(cho[0] == 0){
                    //拍摄一张图片
                    //Toast.makeText(SendImageActivity.this, "拍摄一张照片", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, IMAGE_CAMERA);
                }else{
                    //从相册中选取
                    //Toast.makeText(SendImageActivity.this, "从相册中选取", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, IMAGE_OPEN);
                }
            }
        });
        builder.create().show();
    }

    private boolean sendImageFiles(List<Bitmap> bitmaps) {
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyymmdd_hh_mm_ss");
        String time = dateFormat.format(date);
        Log.e("time",time);
        try{
            if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
                Log.i("TestPoint","8");
                File sdcardPath = Environment.getExternalStorageDirectory();
                for(int i = 0;i<bitmaps.size();i++){
                    FileOutputStream fos;
                    String filename = time + "No" + String.valueOf(i+1) + ".jpg";
                    File file = new File(sdcardPath,filename);
                    if(!file.exists()){
                        try {
                            file.createNewFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    byte[] bytes = ImageUtils.Bitmap2Bytes(bitmaps.get(i));
                    fos = new FileOutputStream(file);
                    fos.write(bytes);
                    fos.close();
                    files.add(file);
                }
                //到此图片保存到本地完毕，马上是发送的操作
                CountDownLatch countDownLatch = new CountDownLatch(1);
                UploadThread u = new UploadThread(countDownLatch,files);
                Thread thread = new Thread(u);
                thread.start();
                try{
                    countDownLatch.await();
                    bitmaps.clear();
                    for(int k = 0;k<files.size();k++){
                        if(files.get(k).delete()){
                            Log.i("手机文件"+String.valueOf(k+1)+"删除状态","ture");
                        }else{
                            Log.i("手机文件"+String.valueOf(k+1)+"删除状态","false");
                        }
                    }
                    files.clear();
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
                Log.i("msg","成功了成功了");
                return true;
            }else{
                Log.i("TestPoint","9");
                return false;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }


    //获取图片路径 响应startActivityForResult
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //打开图片
        if(resultCode==RESULT_OK && requestCode==IMAGE_OPEN) {
            Uri uri = data.getData();
            if (!TextUtils.isEmpty(uri.getAuthority())) {
                //查询选择图片
                Cursor cursor = getContentResolver().query(
                        uri,
                        new String[] { MediaStore.Images.Media.DATA },
                        null,
                        null,
                        null);
                //返回 没找到选择图片
                if (null == cursor) {
                    return;
                }
                //光标移动至开头 获取图片路径
                cursor.moveToFirst();
                pathImage = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
        }  //end if 打开图片
        else if(resultCode==RESULT_OK && requestCode==IMAGE_EDIT){
            //接收intent回传的结果
            ImageSingleton imageSingleton = ImageSingleton.getInstance();
            Bitmap newbitmap = imageSingleton.getBitmap();
            HashMap<String, Object> map1 = new HashMap<String, Object>();
            map1.put("itemImage", newbitmap);
            imageItem.set(tempimagetap,map1);
            tempimagetap = -1;
            simpleAdapter.notifyDataSetChanged();
        }else if(resultCode==RESULT_OK && requestCode==IMAGE_CAMERA && data != null){
            Bundle bundle = data.getExtras();
            Bitmap bitmap = (Bitmap) bundle.get("data");
            HashMap<String, Object> map2 = new HashMap<String, Object>();
            map2.put("itemImage", bitmap);
            imageItem.add(map2);
            simpleAdapter = new SimpleAdapter(this,
                    imageItem, R.layout.griditem_addpic,
                    new String[] { "itemImage"}, new int[] { R.id.imageView1});
            simpleAdapter.setViewBinder(new SimpleAdapter.ViewBinder() {
                @Override
                public boolean setViewValue(View view, Object data,
                                            String textRepresentation) {
                    // TODO Auto-generated method stub
                    if(view instanceof ImageView && data instanceof Bitmap){
                        ImageView i = (ImageView)view;
                        i.setImageBitmap((Bitmap) data);
                        return true;
                    }
                    return false;
                }
            });
            gridView1.setAdapter(simpleAdapter);
            simpleAdapter.notifyDataSetChanged();
        }
    }

    //刷新图片
    @Override
    protected void onResume() {
        super.onResume();
        if(!TextUtils.isEmpty(pathImage)){
            Bitmap addbmp=BitmapFactory.decodeFile(pathImage);
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("itemImage", addbmp);
            imageItem.add(map);
            simpleAdapter = new SimpleAdapter(this,
                    imageItem, R.layout.griditem_addpic,
                    new String[] { "itemImage"}, new int[] { R.id.imageView1});
            simpleAdapter.setViewBinder(new SimpleAdapter.ViewBinder() {
                @Override
                public boolean setViewValue(View view, Object data,
                                            String textRepresentation) {
                    // TODO Auto-generated method stub
                    if(view instanceof ImageView && data instanceof Bitmap){
                        ImageView i = (ImageView)view;
                        i.setImageBitmap((Bitmap) data);
                        return true;
                    }
                    return false;
                }
            });
            gridView1.setAdapter(simpleAdapter);
            simpleAdapter.notifyDataSetChanged();
            //刷新后释放防止手机休眠后自动添加
            pathImage = null;
        }
    }

    /*
     * Dialog对话框提示用户删除操作
     * position为删除图片位置
     */
    protected void dialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(SendImageActivity.this);
        builder.setMessage("确认移除已添加图片吗？");
        builder.setTitle("提示");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                imageItem.remove(position);
                simpleAdapter.notifyDataSetChanged();
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
