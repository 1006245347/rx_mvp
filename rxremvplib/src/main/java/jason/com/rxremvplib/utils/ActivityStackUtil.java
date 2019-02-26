package jason.com.rxremvplib.utils;

import android.app.Activity;
import android.util.Log;

import java.util.Stack;

import jason.com.rxremvplib.global.GlobalCode;

/**
 * Created by jason on 18/5/18.
 */

public class ActivityStackUtil {
    private static Stack<Activity> mActivityStack = new Stack<Activity>();
    private static ActivityStackUtil instance = new ActivityStackUtil();

    private ActivityStackUtil() {
    }

    public static ActivityStackUtil getScreenManager() {
        return instance;
    }

    // 弹出当前activity并销毁
    public void popActivity(Activity activity) {
        if (activity != null) {
            activity.finish();
            mActivityStack.remove(activity);
            activity = null;
        }
    }

    //获取当前显示的activity，注意窗口跳转时要销毁activity，保证index正确
    public Activity getCurAty() {
        if (!mActivityStack.isEmpty()) {
            return mActivityStack.get(mActivityStack.size() - 1);
        }
        return null;
    }

    // 将当前Activity推入栈中
    public void pushActivity(Activity activity) {
        mActivityStack.add(activity);
    }

    // 退出最后一个显示的activity
    public void popLastActivity() {
        if (!mActivityStack.isEmpty()) {
            mActivityStack.pop().finish();  //先进先出
        }
    }

    // 退出栈中所有Activity
    public void clearAllActivity() {
        LoadingDialog.dismissprogress();
        while (!mActivityStack.isEmpty()) {
            Activity activity = mActivityStack.pop();
            if (activity != null) {
                Log.v("TAG", "" + activity.toString());
                activity.finish();
            }
        }
    }

    public void printAllAty() {
        while (!mActivityStack.isEmpty()) {
            GlobalCode.printLog("aty=" + mActivityStack.pop().toString());
        }
    }
}
