package com.daiji.feixiang.holder;

import android.widget.ImageView;
import android.widget.TextView;

import com.daiji.feixiang.R;

public class WindowsViewHolder extends MyBaseViewHolder {
    public TextView tv_title;
    public ImageView iv_delete;
    @Override
    public int getResourceId() {
        return R.layout.listview_item_windows;
    }

    @Override
    public void initHolder() {
        tv_title = getView().findViewById(R.id.tv_title);
        iv_delete = getView().findViewById(R.id.iv_delete);
    }
}
