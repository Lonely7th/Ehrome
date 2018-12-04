package com.daiji.feixiang.handler;

import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;

import com.daiji.feixiang.BrowserActivity;
import com.daiji.feixiang.R;
import com.daiji.feixiang.application.MyApplication;
import com.daiji.feixiang.common.Common;
import com.daiji.feixiang.common.Constant;
import com.daiji.feixiang.dao.UpdateDao;

import org.json.JSONObject;

public class MyHandler extends Handler {
    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case Constant.UPDATE_NO_PERMISSION:
                Common.alert("应用没有存储权限");
                break;
            case Constant.UPDATE_NO_MEDIA_MOUNTED:
                Common.alert("您的手机没有挂载sdcard");
                break;
            case Constant.UPDATE_ALERT:
                update(msg.obj);
                break;
            case Constant.EMPTY_FILENAME:
                Common.alert("文件名为空");
                break;
            case Constant.FILE_DOWNLOAD_COMPLETE:
                Common.alert("文件下载完成了");
                break;
            case Constant.APP_ERROR:
                Common.alert("应用程序出错了");
                break;
        }
    }

    private void update(final Object obj) {
        JSONObject jsonObject = (JSONObject) obj;
        String content = "";
        String updateUrl = "";
        try {
            content = jsonObject.getString("content");
            updateUrl = jsonObject.getString("url");
        } catch (Exception e) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(BrowserActivity.getActivity());
        final String finalUpdateUrl = updateUrl;
        builder.setTitle("更新提示")
                .setIcon(R.drawable.app)
                .setMessage(content)
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Common.setPreferences(MyApplication.getContext(), Constant.NOTUPDATE, System.currentTimeMillis() + 3 * 24 * 3600 * 1000);
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        UpdateDao.download(finalUpdateUrl);
                    }
                })
                .create()
                .show();
    }
}
