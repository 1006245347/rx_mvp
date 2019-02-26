package com.rx_mvp.pointdownload;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import com.rx_mvp.R;
import com.rx_mvp.common.RXAPP;

import java.io.File;

import jason.com.rxremvplib.global.GlobalCode;
import jason.com.rxremvplib.http.ApiEngine;
import jason.com.rxremvplib.http.FileDownLoadObserver;
import jason.com.rxremvplib.utils.ACacheUtil;

public class DownloadIntentService extends IntentService {
    private static final String TAG = "TAG";
    private NotificationManager mNotifyManager;
    private String mDownloadFileName;
    private Notification mNotification;

    public DownloadIntentService() {
        super("InitializeService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        try {
            String downloadUrl = intent.getExtras().getString("download_url");
            final int downloadId = intent.getExtras().getInt("download_id");
            mDownloadFileName = intent.getExtras().getString("download_file");

//        Log.d(TAG, "download_url --" + downloadUrl);
//        Log.d(TAG, "download_file --" + mDownloadFileName);

            String path = RXAPP.getAppContext().getImageCacheDir() + "/" + mDownloadFileName;
            final File file = new File(path);
            long range = 0;
            int progress = 0;
            if (file.exists()) {
                String strRande = ACacheUtil.getCache(this, "service");
                range = Long.parseLong(strRande);
                progress = (int) (range * 100 / file.length());
                if (range == file.length()) {
//                installApp(file);
                    GlobalCode.printLog("intalll.");
                    return;
                }
            }

//        Log.d(TAG, "range = " + range);

            final RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.notify_download);
            remoteViews.setProgressBar(R.id.pb_progress, 100, progress, false);
            remoteViews.setTextViewText(R.id.tv_progress, "已下载" + progress + "%");

            final NotificationCompat.Builder builder =
                    new NotificationCompat.Builder(this)
                            .setContent(remoteViews)
                            .setTicker("正在下载")
                            .setSmallIcon(R.mipmap.ic_launcher);

            mNotification = builder.build();

            mNotifyManager = (NotificationManager)
                    getSystemService(Context.NOTIFICATION_SERVICE);
            mNotifyManager.notify(downloadId, mNotification);

            ApiEngine.getInstance().pointDownLoad(downloadUrl, range, RXAPP.getAppContext().getImageCacheDir().getAbsolutePath(), mDownloadFileName, new FileDownLoadObserver<File>() {
                @Override
                public void onDownLoadSuccess(File file) {
                    mNotifyManager.cancel(downloadId);
                    GlobalCode.printLog("do_suc");
                }

                @Override
                public void onDownLoadFail(Throwable throwable) {
                    mNotifyManager.cancel(downloadId);
                    GlobalCode.printLog(throwable);
                }

                @Override
                public void onProgress(int progress, long total) {
                    remoteViews.setProgressBar(R.id.pb_progress, 100, progress, false);
                    remoteViews.setTextViewText(R.id.tv_progress, "已下载" + progress + "%");
                    mNotifyManager.notify(downloadId, mNotification);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void installApp(File file) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        startActivity(intent);
    }
}
