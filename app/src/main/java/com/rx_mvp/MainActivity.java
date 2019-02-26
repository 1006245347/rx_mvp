package com.rx_mvp;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.rx_mvp.common.RXAPP;
import com.rx_mvp.pointdownload.DownloadIntentService;
import com.rx_mvp.topsnackbar.TopSnackbarUtils;
import com.tencent.bugly.crashreport.BuglyLog;

import jason.com.rxremvplib.base.BaseActivity2;
import jason.com.rxremvplib.global.FunCallback;
import jason.com.rxremvplib.global.GlobalCode;

public class MainActivity extends BaseActivity2 {

    private Button btn_set;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn_set = findViewById(R.id.btn_reset);
        GlobalCode.clickFun(this, R.id.btn_reset, new FunCallback() {
            @Override
            public void onFunCallback() {
                if (mToolbar.getVisibility() == View.VISIBLE) {
                    showToolbar(false);
                } else {
                    showToolbar(true);
                }
                TopSnackbarUtils.showToast(MainActivity.this, "kkkk");

            }
        });
//        floatStatusBar();

        BuglyLog.v("TAG", "MAIN_onCreate>>>");
    }

    /*@Override //这种事爱奇艺视频播放时横屏沉浸式效果
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus && Build.VERSION.SDK_INT >= 19) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                            View.SYSTEM_UI_FLAG_FULLSCREEN |
                            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            );
        }
    }*/

    @Override
    protected void initAty() {
        RXAPP.doSetIP("dldir1.qq.com");
        GlobalCode.clickFun(this, R.id.btn_download, new FunCallback() {
            @Override
            public void onFunCallback() {
                if (GlobalCode.isServiceRunning(mActivity, DownloadIntentService.class.getName())) {
                    TopSnackbarUtils.showToast(mActivity,"正在下载");
                    return;
                }
//                String downloadUrl = "http://sqdd.myapp.com/myapp/qqteam/tim/down/tim.apk;";
                String downloadUrl="http://dldir1.qq.com/qqmi/aphone_p2p/TencentVideo_V6.0.0.14297_848.apk";
                Intent intent = new Intent(mActivity, DownloadIntentService.class);
                Bundle bundle = new Bundle();
                bundle.putString("download_url", downloadUrl);
                bundle.putInt("download_id", 10);
                bundle.putString("download_file", downloadUrl.substring(downloadUrl.lastIndexOf('/') + 1));
                intent.putExtras(bundle);
                startService(intent);
            }
        });
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            GlobalCode.printLog("landscape>>>");
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            GlobalCode.printLog("portrait>>>");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        GlobalCode.printLog("onstart>>");
    }

    @Override
    protected void onResume() {
        super.onResume();
        GlobalCode.printLog("onresume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        GlobalCode.printLog("onpause");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        GlobalCode.printLog("ondestroy");
    }
}
