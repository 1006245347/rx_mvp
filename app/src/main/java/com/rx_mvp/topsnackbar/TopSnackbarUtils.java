package com.rx_mvp.topsnackbar;

import android.app.Activity;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;


/**
 * Created by jason on 18/11/15.
 */

public class TopSnackbarUtils {

    public static void showTopToastEvent(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        final ViewGroup viewGroup = (ViewGroup) activity.findViewById(android.R.id.content).getRootView();//注意getRootView()最为重要，直接关系到TSnackBar的位置
//        snackBar.setPromptThemBackground(Prompt.SUCCESS).setText("登录成功").setDuration(TSnackbar.LENGTH_LONG).show();
//        snackBar.setPromptThemBackground(Prompt.ERROR).setText("登录失败").setDuration(TSnackbar.LENGTH_LONG).show();
//        TSnackbar.make(viewGroup, "网络已连接", TSnackbar.LENGTH_LONG, TSnackbar.APPEAR_FROM_TOP_TO_DOWN).setPromptThemBackground(Prompt.SUCCESS).show();
//        TSnackbar.make(viewGroup, "网络未连接", TSnackbar.LENGTH_LONG, TSnackbar.APPEAR_FROM_TOP_TO_DOWN).setPromptThemBackground(Prompt.WARNING).show();
        TSnackbar snackBar = TSnackbar.make(viewGroup, "加载中...", TSnackbar.LENGTH_INDEFINITE, TSnackbar.APPEAR_FROM_TOP_TO_DOWN);
        snackBar.setAction("取消", new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        snackBar.setMinHeight(50, 50);
        snackBar.setPromptThemBackground(Prompt.SUCCESS);
        snackBar.addIconProgressLoading(0, true, false);
//        snackBar.addIcon(R.drawable.kprogresshud_spinner, 100, 100);
        snackBar.show();
    }

    public static void showToast(Activity activity, String msg) {   //4.4toolbar会变化。要封装研究下，暂时不全头部显示
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        final ViewGroup viewGroup = (ViewGroup) activity.findViewById(android.R.id.content).getRootView();//注意getRootView()最为重要，直接关系到TSnackBar的位置
//        snackBar.setPromptThemBackground(Prompt.SUCCESS).setText("登录成功").setDuration(TSnackbar.LENGTH_LONG).show();
//        snackBar.setPromptThemBackground(Prompt.ERROR).setText("登录失败").setDuration(TSnackbar.LENGTH_LONG).show();
//        TSnackbar.make(viewGroup, "网络已连接", TSnackbar.LENGTH_LONG, TSnackbar.APPEAR_FROM_TOP_TO_DOWN).setPromptThemBackground(Prompt.SUCCESS).show();
//        TSnackbar.make(viewGroup, "网络未连接", TSnackbar.LENGTH_LONG, TSnackbar.APPEAR_FROM_TOP_TO_DOWN).setPromptThemBackground(Prompt.WARNING).show();
        TSnackbar snackBar = TSnackbar.make(viewGroup, msg, TSnackbar.LENGTH_LONG, TSnackbar.APPEAR_FROM_TOP_TO_DOWN);
        snackBar.setMinHeight(50, 50);
        snackBar.setPromptThemBackground(Prompt.SUCCESS);
        snackBar.show();
    }

    public static void showEorrorToast(Activity activity, String msg) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        final ViewGroup viewGroup = (ViewGroup) activity.findViewById(android.R.id.content).getRootView();//注意getRootView()最为重要，直接关系到TSnackBar的位置
//        snackBar.setPromptThemBackground(Prompt.SUCCESS).setText("登录成功").setDuration(TSnackbar.LENGTH_LONG).show();
//        snackBar.setPromptThemBackground(Prompt.ERROR).setText("登录失败").setDuration(TSnackbar.LENGTH_LONG).show();
//        TSnackbar.make(viewGroup, "网络已连接", TSnackbar.LENGTH_LONG, TSnackbar.APPEAR_FROM_TOP_TO_DOWN).setPromptThemBackground(Prompt.SUCCESS).show();
//        TSnackbar.make(viewGroup, "网络未连接", TSnackbar.LENGTH_LONG, TSnackbar.APPEAR_FROM_TOP_TO_DOWN).setPromptThemBackground(Prompt.WARNING).show();
        TSnackbar snackBar = TSnackbar.make(viewGroup, TextUtils.isEmpty(msg) ? "发生错误!" : msg, TSnackbar.LENGTH_LONG, TSnackbar.APPEAR_FROM_TOP_TO_DOWN);
        snackBar.setMinHeight(50, 50);
        snackBar.setPromptThemBackground(Prompt.ERROR);
        snackBar.show();
    }

    public static void showWarning(Activity activity, String msg) {
        if (Build.VERSION.SDK_INT >=Build.VERSION_CODES.KITKAT && Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        final ViewGroup viewGroup = (ViewGroup) activity.findViewById(android.R.id.content).getRootView();//注意getRootView()最为重要，直接关系到TSnackBar的位置
        TSnackbar snackBar = TSnackbar.make(viewGroup, TextUtils.isEmpty(msg) ? "警告!" : msg, TSnackbar.LENGTH_LONG, TSnackbar.APPEAR_FROM_TOP_TO_DOWN);
        snackBar.setMinHeight(50, 50);
        snackBar.setPromptThemBackground(Prompt.WARNING);
        snackBar.show();
    }
}
