package com.daiji.feixiang;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.daiji.feixiang.bean.BrowserHistoryBean;
import com.daiji.feixiang.common.Common;
import com.daiji.feixiang.common.Constant;
import com.daiji.feixiang.dao.BrowserBookMarkDao;
import com.daiji.feixiang.dao.BrowserHistoryDao;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BrowserHistoryActivity extends BaseActivity {
    private BrowserHistoryDao browserHistoryDao;
    private ListView lv_history;
    private List<BrowserHistoryBean> lists = new ArrayList<>();
    private int page = 1;
    private MyAdapter myAdapter;
    private MyHandler myHandler = new MyHandler();
    private List<BrowserHistoryBean> data = new ArrayList<>();
    private BrowserBookMarkDao browserBookMarkDao;

    @Override
    public void init(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_browser_history);
        browserHistoryDao = new BrowserHistoryDao();
        initUi();
        initData();
    }

    private void initData() {
        new Thread() {
            @Override
            public void run() {
                data = getPage();
                if (data != null) {
                    myHandler.sendEmptyMessage(1);
                }
            }
        }.start();
    }

    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    if (data != null && data.size() > 0) {
                        lists.addAll(data);
                    }
                    myAdapter.notifyDataSetChanged();
                    break;
            }
        }
    }

    private List<BrowserHistoryBean> getPage() {
        List<BrowserHistoryBean> data = null;
        try {
            data = browserHistoryDao.page(page);
            if (data.size() > 0) {
                page++;
                return data;
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return null;
    }

    private void initUi() {
        lv_history = findViewById(R.id.lv_history);
        myAdapter = new MyAdapter();
        lv_history.setAdapter(myAdapter);

        lv_history.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem != 0) {
                    if ((firstVisibleItem + visibleItemCount) > totalItemCount - 1) {
                        new Thread() {
                            @Override
                            public void run() {
                                data = getPage();
                                if (data != null) {
                                    myHandler.sendEmptyMessage(1);
                                }
                            }
                        }.start();
                    }
                }
            }
        });

        lv_history.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BrowserHistoryBean browserHistoryBean = lists.get(position);
                String url = browserHistoryBean.url;
                Intent intent = new Intent();
                intent.putExtra("url", url);
                setResult(1, intent);
                finish();
            }
        });

        lv_history.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setItems(R.array.history_more, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        BrowserHistoryBean browserHistoryBean = lists.get(position);
                        String url = browserHistoryBean.url;
                        String title = browserHistoryBean.title;
                        int _id = browserHistoryBean._id;
                        switch (which){
                            case 0:
                                Common.setPreferences(mContext, Constant.HOME_URL, url);
                                Toast.makeText(getApplicationContext(), "设置成功", Toast.LENGTH_SHORT).show();
                                break;
                            case 1:
                                browserBookMarkDao = new BrowserBookMarkDao();
                                browserBookMarkDao.add(url, title);
                                Toast.makeText(getApplicationContext(), "添加成功", Toast.LENGTH_SHORT).show();
                                break;
                            case 2:
                                browserHistoryDao.delete(_id);
                                lists.remove(position);
                                myAdapter.notifyDataSetChanged();
                                Toast.makeText(getApplicationContext(), "删除成功", Toast.LENGTH_SHORT).show();
                                break;
                        }
                        dialog.dismiss();
                    }
                }).setIcon(R.drawable.app).setTitle("请选择").create().show();

                return true;
            }
        });
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
                view = getLayoutInflater().inflate(R.layout.listview_item_history, null);
                myViewHolder = new MyViewHolder();
                myViewHolder.tv_title = view.findViewById(R.id.tv_title);
                myViewHolder.tv_time = view.findViewById(R.id.tv_time);
                myViewHolder.tv_url = view.findViewById(R.id.tv_url);
                view.setTag(myViewHolder);
            } else {
                myViewHolder = (MyViewHolder) view.getTag();
            }

            BrowserHistoryBean item = (BrowserHistoryBean) getItem(i);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");

            Date date = new Date(Long.parseLong(item.time));
            String format = simpleDateFormat.format(date);
            myViewHolder.tv_time.setText(format);

            myViewHolder.tv_title.setText(item.title);
            myViewHolder.tv_url.setText(item.url);
            return view;
        }

    }


    private static class MyViewHolder {
        public TextView tv_title;
        public TextView tv_time;
        public TextView tv_url;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_history, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.clear:
                //清除记录
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("清除数据")
                        .setMessage("清除之后，无法恢复")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                browserHistoryDao.removeAll();
                                lists.clear();
                                Toast.makeText(mContext, "清除成功", Toast.LENGTH_LONG).show();
                                myAdapter.notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
                break;

        }
        return super.onOptionsItemSelected(item);
    }
}
