package com.myapp.mymusic.util;

import android.view.View;

/**
 * RecyclerView回调接口。当点击其子项时，可交回Activity处理
 */
public interface OnRecyclerItemClickListener {
    /**
     * 该方法可在Activity中重写，即点击子项时的事件处理
     * @param view
     * @param position
     */

    void onRecyclerItemsClick(View view, int position);
}
