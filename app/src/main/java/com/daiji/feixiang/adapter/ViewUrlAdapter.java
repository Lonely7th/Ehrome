package com.daiji.feixiang.adapter;

import com.daiji.feixiang.bean.ViewUrlBean;
import com.daiji.feixiang.holder.MyBaseViewHolder;
import com.daiji.feixiang.holder.ViewUrlViewHolder;

import java.util.ArrayList;

public class ViewUrlAdapter extends MyBaseAdapter<ViewUrlBean> {
    public ViewUrlAdapter(ArrayList data) {
        super(data);
    }

    @Override
    public void initView(ViewUrlBean item, MyBaseViewHolder viewHolder) {
        ViewUrlViewHolder vh = (ViewUrlViewHolder) viewHolder;
        vh.tv_item.setText(item.item);
    }

    @Override
    public MyBaseViewHolder getHolder() {
        return new ViewUrlViewHolder();
    }
}
