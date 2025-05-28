package com.myapp.mymusic.ui;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.myapp.mymusic.R;
import com.myapp.mymusic.adapter.MusicRAdapter;
import com.myapp.mymusic.bean.MusicBean;
import com.myapp.mymusic.service.MyMusicService;
import com.myapp.mymusic.util.MusicUtil;
import com.myapp.mymusic.util.OnRecyclerItemClickListener;

public class MainACT extends Activity {

    private SharedPreferences sp;
    private RecyclerView rv_list;
    private MusicRAdapter adapter;
    private TextView textView1, textView2;
    private ImageView imageView_play, imageView_next, imageView_front;
    // 当前音乐播放位置,从0开始
    private int cur_pos = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        sp = getSharedPreferences("location", MODE_PRIVATE);

        setContentView(R.layout.act_main);

        // 加载currentposition的初始数据
        cur_pos = sp.getInt("currentposition", 0);

        // 控制操作栏按钮点击事件
        setClick();

        // 给textView1和textView2赋初值
        initText();

        // 列表的绑定,数据加载,以及相关事件的监听
        setListView();

        // 绑定音乐服务
        Intent bindIntent = new Intent(this, MyMusicService.class);
        bindService(bindIntent, myMusicConnection, BIND_AUTO_CREATE);
    }


    @Override
    protected void onDestroy() {
        try {
            if (myMusicBinder != null && myMusicBinder.isPlaying()) {
                myMusicBinder.pause();
            }
            if (myMusicBinder != null) {
                // 记录播放状态
                MusicBean bean = myMusicBinder.getCurrentMusic();
                if (bean != null) {
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("song_name", bean.getSong());
                    editor.putString("song_singer", bean.getSinger());
                    editor.putInt("currentposition", cur_pos);
                    editor.commit();
                }
            }
            // 关闭与服务的连接
            unbindService(myMusicConnection);
            // 关闭服务
            if (MusicUtil.isMusicServiceRunning(this)) {
                this.stopService(new Intent(this, MyMusicService.class));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }


    private void setClick() {
        View layout_play_ctrl = (View) findViewById(R.id.layout_play_ctrl);
        imageView_play = (ImageView) layout_play_ctrl.findViewById(R.id.imageview_play);
        imageView_next = (ImageView) layout_play_ctrl.findViewById(R.id.imageview_next);
        imageView_front = (ImageView) layout_play_ctrl.findViewById(R.id.imageview_front);
        textView1 = (TextView) layout_play_ctrl.findViewById(R.id.name);
        textView2 = (TextView) layout_play_ctrl.findViewById(R.id.singer);

        imageView_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePlayImg(R.mipmap.ic_bt_play, R.mipmap.pause_red);
                if (myMusicBinder.isPlaying()) {
                    myMusicBinder.pause();
                } else {
                    myMusicBinder.play();
                }
            }
        });

        imageView_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextMusic();
            }
        });

        imageView_front.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                frontMusic();
            }
        });
    }

    private void initText() {
        textView1.setText(sp.getString("song_name", "歌曲名").trim());
        textView2.setText(sp.getString("song_singer", "歌手").trim());
    }

    private void setListView() {
        rv_list = (RecyclerView) findViewById(R.id.rv_list);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        rv_list.setLayoutManager(manager);

        adapter = new MusicRAdapter();
        // 列表项点击事件回调
        adapter.setOnItemClickListener(new OnRecyclerItemClickListener() {
            @Override
            public void onRecyclerItemsClick(View view, int position) {
                cur_pos = position;
                playMusic(cur_pos);
                adapter.setFlag(cur_pos);
                adapter.notifyDataSetChanged();
            }
        });
        rv_list.setAdapter(adapter);

        adapter.setDatas(MusicUtil.loadMusicData(this));
        // 标记正在播放的音乐条目
        adapter.setFlag(cur_pos);
        adapter.notifyDataSetChanged();

    }


    // 播放
    private void playMusic(int position) {
        textView1.setText(adapter.getItem(position).getSong());
        textView2.setText(adapter.getItem(position).getSinger());
        imageView_play.setImageResource(R.mipmap.pause_red);

        if (myMusicBinder != null) {
            myMusicBinder.initMediaPlayer(adapter.getItem(position));
            myMusicBinder.play();
        }
    }

    // 下一曲
    private void nextMusic() {
        isFirstOnComplete = true;
        cur_pos++;
        if (cur_pos > adapter.getItemCount() - 1) {
            cur_pos = 0;
        }
        playMusic(cur_pos);
        adapter.setFlag(cur_pos);
        adapter.notifyDataSetChanged();
    }

    // 上一曲
    private void frontMusic() {
        isFirstOnComplete = true;
        cur_pos--;
        if (cur_pos < 0) {
            cur_pos = adapter.getItemCount() - 1;
        }
        playMusic(cur_pos);
        adapter.setFlag(cur_pos);
        adapter.notifyDataSetChanged();
    }

    private void changePlayImg(int resID_play, int resID_pause) {
        if (imageView_play
                .getDrawable()
                .getCurrent()
                .getConstantState()
                .equals(getResources().getDrawable(resID_play).getConstantState())) {
            imageView_play.setImageResource(resID_pause);
        } else {
            imageView_play.setImageResource(resID_play);
        }
    }


    /**
     * 以下为调用音乐服务所需，要使用音乐服务必须先启动和绑定服务。
     */
    public MyMusicService.MyMusicBinder myMusicBinder;
    private MyMusicConnection myMusicConnection = new MyMusicConnection();
    private boolean isFirstOnComplete = false;

    public class MyMusicConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d("服务连接日志", "服务连接，调用者：" + getApplicationContext());
            myMusicBinder = (MyMusicService.MyMusicBinder) service;

            if (myMusicBinder != null) {
                // 监听mediaplayer播放完毕时调用
                myMusicBinder.getMediaPlayer().setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        // 这里会引发初次进入时直接点击播放按钮时，播放的是下一首音乐的问题
                        if (!isFirstOnComplete){
                            isFirstOnComplete = true;
                            playMusic(cur_pos);
                        }else {
                            nextMusic();
                        }
                    }
                });
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d("服务连接日志", "服务断开连接");
        }
    }


}
