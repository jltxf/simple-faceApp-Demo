package com.example.faceappdemo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.text.DecimalFormat;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    private  final int ALL_PERMISSIONS_REQUEST_COOE = 100;
    //权限数组
private final String[] permissions = new String[]{Manifest.permission.INTERNET,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        boolean  isPermissionsGranted =true;//是否所有权限已获取
        for(String permission : permissions){//检查所有权限是否已获取
            if(ContextCompat.checkSelfPermission(this,permission)!=PackageManager.PERMISSION_GRANTED){//PackageManager.PERMISSION_GRANTED已经获取的权限的值
                isPermissionsGranted =false;
                break;
            }
        }
        if(isPermissionsGranted){//权限已经完全获取
            Timer timer = new Timer();//
            timer.schedule(new TimerTask() {//定时执行run()中的动作
                @Override
                public void run() {
                    Intent intent = new Intent(MainActivity.this, DetectActivity.class);
                    startActivity(intent);
                }
            },3000);
        }else {
            //动态申请
            ActivityCompat.requestPermissions(this,permissions,ALL_PERMISSIONS_REQUEST_COOE);
        }
    }

    /*动态申请会回调onRequestPermissionsResult(),
    ((ActivityCompat.OnRequestPermissionsResultCallback)activity).onRequestPermissionsResult(requestCode, permissions, grantResults);
    本项目在下面重写了onRequestPermissionsResult()
    */


    /**
     *
     * @param requestCode 当时调用requestPermissions()ALL_PERMISSIONS_REQUEST_COOE的参数
     * @param permissions 当时调用requestPermissions()的permissions
     * @param grantResults 代表String数组permissions的权限的赋予的值的情况
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode ==ALL_PERMISSIONS_REQUEST_COOE){
            boolean  isPermissionsGranted =true;//是否所有权限已获取
            for(int request : grantResults){//检查所有权限是否已获取
                if(request!=PackageManager.PERMISSION_GRANTED){//PackageManager.PERMISSION_GRANTED已经获取的权限的值
                    isPermissionsGranted =false;
                    break;
                }
            }
            if(isPermissionsGranted){//权限已经完全获取
                Timer timer = new Timer();//
                timer.schedule(new TimerTask() {//定时执行run()中的动作
                    @Override
                    public void run() {
                        Intent intent = new Intent(MainActivity.this, DetectActivity.class);
                        startActivity(intent);
                    }
                },3000);
            }else {
                finish();
            }
        }
    }
}
