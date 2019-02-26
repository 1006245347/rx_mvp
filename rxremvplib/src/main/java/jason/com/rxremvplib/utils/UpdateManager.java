package jason.com.rxremvplib.utils;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import jason.com.rxremvplib.R;


/**
 * 版本更新 apk下载
 *
 * @Title: UpdateManager.java
 */
public class UpdateManager extends Service {


    private static Activity mActivity;
    // app信息
    private static String mAppName;
    //下载地址
    private static String downPath;
    // 通知栏
    private NotificationManager mNotificationManager = null;

    // 跳转Intent
    private PendingIntent mPendingIntent = null;

    private static boolean isShowNotify;
    private NotificationCompat.Builder mNotificationBuilder;
    private static final int ID_NOTIFICATION = 919;

    public UpdateManager() {
    }

    public static void builder(Context context, String tmpAppName, String tmpdownPath, boolean tmpShowNotify) {
        mActivity = (Activity) context;
        mAppName = tmpAppName;
        downPath = tmpdownPath;
        Log.v("TAG", "downPath=" + downPath);
        isShowNotify = tmpShowNotify;
    }

    public static void builder(Context context, String tmpAppName, String tmpdownPath) {
        builder(context, tmpAppName, tmpdownPath, true);
    }

    // 更新下载进度
    private void updateDownloadSchedule(int percen) {

        if (!isShowNotify) {//通知栏不通知
            return;
        }

        String content = "当前下载进度：" + percen + "%";
        try {

            mNotificationManager.notify(ID_NOTIFICATION,
                    mNotificationBuilder
                            .setContentText(content)
                            .setProgress(100, (int) ((float) percen), false)
                            .build());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("updateApp", "service start");

        // 初始化通知栏
        mNotificationManager = (NotificationManager) this.getSystemService(NOTIFICATION_SERVICE);

        Intent notificationIntent = mActivity.getIntent();
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        // 设置下载过程中，点击通知栏，回到主界面
        mPendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        //用v4包中Compat类兼容低版本
        mNotificationBuilder = new NotificationCompat.Builder(mActivity)
                .setSmallIcon(R.drawable.head_back)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.head_back))
                .setContentTitle("开始下载")
                .setContentIntent(mPendingIntent)
                .setAutoCancel(true)
                .setProgress(100, 0, false);

        mNotificationManager.notify(ID_NOTIFICATION, mNotificationBuilder.build());


        // 下载线程
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    downloadLatestApp(downPath);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        return super.onStartCommand(intent, flags, startId);
    }

    // 下载最新版本的APP
    private void downloadLatestApp(String downloadAppPath) throws IOException {
        // 本地存储路径
        String filePath = Util.getSavePath(mActivity) + "/" + mAppName + ".apk";
        Log.e("updateApp", "download to : " + filePath);
        Log.e("updateApp", "app path is : " + downloadAppPath);
        //内存卡中有新版本，直接更新
        /*if(Util.isApkExit(filePath)){
            prepareToInstallAPP(filePath);
			return;
		}*/

        long downloadSize = 0;
        HttpURLConnection httpUrlConnection = null;
        InputStream in = null;
        FileOutputStream out = null;
        try {
            URL url = new URL(downloadAppPath);

            httpUrlConnection = (HttpURLConnection) url.openConnection();

            final long fileSize = httpUrlConnection.getContentLength();

            Log.e("updateApp", "fileSize is : " + fileSize);

            in = httpUrlConnection.getInputStream();

            File fileOut = new File(filePath);
            out = new FileOutputStream(fileOut);

            byte[] bytes = new byte[1024];
            int readSize;
            int tmpSize = 0;
            while ((readSize = in.read(bytes)) != -1) {
                out.write(bytes, 0, readSize);
                downloadSize += readSize;
                tmpSize += readSize;
                if (tmpSize >= fileSize / 100 || downloadSize == fileSize) {    //每次增加1%
                    tmpSize = 0;
                    updateDownloadSchedule((int) (downloadSize * 100 / fileSize));
                }

            }

            // 使文件有读写权限
            String cmd = "chmod 777 " + filePath;
            try {
                Runtime.getRuntime().exec(cmd);
            } catch (Exception e) {
                e.printStackTrace();
            }

            prepareToInstallAPP(filePath);

        } catch (Exception e) {
            downloadFail();

            e.printStackTrace();
        } finally {
            if (httpUrlConnection != null) {
                httpUrlConnection.disconnect();
            }

            if (in != null) {
                in.close();
            }

            if (out != null) {
                out.close();
            }
        }
    }

    // 下载失败
    private void downloadFail() {
        Intent intent = new Intent(mActivity, UpdateManager.class);
        mPendingIntent = PendingIntent.getService(this, 0, intent, 0);
        //用v4包中Compat类兼容低版本
        mNotificationManager.notify(ID_NOTIFICATION,
                mNotificationBuilder
                        .setContentTitle("下载失败，点击重新下载")
                        .setContentIntent(mPendingIntent)
                        .setAutoCancel(true)
                        .build());

        // 停止服务
        stopSelf();
    }

    // 安装APP
    private void prepareToInstallAPP(String filePath) {
        Log.e("tag", "install file path : " + filePath);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        //兼容7.0文件
        Uri data;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            data = FileProvider.getUriForFile(mActivity, mActivity.getApplication().getPackageName() + ".FileProvider", new File(filePath));
            // 给目标应用一个临时授权
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            data = Uri.fromFile(new File(filePath));
        }
        intent.setDataAndType(data, "application/vnd.android.package-archive");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        mNotificationManager.cancel(0);
        // 停止服务
        stopSelf();
    }

    /**
     * 工具类
     */
    public static class Util {

        /**
         * 获取存储路径
         */
        public static String getSavePath(Context context) {
            File sdDir = null;

            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                sdDir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);

                if (sdDir == null) {
                    sdDir = context.getCacheDir();
                }
            } else {
                sdDir = context.getCacheDir();
            }

            return sdDir.toString();
        }

        /**
         * 判断apk文件是否存在
         *
         * @param name 文件名
         * @return
         */
        public static boolean isApkExit(String name) {
            if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                File file = new File(name);
                if (file.exists()) {
                    return true;
                }
            }
            return false;
        }


        /**
         * 得到当前版本信息
         *
         * @return 当前版名字
         */
        public static String getlocalVersionName(Context mContext) {
            PackageInfo packageInfo = null;
            String localVersion = null;
            try {
                packageInfo = mContext.getApplicationContext().getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
                localVersion = packageInfo.versionName;
            } catch (NameNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return localVersion;
        }

        /**
         * 得到当前版本信息
         *
         * @return 当前版本号
         */
        public static int getlocalVersionCode(Context mContext) {
            PackageInfo packageInfo = null;
            int localVersion = 0;
            try {
                packageInfo = mContext.getApplicationContext().getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
                localVersion = packageInfo.versionCode;
            } catch (NameNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return localVersion;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
