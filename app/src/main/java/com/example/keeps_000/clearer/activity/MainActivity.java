package com.example.keeps_000.clearer.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.keeps_000.clearer.R;
import com.example.keeps_000.clearer.singleton.DeviceSingleton;
import com.example.keeps_000.clearer.singleton.ImageSingleton;
import com.example.keeps_000.clearer.singleton.MessageSingleton;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private static int RESULT_LOAD_IMAGE = 1;
    private static int RESULT_CAMERA = 2;

    private Button btn_take_photo;
    private Button btn_use_photo;
    private Button btn_send_pictures;
    private Button btn_about_us;
    private Button btn_my_pictures;
    private Button btn_out;
    private Button btn_setting;
    private Button btn_high_camera;

    private String mDeviceID;
    private String host = "tcp://139.199.153.122:61613";
    private String userName = "admin";
    private String passWord = "password";
    private Handler handler;
    private MqttClient client;
    private String myTopic = "Topic_MQTT_Clearer";
    private MqttConnectOptions options;
    private ScheduledExecutorService scheduler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDeviceID = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        Log.i("设备ID",mDeviceID);
        DeviceSingleton deviceSingleton = DeviceSingleton.getInstance();
        deviceSingleton.setmDeviceID(mDeviceID);

        if(ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.INTERNET)!= PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.READ_PHONE_STATE)!= PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.ACCESS_NETWORK_STATE)!= PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.VIBRATE)!= PackageManager.PERMISSION_GRANTED
                ){
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.INTERNET,Manifest.permission.READ_PHONE_STATE,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.VIBRATE,Manifest.permission.ACCESS_NETWORK_STATE},1);
            Log.i("TestPoint","1");
        }else{
            Log.i("TestPoint","2");
            initView();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String[] permissions,int[] grantResults) {
        switch (requestCode){
            case 1:
                if (grantResults.length==7&&grantResults[0]==PackageManager.PERMISSION_GRANTED&&grantResults[1]==PackageManager.PERMISSION_GRANTED&&grantResults[2]==PackageManager.PERMISSION_GRANTED&&grantResults[3]==PackageManager.PERMISSION_GRANTED&&grantResults[4]==PackageManager.PERMISSION_GRANTED&&grantResults[5]==PackageManager.PERMISSION_GRANTED&&grantResults[6]==PackageManager.PERMISSION_GRANTED){
                    Log.i("TestPoint","3");
                    initView();
                }else {
                    //这里是拒绝给APP摄像头权限，给个提示什么的说明一下都可以。
                    Toast.makeText(MainActivity.this,"请手动打开权限",Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    //初始化开始界面
    private void initView(){
        startMqttConnection();

        Log.i("TestPoint","4");
        btn_take_photo = findViewById(R.id.take_photo);
        btn_use_photo = findViewById(R.id.use_photo);
        btn_send_pictures = findViewById(R.id.send_pictures);
        btn_about_us = findViewById(R.id.about_us);
        btn_my_pictures = findViewById(R.id.my_pictures);
        btn_out = findViewById(R.id.btn_out);
        btn_setting = findViewById(R.id.btn_setting);
        btn_high_camera = findViewById(R.id.btn_high_camera);

        btn_high_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(MainActivity.this,"该功能正在开发中...",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this,CameraActivity.class);
                startActivity(intent);
            }
        });

        btn_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,AboutUsActivity.class);
                startActivity(intent);
            }
        });

        btn_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog();
//                Intent intent = new Intent(MainActivity.this,ReceiveShowActivity.class);
//                startActivity(intent);
            }
        });

        btn_take_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //调用摄像头拍一张照片
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent,RESULT_CAMERA);
            }
        });
        btn_use_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //从手机相册里拿照片
                //打开本地相册
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                //设定结果返回
                startActivityForResult(intent,RESULT_LOAD_IMAGE);
            }
        });
        btn_send_pictures.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,SendImageActivity.class);
                startActivity(intent);
            }
        });
        btn_about_us.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,AboutUsActivity.class);
                startActivity(intent);
            }
        });
        btn_my_pictures.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,MyPicturesActivity.class);
                startActivity(intent);
            }
        });
    }

    private void startMqttConnection() {
        init();
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if(msg.what == 1) {
                    Log.i("MQTT连接情况",(String) msg.obj);
                } else if(msg.what == 2) {
                    Log.i("MQTT连接情况","连接成功");
                    try {
                        client.subscribe(myTopic, 1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if(msg.what == 3) {
                    Log.i("MQTT连接情况","连接失败，系统正在重连");
                }
            }
        };
        startReconnect();
    }

    //从新连接
    private void startReconnect() {
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                if(!client.isConnected()) {
                    connect();
                }
            }
        }, 0 * 1000, 10 * 1000, TimeUnit.MILLISECONDS);
    }

    private void init() {
        try {
            //host为主机名，test为clientid即连接MQTT的客户端ID，一般以客户端唯一标识符表示，MemoryPersistence设置clientid的保存形式，默认为以内存保存
            client = new MqttClient(host, "id_xushuqi",new MemoryPersistence());
            //MQTT的连接设置
            options = new MqttConnectOptions();
            //设置是否清空session,这里如果设置为false表示服务器会保留客户端的连接记录，这里设置为true表示每次连接到服务器都以新的身份连接
            options.setCleanSession(true);
            //设置连接的用户名
            options.setUserName(userName);
            //设置连接的密码
            options.setPassword(passWord.toCharArray());
            // 设置超时时间 单位为秒
            options.setConnectionTimeout(10);
            // 设置会话心跳时间 单位为秒 服务器会每隔1.5*20秒的时间向客户端发送个消息判断客户端是否在线，但这个方法并没有重连的机制
            options.setKeepAliveInterval(20);
            //设置回调
            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    //连接丢失后，一般在这里面进行重连
                    System.out.println("connectionLost----------");
                }
                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    //publish后会执行到这里
                    System.out.println("deliveryComplete---------" + token.isComplete());
                }
                @Override
                public void messageArrived(String topicName, org.eclipse.paho.client.mqttv3.MqttMessage message)
                        throws Exception {
                    //subscribe后得到的消息会执行到这里面
                    Message msg = new Message();
                    //Toast.makeText(MainActivity.this,message.toString(),Toast.LENGTH_SHORT);
                    msg.what = 1;
                    msg.obj = topicName+"---"+message.toString();
                    handler.sendMessage(msg);
                    Log.i("收到消息==>",message.toString());
                    String[] temp = message.toString().split("_");
                    //解析收到的订阅消息，看是否是本机的消息
                    if(temp[0].equals("图片处理完毕") && temp[1].equals(mDeviceID)){
                        int pictures_num = Integer.valueOf(temp[2]);
                        Log.i("这次的图片有几张？",""+pictures_num);
                        String pictures_list_name = temp[3];
                        Log.i("这次图片的系列名称是：",pictures_list_name);
                        Intent intent = new Intent(MainActivity.this,ReceiveShowActivity.class);
                        MessageSingleton messageSingleton = MessageSingleton.getInstance();
                        messageSingleton.setPictures_num(pictures_num);
                        messageSingleton.setPictures_list_name(pictures_list_name);
                        //是本机的消息
                        //这里想做一个发送消息抬头的功能
                        NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(MainActivity.this);
                        PendingIntent pendingIntent = PendingIntent.getActivity(MainActivity.this,0,intent,0);
                        mBuilder.setContentTitle("Clearer消息")//设置通知栏标题
                                .setContentText("您的图片已清晰化完毕，请点击查看！") //设置通知栏显示内容
                                .setContentIntent(pendingIntent) //设置通知栏点击意图
                                .setTicker("通知到来") //通知首次出现在通知栏，带上升动画效果的
                                .setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示，一般是系统获取到的时间
                                .setAutoCancel(true)//设置这个标志当用户单击面板就可以让通知将自动取消
                                .setSmallIcon(R.mipmap.icon_clearer);//设置通知小ICON
                        Notification notification = mBuilder.build();
                        int id = 199;
                        mNotificationManager.notify(id, notification);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //连接mqtt
    private void connect() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    client.connect(options);
                    Message msg = new Message();
                    msg.what = 2;
                    handler.sendMessage(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                    Message msg = new Message();
                    msg.what = 3;
                    handler.sendMessage(msg);
                }
            }
        }).start();
    }
    //当按返回键的时候，退出程序需然后断开连接
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(client != null && keyCode == KeyEvent.KEYCODE_BACK) {
            try {
                client.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return super.onKeyDown(keyCode, event);
    }
    //销毁
    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            scheduler.shutdown();
            client.disconnect();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private void dialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage("您确认要退出吗？");
        builder.setTitle("提示");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if(client != null) {
                    try {
                        client.disconnect();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                finish();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            //获取返回的数据，这里是android自定义的Uri地址
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            //获取选择照片的数据视图
            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();
            //从数据视图中获取已选择图片的路径
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inMutable = true;
            Bitmap bitmap = BitmapFactory.decodeFile(picturePath,options);
            ImageSingleton imageSingleton = ImageSingleton.getInstance();
            imageSingleton.setBitmap(bitmap);
            Intent intent = new Intent(MainActivity.this,ShowActivity.class);
            startActivity(intent);
        } else if (requestCode == RESULT_CAMERA && resultCode == RESULT_OK && null != data) {
            Bundle bundle = data.getExtras(); // 从data中取出传递回来缩略图的信息，图片质量差，适合传递小图片
            Bitmap bitmap = (Bitmap) bundle.get("data"); // 将data中的信息流解析为Bitmap类型
            ImageSingleton imageSingleton = ImageSingleton.getInstance();
            imageSingleton.setBitmap(bitmap);
            Intent intent = new Intent(MainActivity.this,ShowActivity.class);
            startActivity(intent);
        }
    }
}