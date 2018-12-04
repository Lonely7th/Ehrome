package com.daiji.feixiang.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.daiji.feixiang.holder.MyBaseViewHolder;

import java.util.ArrayList;

public abstract class MyBaseAdapter<E> extends BaseAdapter {
    protected ArrayList<E> data;
    protected MyBaseViewHolder viewHolder;

    public MyBaseAdapter(ArrayList<E> data) {
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public E getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            viewHolder = getHolder();
            convertView = viewHolder.getView();
        } else {
            viewHolder = (MyBaseViewHolder) convertView.getTag();
        }
        initView(getItem(position), viewHolder);
        return convertView;
    }

    /**
     * 初始化布局文件
     * @param item 单个item数据bean
     * @param viewHolder
     */
    public abstract void initView(E item, MyBaseViewHolder viewHolder);

    /**
     * 获取viewHolder对象
     * @return
     */
    public abstract MyBaseViewHolder getHolder();
}
