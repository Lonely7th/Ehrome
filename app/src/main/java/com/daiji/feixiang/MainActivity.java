package com.daiji.feixiang;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import com.daiji.feixiang.common.Constant;

public class MainActivity extends AppCompatActivity {

    private MyHandler myHandler = new MyHandler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //全屏显示
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        /**
         * 启动跳转
         */
        myHandler.sendEmptyMessageDelayed(Constant.SUCCESS, 1200);
    }

    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                //跳到主页
                case Constant.SUCCESS:
                    startActivity(new Intent(getApplicationContext(), BrowserActivity.class));
                    finish();
            }
        }
    }
}
