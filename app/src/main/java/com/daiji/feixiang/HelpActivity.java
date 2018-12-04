package com.daiji.feixiang;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.daiji.feixiang.bean.HelpBean;

import java.util.ArrayList;
import java.util.List;

public class HelpActivity extends BaseActivity {

    private ListView lv_help;
    private List<HelpBean> lists = new ArrayList<>();
    private MyAdapter myAdapter = new MyAdapter();

    @Override
    public void init(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_help);
        initUi();
    }

    private void initUi() {
        lv_help = findViewById(R.id.lv_help);
        lv_help.setAdapter(myAdapter);
        initData();
    }

    private void initData() {
        lists.add(new HelpBean("什么是安全证书","当您访问使用 HTTPS（安全连接）的网站时，该网站的服务器会使用证书向浏览器（如 Chrome）证明该网站的身份。任何人都可以创建证书，随意声称对应的网站是任意网站。\n" +
                "\n" +
                "为了确保您安全上网，会要求网站使用来自受信任组织发放的证书。"));
        lists.add(new HelpBean("连接是安全的","即使您看到这个图标，在分享隐私信息时也一定要小心。请查看地址栏，以确保您打开的网站就是您要访问的网站。"));
        lists.add(new HelpBean("您与此网站之间建立的连接不安全","建议您不要在此网页上输入任何私密信息或个人信息。请尽量不要使用该网站。\n" +
                "\n" +
                "不安全：请谨慎操作。此网站的连接私密性存在严重问题。别人也许能看到您发送的信息或通过此网站获得的信息。\n" +
                "\n" +
                "您可能会看到“登录方式不安全”或“付款方式不安全”这条消息。\n" +
                "\n" +
                "危险：请不要访问此网站。如果您看到整页的红色警告屏幕，则表明该网站已被安全浏览标记为不安全的网站。访问该网站可能会使您的隐私信息面临被泄露的风险。"));
        lists.add(new HelpBean("您与此网站之间建立的连接并非完全安全","该网站未使用私密连接。别人也许能看到或更改您通过此网站发送或获得的信息。\n" +
                "\n" +
                "在某些网站上，您可以访问相应网页的更安全版本：\n" +
                "\n" +
                "选择地址栏。\n" +
                "删除 http://，然后输入 https://。\n" +
                "如果这不起作用，请联系网站所有者，让他们使用 HTTPS 保护网站和您数据的安全。"));

        lists.add(new HelpBean("权限说明","位置权限\n某些网页需要用到位置信息，如百度地图网页版。\n存储权限\n网页下载文件需要用到存储权限。\n电话权限（百度移动统计第三方库需要）\n此权限仅用来获取手机唯一标识码，不会获取手机通讯录、短信等隐私权限。"));
        lists.add(new HelpBean("网页拦截说明","可以拦截URL与网站js脚本2种方式，需要在设置中手动开启。"));
        myAdapter.notifyDataSetChanged();
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
                view = getLayoutInflater().inflate(R.layout.listview_item_help, null);
                myViewHolder = new MyViewHolder();
                myViewHolder.tv_title = view.findViewById(R.id.tv_title);
                myViewHolder.tv_desc = view.findViewById(R.id.tv_desc);
                view.setTag(myViewHolder);
            } else {
                myViewHolder = (MyViewHolder) view.getTag();
            }

            HelpBean item = (HelpBean) getItem(i);
            myViewHolder.tv_title.setText(item.title);
            myViewHolder.tv_desc.setText(item.desc);
            return view;
        }

    }


    private static class MyViewHolder {
        public TextView tv_title;
        public TextView tv_desc;
    }
}
