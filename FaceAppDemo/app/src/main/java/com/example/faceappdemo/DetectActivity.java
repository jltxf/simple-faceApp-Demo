package com.example.faceappdemo;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.megvii.facepp.api.FacePPApi;
import com.megvii.facepp.api.IFacePPCallBack;
import com.megvii.facepp.api.bean.DetectResponse;
import com.megvii.facepp.api.bean.Face;
import com.megvii.facepp.api.bean.HumanBodyDetectResponse;
import com.megvii.facepp.api.face.IFaceApi;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.Buffer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DetectActivity extends AppCompatActivity {
    private static final String API_KEY = "p1H3HLwyhdUGQ67wg0sagmq_9BFgeUwz";
    private static final String API_SECRET = "1rJ_w53BWHwqfBRA5tActjw4DG2w4A4j";
    private final int PHOTO_REQUEST_CODE = 100;
    private ImageView ivPic;
    private Button btnCamera, btnDetect, btnGallery, btnSave;
    private String picPath;
    private Bitmap bitmap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detect);
        initView();
        initEvents();
    }

    private void initEvents() {
        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(DetectActivity.this, "btnCamera", Toast.LENGTH_LONG).show();
            }
        });
        btnDetect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*使用APi*/
                Map<String, String> params = new HashMap<>();//要解析的相关参数
                params.put("return_attributes", "gender,age");//return_attributes要检测的人脸参数
                params.put("return_landmark", "0");//return_landmark要检测的人脸关键点，我们这离不检测，设置为0

                FacePPApi faceppApi = new FacePPApi(API_KEY,API_SECRET);//API内的Object类





                faceppApi.detect(params, toByteArray(bitmap), new IFacePPCallBack<DetectResponse>() {
                    @Override
                    public void onSuccess(DetectResponse detectResponse) {
                        // 自己的业务处理
                        Canvas canvas = new Canvas(bitmap);//若要在bitmap上创建一个Canvas，bitmap必须为可变
                        //图中的人脸不止一个
                        for (Face face : detectResponse.getFaces()) {//detectResponse.getFaces()返回的人脸对象集
                            //face中包含了人脸在图中的范围
                            int height = face.getFace_rectangle().getHeight();//getFace_rectangle返回人脸的范围
                            int width = face.getFace_rectangle().getWidth();
                            int left = face.getFace_rectangle().getLeft();//人脸左上角顶点的x坐标
                            int top = face.getFace_rectangle().getTop();//人脸左上角顶点的y坐标

                            int right = left + width;//人脸右下角顶点的x坐标
                            int bottom = top + height;//人脸you下角顶点的y坐标
                            Paint paint = new Paint();
                            paint.setColor(Color.YELLOW);
                            paint.setStyle(Paint.Style.STROKE);//画线
                            paint.setStrokeWidth(1);//线条大小
                            canvas.drawRect(left, top, right, bottom, paint);

                            /**
                             * TypedValue.applyDimension 将dp值转化成像素值
                             * TypedValue.COMPLEX_UNIT_DIP 第一个参数，表示要转化成像素前的单位为dp
                             *第二个参数为20 要改变的数字
                             * 第三个参数为DisplayMetrics,是手机屏幕大小的实例（像素密度），这里采用 getResources().getDisplayMetrics()
                             */
                            /**
                             * distance 红色矩形离框位置的距离
                             * rectWdith 红色矩形的宽
                             *rectHeight红色矩形的高这里都是像素值
                             * 问题：据屏幕像素密度转换的像素可能与图片的像素密度不一致
                             */
                            int distance = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, getResources().getDisplayMetrics());
                            int rectWdith = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, getResources().getDisplayMetrics());
                            int rectHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, getResources().getDisplayMetrics());

                            //解决：与图片的像素密度尺寸进行按比例转化
                            //getResources().getDisplayMetrics().heightPixels 屏幕垂直方向的像素
                            distance = distance * bitmap.getHeight() / getResources().getDisplayMetrics().heightPixels;
                            rectWdith = rectWdith * bitmap.getWidth() / getResources().getDisplayMetrics().widthPixels;
                            rectHeight = rectHeight * bitmap.getHeight() / getResources().getDisplayMetrics().heightPixels;

                            //黄色框的中心点的坐标
                            int faceCenterX = left + width / 2;
                            int faceCenterY = top + height / 2;
                            //红色矩形背景的中心点的坐标
                            int rectCenterX = faceCenterX;
                            int rectCenterY = faceCenterY - distance - rectHeight / 2 - height / 2;
                            //获取红色矩形的左上顶点和右下顶点的坐标
                            int rectLeft = rectCenterX - rectWdith / 2;
                            int rectTop = rectCenterY - rectHeight / 2;
                            int rectRight = rectCenterX + rectWdith / 2;
                            int rectButtom = rectCenterY + rectHeight / 2;


                            paint.setColor(Color.RED);
                            paint.setStyle(Paint.Style.FILL);//Paint.Style.FILL画笔风格是实心的：矩形填满
                            canvas.drawRect(rectLeft, rectTop, rectRight, rectButtom, paint);

                            paint.setColor(Color.WHITE);
                            String sex = null;
                            if (face.getAttributes().getGender().getValue().equals("Female")) {
                                sex = "女";//face.getAttributes()人脸的数据对象
                            } else {
                                sex = "男";
                            }
                            String contnet = sex + " " + face.getAttributes().getAge().getValue() + "(+-5)岁";
                            int textSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 18, getResources().getDisplayMetrics());
                            textSize = textSize * bitmap.getWidth() / getResources().getDisplayMetrics().widthPixels;
                            Rect textRect = new Rect();
                            paint.setTextSize(textSize);
                            paint.getTextBounds(contnet, 0, contnet.length(), textRect);//传值
                            paint.setTextAlign(Paint.Align.CENTER);//居中绘写
                            canvas.drawText(contnet, rectCenterX, rectCenterY + textRect.height() / 2, paint);
                        }
                        ivPic.post(new Runnable() {
                            @Override
                            public void run() {
                                ivPic.invalidate();//重新加载一次原来的对象
                                //非主线程不能刷新控件
                            }
                        });
                        Log.i("Faceapp", "on scuuess");
                    }

                    @Override
                    public void onFailed(String s) {
                        // 自己的失败处理
                        Log.i("Faceapp", "on fail");
                    }
                });
            }
        });
        btnGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //隐式打开图库
//                   <data android:host=""
//                android:path=""/>
                //MediaStore.Images.Media.EXTERNAL_CONTENT_URI图片的主机名和路径名 具体的action Intent.ACTION_PICK
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, PHOTO_REQUEST_CODE);
            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    //Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) 手机有没有SD卡
                    Toast.makeText(DetectActivity.this, "访问sD卡失败，不能保存图片", Toast.LENGTH_LONG).show();
                }
                //(Environment.getExternalStorageDirectory().getAbsolutePath()SD卡绝对路径 文件夹来的
                File bimapDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "faceAppPic");//查看此路径
                if (!bimapDir.exists()) {//exists存在返回true
                    bimapDir.mkdir();//创建出所有级别的文件夹
                }

                File picFile = new File(bimapDir, getnewPicName());//真正的文件存储
                try {
                    picFile.createNewFile();//
                    BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(picFile));//输出流
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                    //Bitmap.CompressFormat.JPEG 输出类型 100 输出流文件缩放比， bos 输出流
                    bos.flush();
                    bos.close();
                    //对文件系统发出"扫描图库"的广播
                    Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(picFile));
                    sendBroadcast(intent);//广播发送
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            private String getnewPicName() {
                SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmsssss");
                String str = format.format(new Date()) + ".jpeg";

                return str;
            }
        });
    }

    private void initView() {
        ivPic = (ImageView) findViewById(R.id.iv_pic);
        btnCamera = (Button) findViewById(R.id.btn_camera);
        btnDetect = (Button) findViewById(R.id.btn_detect);
        btnGallery = (Button) findViewById(R.id.btn_gallery);
        btnSave = (Button) findViewById(R.id.btn_save);
    }


    //
    // 因为 我们通过startActivityForResult()打开了图库，则返回时回调了此方法
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Intent里面放的是选择的东西的路径
        if (requestCode == PHOTO_REQUEST_CODE) {//是打开图片的那个Activity的
            if (resultCode == RESULT_OK) {//已经选择了图片后才返回
                /*这个uri是指向图片的多媒体数据库路径 可以认为是操作contentProvider的路径
                每个图片等多媒体都有一条数据连接存放在数据库，这个数据是图片的详细信息，例如地址，iso大小等*/
                Uri uri = data.getData();//获取Intent里面的数据

                //Cursor 游标，默认指向第一条数据
                Cursor cursor = getContentResolver().query(uri, null, null, null, null);
                cursor.moveToFirst();//让cursor指向第一条记录

                /*cursor.getString();获取列的值，参数为int，是它的列
                cursor.getColumnIndex()通过cursor游标的列名来获取他在游标中的列号
                MediaStore.Images.Media.DATA 指向列的列名 _data*/
                picPath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));//得到图片的文件路径


                //1.首先获取原图的大小
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;//inJustDecodeBounds指示加载图片的边界
                BitmapFactory.decodeFile(picPath, options);//从文件系统中将文件解码成java对象
                /*而返回一个bitmap对象，但是此时bitmap为空，图片并没有被加载进内存
                而设置了options.outHeight;options.outWidth即options的宽和高为picpath的宽和高，并加载进内存*/
                int outHeight = options.outHeight;//原图的大小
                int outWidgh = options.outWidth;//原图的大小

                int ivPicHeight = ivPic.getHeight();//目标的宽和高
                int ivPicWidth = ivPic.getWidth();

                int radioHeight = outHeight / ivPicHeight;//缩放
                int radioWidth = outWidgh / ivPicWidth;
                int radio = Math.max(radioHeight, radioWidth); //2.进行图片压缩比例确定
//3.根据图片比例进行压缩
                options.inJustDecodeBounds = false;//inJustDecodeBounds指示不再加载图片的边界
                options.inSampleSize = radio;//采样比例
                options.inMutable = true;//使bitmap可变，默认不可变
                bitmap = BitmapFactory.decodeFile(picPath, options);//从文件系统中将文件解码成java对象
                //此时图片被加载


                ivPic.setImageBitmap(bitmap);
            }
        }
    }

    public static byte[] toByteArray(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
        return baos.toByteArray();
    }
}
