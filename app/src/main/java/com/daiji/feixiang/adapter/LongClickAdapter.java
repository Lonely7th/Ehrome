package com.daiji.feixiang.adapter;

import com.daiji.feixiang.bean.LongClickBean;
import com.daiji.feixiang.holder.LongClickViewHolder;
import com.daiji.feixiang.holder.MyBaseViewHolder;

import java.util.ArrayList;

public class LongClickAdapter extends MyBaseAdapter<LongClickBean> {
    public LongClickAdapter(ArrayList data) {
        super(data);
    }

    @Override
    public void initView(LongClickBean bean, MyBaseViewHolder viewHolder) {
        LongClickViewHolder vh = (LongClickViewHolder) viewHolder;
        vh.tv_name.setText(bean.name);
    }

    @Override
    public LongClickViewHolder getHolder() {
        return new LongClickViewHolder();
    }
}
