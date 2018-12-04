package com.daiji.feixiang.holder;

import android.widget.TextView;

import com.daiji.feixiang.R;

public class ViewUrlViewHolder extends MyBaseViewHolder {

    public TextView tv_item;

    @Override
    public int getResourceId() {
        return R.layout.listview_item_viewurl;
    }

    @Override
    public void initHolder() {
        tv_item = getView().findViewById(R.id.tv_item);
    }
}
