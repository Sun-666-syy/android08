package com.myapp.mymusic.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.myapp.mymusic.R;

import kr.co.namee.permissiongen.PermissionFail;
import kr.co.namee.permissiongen.PermissionGen;
import kr.co.namee.permissiongen.PermissionSuccess;

public class WelcomeACT extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_welcome);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //获取权限
                requestPermissions();
            }
        }, 2000);
    }


    private void requestPermissions() {
        //适配6.0以上机型请求权限
        PermissionGen.with(this)
                .addRequestCode(100)
                .permissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                .request();
    }

    //以下三个方法用于6.0以上权限申请适配
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        PermissionGen.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    @PermissionSuccess(requestCode = 100)
    public void doSomething() {
        //Toast.makeText(this, "相关权限已允许", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, MainACT.class));
        finish();
    }

    @PermissionFail(requestCode = 100)
    public void doFailSomething() {
        Toast.makeText(this, "程序已退出，若要使用程序请允许全部相关权限", Toast.LENGTH_SHORT).show();
        finish();
    }
}
