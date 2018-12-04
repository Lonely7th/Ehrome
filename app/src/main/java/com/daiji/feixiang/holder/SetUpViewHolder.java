package com.daiji.feixiang.holder;

import android.widget.ImageView;
import android.widget.TextView;

import com.daiji.feixiang.R;

public class SetUpViewHolder extends MyBaseViewHolder {
    public ImageView iv_icon;
    public TextView tv_title;
    @Override
    public int getResourceId() {
        return R.layout.gridview_item_setup;
    }

    @Override
    public void initHolder() {
        iv_icon = getView().findViewById(R.id.iv_icon);
        tv_title = getView().findViewById(R.id.tv_title);
    }
}
