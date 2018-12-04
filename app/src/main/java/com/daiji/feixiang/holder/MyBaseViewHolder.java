package com.daiji.feixiang.holder;

import android.view.View;

import com.daiji.feixiang.application.MyApplication;

public abstract class MyBaseViewHolder {
    private View view;

    public MyBaseViewHolder() {
        initView();
        initHolder();
    }

    private void initView() {
        view = View.inflate(MyApplication.getContext(), getResourceId(), null);
        view.setTag(this);
    }

    public Object getTag() {
        return view.getTag();
    }

    /**
     * 返回布局文件id
     * @return
     */
    public abstract int getResourceId();

    /**
     * viewHolder对象初始化
     */
    public abstract void initHolder();

    public View getView() {
        return view;
    }
}
