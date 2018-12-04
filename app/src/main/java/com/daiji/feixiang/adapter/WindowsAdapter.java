package com.daiji.feixiang.adapter;

import android.webkit.WebView;

import com.daiji.feixiang.holder.MyBaseViewHolder;
import com.daiji.feixiang.holder.WindowsViewHolder;

import java.util.ArrayList;

public abstract class WindowsAdapter extends MyBaseAdapter<WebView> {
    public WindowsAdapter(ArrayList data) {
        super(data);
    }

    @Override
    public abstract void initView(WebView item, MyBaseViewHolder viewHolder);

    @Override
    public WindowsViewHolder getHolder() {
        return new WindowsViewHolder();
    }
}
