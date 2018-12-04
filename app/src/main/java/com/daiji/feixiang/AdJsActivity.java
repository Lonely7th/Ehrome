package com.daiji.feixiang;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.daiji.feixiang.bean.AdBean;
import com.daiji.feixiang.dao.AdDao;

import java.util.ArrayList;
import java.util.List;

public class AdJsActivity extends BaseActivity {

    private List<AdBean> lists;
    private MyAdapter adapter;
    private AdDao adDao;

    @Override
    public void init(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_ad_url);
        ListView lv_url = findViewById(R.id.lv_url);

        lists = new ArrayList<>();
        adapter = new MyAdapter();
        lv_url.setAdapter(adapter);

        initData();

        lv_url.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                final AdBean adBean = lists.get(position);
                AlertDialog.Builder builder = new AlertDialog.Builder(AdJsActivity.this);
                builder.setIcon(R.drawable.app)
                        .setTitle("删除提示")
                        .setMessage("确定删除" + adBean.url)
                        .setNegativeButton("取消", null)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                adDao.delete(adBean._id);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        lists.remove(position);
                                        adapter.notifyDataSetChanged();
                                    }
                                });
                                dialog.dismiss();
                            }
                        })
                        .create()
                        .show();
            }
        });
    }

    private void initData() {
        adDao = new AdDao();
        new Thread() {
            @Override
            public void run() {
                List<AdBean> all = adDao.getAll(2);
                lists.addAll(all);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        }.start();
    }

    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return lists.size();
        }

        @Override
        public Object getItem(int i) {
            return lists.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            MyViewHolder myViewHolder;
            if (view == null) {
                view = getLayoutInflater().inflate(R.layout.listview_item_ad, null);
                myViewHolder = new MyViewHolder();
                myViewHolder.tv_url = view.findViewById(R.id.tv_url);
                view.setTag(myViewHolder);
            } else {
                myViewHolder = (MyViewHolder) view.getTag();
            }

            AdBean item = (AdBean) getItem(i);
            myViewHolder.tv_url.setText(item.url);
            return view;
        }

    }


    private static class MyViewHolder {
        public TextView tv_url;
    }
}
