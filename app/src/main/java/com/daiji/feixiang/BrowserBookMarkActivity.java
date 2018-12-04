package com.daiji.feixiang;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.daiji.feixiang.bean.BrowserBookMarkBean;
import com.daiji.feixiang.common.Common;
import com.daiji.feixiang.common.Constant;
import com.daiji.feixiang.dao.BrowserBookMarkDao;

import java.util.ArrayList;
import java.util.List;

public class BrowserBookMarkActivity extends BaseActivity {
    private BrowserBookMarkDao browserBookMarkDao;
    private ListView lv_markbook;
    private List<BrowserBookMarkBean> lists = new ArrayList<>();
    private MyAdapter myAdapter;
    private MyHandler myHandler = new MyHandler();

    @Override
    public void init(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_browser_bookmark);
        browserBookMarkDao = new BrowserBookMarkDao();
        initUi();
        initData();
    }

    private void initData() {
        new Thread() {
            @Override
            public void run() {
                try {
                    lists = browserBookMarkDao.getAll();
                    if (lists != null) {
                        myHandler.sendEmptyMessage(1);
                    }
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }

            }
        }.start();
    }

    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    myAdapter.notifyDataSetChanged();
                    break;
            }
        }
    }

    private void initUi() {
        lv_markbook = findViewById(R.id.lv_markbook);
        myAdapter = new MyAdapter();
        lv_markbook.setAdapter(myAdapter);
        lv_markbook.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BrowserBookMarkBean browserBookMarkBean = lists.get(position);
                String url = browserBookMarkBean.url;
                Intent intent = new Intent();
                intent.putExtra("url", url);
                setResult(1, intent);
                finish();
            }
        });

        lv_markbook.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setIcon(R.drawable.app)
                        .setTitle("请选择")
                        .setItems(R.array.more_nookmark, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                BrowserBookMarkBean browserBookMarkBean = lists.get(position);
                                String url = browserBookMarkBean.url;
                                int _id = browserBookMarkBean._id;
                                switch (which) {
                                    case 0:
                                        Common.setPreferences(mContext, Constant.HOME_URL, url);
                                        Toast.makeText(getApplicationContext(), "设置成功", Toast.LENGTH_SHORT).show();
                                        break;
                                    case 1:
                                        //编辑书签
                                        editBookMark(browserBookMarkBean);
                                        break;
                                    case 2:
                                        browserBookMarkDao.delete(_id);
                                        lists.remove(position);
                                        myAdapter.notifyDataSetChanged();
                                        Toast.makeText(getApplicationContext(), "删除成功", Toast.LENGTH_SHORT).show();
                                        break;
                                }
                                dialog.dismiss();
                            }
                        }).create().show();

                return true;
            }
        });
    }

    /**
     * 编辑书签
     *
     * @param browserBookMarkBean
     */
    private void editBookMark(final BrowserBookMarkBean browserBookMarkBean) {
        View view = View.inflate(mContext, R.layout.alertdialog_editbookmark, null);
        final EditText et_title = view.findViewById(R.id.et_title);
        final EditText et_url = view.findViewById(R.id.et_url);
        et_title.setText(browserBookMarkBean.title);
        et_url.setText(browserBookMarkBean.url);

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setIcon(R.drawable.app)
                .setView(view)
                .setTitle("编辑书签")
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int id = browserBookMarkBean._id;
                        String title = et_title.getText().toString().trim();
                        String url = et_url.getText().toString().trim();
                        if (TextUtils.isEmpty(title)) {
                            Toast.makeText(mContext, "名称不能为空", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (TextUtils.isEmpty(url)) {
                            Toast.makeText(mContext, "链接不能为空", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (!url.startsWith("http")) {
                            Toast.makeText(mContext, "链接输入有误", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        browserBookMarkDao.edit(id, url, title);
                        Toast.makeText(mContext, "编辑成功", Toast.LENGTH_SHORT).show();
                        browserBookMarkBean.url = url;
                        browserBookMarkBean.title = title;
                        myAdapter.notifyDataSetChanged();
                    }
                })
                .create()
                .show();
    }

    /**
     * 添加书签
     */
    private void addBookMark() {
        View view = View.inflate(mContext, R.layout.alertdialog_editbookmark, null);
        final EditText et_title = view.findViewById(R.id.et_title);
        final EditText et_url = view.findViewById(R.id.et_url);

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setIcon(R.drawable.app)
                .setView(view)
                .setTitle("添加书签")
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String title = et_title.getText().toString().trim();
                        String url = et_url.getText().toString().trim();
                        if (TextUtils.isEmpty(title)) {
                            Toast.makeText(mContext, "名称不能为空", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (TextUtils.isEmpty(url)) {
                            Toast.makeText(mContext, "链接不能为空", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (!url.startsWith("http")) {
                            Toast.makeText(mContext, "链接输入有误", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        int _id = (int) browserBookMarkDao.add(url, title);
                        Toast.makeText(mContext, "添加成功", Toast.LENGTH_SHORT).show();
                        BrowserBookMarkBean browserBookMarkBean = new BrowserBookMarkBean();
                        browserBookMarkBean.url = url;
                        browserBookMarkBean.title = title;
                        browserBookMarkBean._id = _id;
                        lists.add(browserBookMarkBean);
                        myAdapter.notifyDataSetChanged();
                    }
                })
                .create()
                .show();
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
                view = getLayoutInflater().inflate(R.layout.listview_item_bookmark, null);
                myViewHolder = new MyViewHolder();
                myViewHolder.tv_title = view.findViewById(R.id.tv_title);
                myViewHolder.tv_url = view.findViewById(R.id.tv_url);
                view.setTag(myViewHolder);
            } else {
                myViewHolder = (MyViewHolder) view.getTag();
            }

            BrowserBookMarkBean item = (BrowserBookMarkBean) getItem(i);
            myViewHolder.tv_title.setText(item.title);
            myViewHolder.tv_url.setText(item.url);
            return view;
        }

    }


    private static class MyViewHolder {
        public TextView tv_title;
        public TextView tv_url;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_bookmark, menu);
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
                                browserBookMarkDao.removeAll();
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

            case R.id.add:
                addBookMark();
                break;

        }
        return super.onOptionsItemSelected(item);
    }
}
