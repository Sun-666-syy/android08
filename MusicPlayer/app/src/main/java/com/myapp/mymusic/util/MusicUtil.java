package com.myapp.mymusic.util;

import android.app.ActivityManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.myapp.mymusic.bean.MusicBean;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MusicUtil {
    /**
     * 加载本地存储中的音乐
     */
    public static List<MusicBean> loadMusicData(Context context) {

        List<MusicBean> mDatas = new ArrayList<>();

        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        context.sendBroadcast(intent);

        //1.获取ContentResolver对象
        ContentResolver resolver = context.getContentResolver();
        //2.获取本地音乐存储的uri地址
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        //3.开始查询地址
        Cursor cursor = resolver.query(uri, null, null, null, null);
        //4.遍历cursor
        if (cursor != null) {
            while (cursor.moveToNext()) {
                MusicBean bean = new MusicBean();
                bean.setSong(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)));
                bean.setSinger(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)));
                bean.setAlbum(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)));
                bean.setPath(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)));
                bean.setTime(formatTime(cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION))));
                long size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE));
                if (size > 1000 * 800) {//过滤掉短音频
                    mDatas.add(bean);
                }
            }
            cursor.close();
        }
        return mDatas;
    }

    //格式化时间
    public static String formatTime(long duration) {
        SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");
        return sdf.format(new Date(duration));
    }

    /**
     * 判断音乐服务是否正在运行
     *
     * @param mContext 上下文对象
     * @return
     */
    public static boolean isMusicServiceRunning(Context mContext) {
        // ActivityManager用于管理Activity
        ActivityManager mActivityManager = (ActivityManager) mContext.getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> runSerInfoList;
        // getRunningServices()得到正在运行的服务
        runSerInfoList = mActivityManager.getRunningServices(50);
        // 遍历数组判断是否存在服务
        for (int i = 0; i < runSerInfoList.size(); i++) {
            String serName = runSerInfoList.get(i).service.getClassName().toString();
            Log.d("服务工具类日志", "找到服务：" + serName);
            if (serName.equals("com.xiaoyao.mymusicapp.service.MyMusicService")) {
                return true;
            }
        }

        return false;
    }
}