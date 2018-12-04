package com.daiji.feixiang.holder;

import android.widget.TextView;

import com.daiji.feixiang.R;

public class LongClickViewHolder extends MyBaseViewHolder {
    public TextView tv_name;

    @Override
    public int getResourceId() {
        return R.layout.listview_item_clicklong;
    }

    @Override
    public void initHolder() {
        tv_name = getView().findViewById(R.id.tv_name);
    }
}
