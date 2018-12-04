package com.daiji.feixiang.dao;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.Environment;
import android.os.Message;
import android.text.TextUtils;

import com.daiji.feixiang.R;
import com.daiji.feixiang.application.MyApplication;
import com.daiji.feixiang.common.Common;
import com.daiji.feixiang.common.Constant;
import com.daiji.feixiang.common.Request;

import org.json.JSONObject;

public class UpdateDao {
    /**
     * 检查更新
     */
    public static void update(final Activity activity) {
        if (!Common.getPreferences(MyApplication.getContext(), Constant.UPDATE, true)) {
            return;
        }
        if (System.currentTimeMillis() < Common.getPreferences(MyApplication.getContext(), Constant.NOTUPDATE, System.currentTimeMillis())) {
            return;
        }
        new Thread() {
            @Override
            public void run() {
                Request req = new Request();
                req.setUrl(MyApplication.getContext().getString(R.string.update_url));
                req.setTimeOut(5000);
                try {
                    String reqString = req.getString();
                    JSONObject object = new JSONObject(reqString);

                    int updateVersion = object.getInt("version");
                    PackageInfo info = MyApplication.getContext().getPackageManager().getPackageInfo(MyApplication.getContext().getPackageName(), 0);
                    int localVersion = info.versionCode;
                    if (updateVersion > localVersion) {
                        //开始更新

                        //检查权限
                        if (!Common.getPermissions(activity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE})) {
                            //MyApplication.getMyHandler().sendEmptyMessage(Constant.UPDATE_NO_PERMISSION);
                            return;
                        }

                        //检查挂载情况
                        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                            //MyApplication.getMyHandler().sendEmptyMessage(Constant.UPDATE_NO_MEDIA_MOUNTED);
                            return;
                        }
                        Message message = Message.obtain();
                        message.obj = object;
                        message.what = Constant.UPDATE_ALERT;
                        MyApplication.getMyHandler().sendMessage(message);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    /**
     * 下载文件
     *
     * @param url 下载链接
     */
    public static void download(String url) {
        String filename = url.substring(url.lastIndexOf("/") + 1);
        if (filename.contains("?")) {
            filename = filename.substring(0, filename.lastIndexOf("?"));
        }
        if (filename.length() > 30) {
            filename = filename.substring(filename.length() - 30);
        }
        if (TextUtils.isEmpty(filename)) {
            MyApplication.getMyHandler().sendEmptyMessage(Constant.EMPTY_FILENAME);
            return;
        }
        Uri uri = Uri.parse(url);
        DownloadManager service = (DownloadManager) MyApplication.getContext().getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filename);
        request.setTitle(filename);
        request.setDescription(filename);
        service.enqueue(request);
        Common.goDownload();
    }
}
