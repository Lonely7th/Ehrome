package com.daiji.feixiang.application;

import android.app.Application;
import android.content.Context;
import android.os.Handler;

import com.daiji.feixiang.common.Common;
import com.daiji.feixiang.common.Constant;
import com.daiji.feixiang.handler.MyHandler;

import java.io.IOException;


public class MyApplication extends Application {
    private static Context mContext;
    private static Handler myHandler;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        myHandler = new MyHandler();
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                myHandler.sendEmptyMessage(Constant.APP_ERROR);

                int len = e.getStackTrace().length;
                StringBuilder stringBuilder = new StringBuilder();
                for (int i = 0; i < len; i++) {
                    stringBuilder.append(e.getStackTrace()[i] + "\n");
                }

                //打印错误信息
                System.out.println(t.getName());
                System.out.println(e.toString());
                System.out.println(stringBuilder.toString());

                //记录错误日志记录
                if (Common.getPreferences(MyApplication.getContext(), Constant.ERROR_LOG, false)) {
                    try {
                        Common.writeLog(e.toString() + "\n" + stringBuilder.toString());
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });
    }

    public static Context getContext() {
        return mContext;
    }

    public static Handler getMyHandler() {
        return myHandler;
    }
}
