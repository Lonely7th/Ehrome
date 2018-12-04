package com.daiji.feixiang.receiver;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.daiji.feixiang.application.MyApplication;
import com.daiji.feixiang.common.Constant;

public class DownloadCompleteReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            long completeDownloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            if (completeDownloadId > 0) {
                MyApplication.getMyHandler().sendEmptyMessage(Constant.FILE_DOWNLOAD_COMPLETE);
            }
        }
    }
}
