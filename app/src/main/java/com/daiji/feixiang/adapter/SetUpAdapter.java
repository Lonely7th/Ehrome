package com.daiji.feixiang.adapter;

import com.daiji.feixiang.bean.SetUpBean;
import com.daiji.feixiang.holder.MyBaseViewHolder;
import com.daiji.feixiang.holder.SetUpViewHolder;

import java.util.ArrayList;

public class SetUpAdapter extends MyBaseAdapter<SetUpBean> {
    public SetUpAdapter(ArrayList data) {
        super(data);
    }

    @Override
    public void initView(SetUpBean bean, MyBaseViewHolder viewHolder) {
        SetUpViewHolder vh = (SetUpViewHolder) viewHolder;
        vh.iv_icon.setImageResource(bean.icon);
        vh.tv_title.setText(bean.title);
    }

    @Override
    public SetUpViewHolder getHolder() {
        return new SetUpViewHolder();
    }
}
