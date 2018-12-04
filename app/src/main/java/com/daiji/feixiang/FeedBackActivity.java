package com.daiji.feixiang;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;

import com.daiji.feixiang.common.Common;

public class FeedBackActivity extends BaseActivity {

    @Override
    public void init(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_feed_back);
        TextView about_version = findViewById(R.id.about_version);
        TextView about_version_name = findViewById(R.id.about_version_name);
        TextView about_qq = findViewById(R.id.about_qq);
        TextView about_source = findViewById(R.id.about_source);

        try {
            about_version.setText("App版本："+Common.getVersion());
            about_version_name.setText("App版本名称："+Common.getVersionName());
            about_qq.setText("QQ交流群：718016227");
            about_source.setText("https://m.gitee.com/daiji111/flying_browser");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }
}
