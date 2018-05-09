package com.example.keeps_000.clearer.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.graphics.PixelFormat;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.media.Image;
import android.media.ImageReader;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.keeps_000.clearer.R;
import com.example.keeps_000.clearer.image.ImageUtils;
import com.example.keeps_000.clearer.singleton.DeviceSingleton;
import com.example.keeps_000.clearer.singleton.ImageSingleton;
import com.example.keeps_000.clearer.view.DrawImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class CameraActivity extends AppCompatActivity {

    private SurfaceView surfaceView;
    private Button btn_back;
    private DrawImageView drawImageView;
    private SurfaceHolder mSurfaceHolder;
    private CameraManager mCameraManager;//摄像头管理器
    private String mCameraID = "0";//摄像头Id 0 为后  1 为前
    private CameraCaptureSession mCameraCaptureSession;
    private CameraDevice mCameraDevice;
    private Handler childHandler, mainHandler;
    private ImageReader mImageReader;
    private Button btn_select;
    private boolean flag2 = false;

    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();

    //为了使照片竖直显示
    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        drawImageView = findViewById(R.id.view_drawimageview);
        drawImageView.bringToFront();

        btn_back = findViewById(R.id.btn_back_8);
        btn_back.bringToFront();
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btn_select = findViewById(R.id.btn_select);
        btn_select.bringToFront();
        btn_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!flag2){
                    Toast.makeText(CameraActivity.this,"请先选择局部预览!",Toast.LENGTH_SHORT).show();
                }else{
                    flag2 = false;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Log.i("hello","开始拍照了");
                            CaptureRequest.Builder captureRequestBuilder;
                            try{
                                captureRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
                                captureRequestBuilder.addTarget(mImageReader.getSurface());
                                // 自动对焦
                                captureRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
                                // 自动曝光
                                captureRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
                                CaptureRequest mCaptureRequest = captureRequestBuilder.build();
                                mCameraCaptureSession.capture(mCaptureRequest,null,childHandler);
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }
            }
        });

        surfaceView = findViewById(R.id.view_surfaceview);
        surfaceView.setZOrderOnTop(false);
        mSurfaceHolder = surfaceView.getHolder();
        mSurfaceHolder.setFormat(PixelFormat.TRANSPARENT);
        mSurfaceHolder.setKeepScreenOn(true);
        mSurfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                initCamera2();
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                // 释放Camera资源
                if (null != mCameraDevice) {
                    mCameraDevice.close();
                    CameraActivity.this.mCameraDevice = null;
                }
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean flag = true;
                while (flag){
                    DeviceSingleton deviceSingleton = DeviceSingleton.getInstance();
                    String temp = deviceSingleton.getIsTakePicture();
                    String temp2 = deviceSingleton.getIsSelected();
                    if(temp2.equals("Yes")){
                        flag2 = true;
                    }
                    if(temp.equals("Yes")){
                        flag = false;
                        deviceSingleton.setIsTakePicture("No");
                    }else{
                        try{
                            Thread.sleep(1000);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }
                Log.i("hello","开始拍照了");
                CaptureRequest.Builder captureRequestBuilder;
                try{
                    captureRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
                    captureRequestBuilder.addTarget(mImageReader.getSurface());
                    // 自动对焦
                    captureRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
                    // 自动曝光
                    captureRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
                    CaptureRequest mCaptureRequest = captureRequestBuilder.build();
                    mCameraCaptureSession.capture(mCaptureRequest,null,childHandler);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 初始化Camera2
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void initCamera2() {
        HandlerThread handlerThread = new HandlerThread("Camera2");
        handlerThread.start();
        childHandler = new Handler(handlerThread.getLooper());
        mainHandler = new Handler(getMainLooper());
        mCameraID = "" + CameraCharacteristics.LENS_FACING_FRONT;//后摄像头
        mImageReader = ImageReader.newInstance(1080, 1920, ImageFormat.JPEG,3);
        mImageReader.setOnImageAvailableListener(new ImageReader.OnImageAvailableListener() { //可以在这里处理拍照得到的临时照片 例如，写入本地
            @Override
            public void onImageAvailable(ImageReader reader) {
                // 拿到拍照照片数据
                Image image = reader.acquireLatestImage();
                String result = saveImage(image,"temp.jpg");
                Log.i("result",result);
                Intent intent = new Intent(CameraActivity.this,ShowActivity.class);
                startActivity(intent);
                finish();
            }
        }, mainHandler);
        //获取摄像头管理
        mCameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            //打开摄像头
            mCameraManager.openCamera(mCameraID, stateCallback, mainHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private String saveImage(Image image, String name) {
        String result = "true";
        try{
            File sdcardPath = Environment.getExternalStorageDirectory();
            FileOutputStream fos;
            File file = new File(sdcardPath, name);
            if(!file.exists()){
                file.createNewFile();
            }
            ByteBuffer buffer = image.getPlanes()[0].getBuffer();
            byte[] bytes = new byte[buffer.remaining()];
            buffer.get(bytes);
            Bitmap bitmap = ImageUtils.Bytes2Bimap(bytes);
            Bitmap newbitmap = ImageUtils.rotaingImageView(90,bitmap);
            DeviceSingleton deviceSingleton = DeviceSingleton.getInstance();
            int x = deviceSingleton.getX();
            int y = deviceSingleton.getY();
            int screenX = deviceSingleton.getScreenWidth();
            int screenY = deviceSingleton.getScreenHeight();
            //截取
            System.out.println("照片宽和高："+newbitmap.getWidth()+" "+newbitmap.getHeight());
            float scaleX = (float) screenX / (float) newbitmap.getWidth();
            float scaleY = (float) screenY / (float) newbitmap.getHeight();
            int targetX = (int)((float) x / scaleX);
            int targetY = (int)((float) y / scaleY);
            newbitmap = Bitmap.createBitmap(newbitmap,targetX,targetY,100,100);
            byte[] newbytes = ImageUtils.Bitmap2Bytes(newbitmap);
            fos = new FileOutputStream(file);
            fos.write(newbytes);
            fos.close();

            ImageSingleton imageSingleton = ImageSingleton.getInstance();
            imageSingleton.setBitmap(newbitmap);

        }catch (Exception e){
            result = "false";
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 摄像头创建监听
     */
    private CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(CameraDevice camera) {//打开摄像头
            mCameraDevice = camera;
            //开启预览
            takePreview();
        }

        @Override
        public void onDisconnected(CameraDevice camera) {//关闭摄像头
            if (null != mCameraDevice) {
                mCameraDevice.close();
                CameraActivity.this.mCameraDevice = null;
            }
        }

        @Override
        public void onError(CameraDevice camera, int error) {//发生错误
            Toast.makeText(CameraActivity.this, "摄像头开启失败", Toast.LENGTH_SHORT).show();
        }
    };

    /**
     * 开始预览
     */
    private void takePreview() {
        try {
            // 创建预览需要的CaptureRequest.Builder
            final CaptureRequest.Builder previewRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            // 将SurfaceView的surface作为CaptureRequest.Builder的目标
            previewRequestBuilder.addTarget(mSurfaceHolder.getSurface());
            // 创建CameraCaptureSession，该对象负责管理处理预览请求和拍照请求
            mCameraDevice.createCaptureSession(Arrays.asList(mSurfaceHolder.getSurface(), mImageReader.getSurface()), new CameraCaptureSession.StateCallback() // ③
            {
                @Override
                public void onConfigured(CameraCaptureSession cameraCaptureSession) {
                    if (null == mCameraDevice) return;
                    // 当摄像头已经准备好时，开始显示预览
                    mCameraCaptureSession = cameraCaptureSession;
                    try {
                        //自动对焦
                        previewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE,CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
                        //自动闪光灯
                        previewRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE,CaptureRequest.CONTROL_AE_MODE_OFF);
                        // 显示预览
                        CaptureRequest previewRequest = previewRequestBuilder.build();
                        mCameraCaptureSession.setRepeatingRequest(previewRequest, null, childHandler);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onConfigureFailed(CameraCaptureSession cameraCaptureSession) {
                    Toast.makeText(CameraActivity.this, "配置失败", Toast.LENGTH_SHORT).show();
                }
            }, childHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }


}
