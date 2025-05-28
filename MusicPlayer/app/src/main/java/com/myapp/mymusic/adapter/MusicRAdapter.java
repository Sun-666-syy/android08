package com.myapp.mymusic.adapter;

import android.annotation.SuppressLint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.myapp.mymusic.R;
import com.myapp.mymusic.bean.MusicBean;
import com.myapp.mymusic.util.OnRecyclerItemClickListener;

import java.util.List;

public class MusicRAdapter extends RecyclerView.Adapter<MusicRAdapter.ViewHolder> {

    private List<MusicBean> datas;

    private int position_flag = 0;


    public void setFlag(int flag) {
        this.position_flag = flag;
    }

    public void setDatas(List<MusicBean> datas) {
        this.datas = datas;
    }

    public List<MusicBean> getDatas() {
        return datas;
    }

    public MusicBean getItem(int i) {
        return datas.get(i);
    }

    // 用于回调
    private OnRecyclerItemClickListener mOnRecyclerItemClickListener;

    static class ViewHolder extends RecyclerView.ViewHolder {
        View musicBeanView;
        TextView song;// 歌曲名
        TextView singer;// 歌手
        TextView time;// 时长
        TextView position;// 序号

        public ViewHolder(View view) {
            super(view);
            musicBeanView = view;
            song = (TextView) view.findViewById(R.id.item_mymusic_song);
            singer = (TextView) view.findViewById(R.id.item_mymusic_singer);
            time = (TextView) view.findViewById(R.id.item_mymusic_time);
            position = (TextView) view.findViewById(R.id.item_mymusic_postion);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // 载入子项布局
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_song_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        MusicBean data = datas.get(position);

        // 给控件赋值
        holder.song.setText(data.getSong());
        if (position == position_flag) {
            holder.position.setBackgroundResource(R.mipmap.ic_play);
            holder.position.setText("");
        } else {
            holder.position.setText(position + 1 + "");
            holder.position.setBackground(null);

        }
        holder.singer.setText(data.getSinger() + " | " + data.getAlbum());

        holder.time.setText(data.getTime());

        holder.musicBeanView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 使用回调给Activity处理
                if (mOnRecyclerItemClickListener != null) {
                    // 将点击后的子项MusicBean回调
                    mOnRecyclerItemClickListener.onRecyclerItemsClick(v, position);
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return datas == null ? 0 : datas.size();
    }

    /**
     * 用于回调。给RecyclerView设置Adapter时调用它，参数里new一个OnRecyclerItemsClickListener类
     *
     * @param onRecyclerItemClickListener 要实现的接口
     */
    public void setOnItemClickListener(OnRecyclerItemClickListener onRecyclerItemClickListener) {
        mOnRecyclerItemClickListener = onRecyclerItemClickListener;
    }
}