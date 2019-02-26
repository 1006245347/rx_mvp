package jason.com.rxremvplib.utils;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import jason.com.rxremvplib.R;
import jason.com.rxremvplib.widget.DYLoadingView;

/**
 * Created by jason on 18/9/1.
 */

public class LoadingDialog extends Dialog {
    private static LoadingDialog mLoadingProgress;

    public LoadingDialog(Context context) {
        super(context);
    }

    public LoadingDialog(Context context, int theme) {
        super(context, theme);
    }

  /*  public static void showprogress(Context context, CharSequence msg) {
        if (null == mLoadingProgress)
            mLoadingProgress = new LoadingDialog(context, R.style.loading_dialog);
        mLoadingProgress.setCanceledOnTouchOutside(false);
        mLoadingProgress.setTitle("");
        mLoadingProgress.setContentView(R.layout.loading_layout);
        mLoadingProgress.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        if (null == msg || TextUtils.isEmpty(msg)) {
            mLoadingProgress.findViewById(R.id.loading_tv).setVisibility(View.GONE);
        } else {
            TextView tv = (TextView) mLoadingProgress.findViewById(R.id.loading_tv);
            tv.setText(msg);
        }
        mLoadingProgress.setCancelable(false);
        mLoadingProgress.show();
    }*/

   /* public static void dismissprogress() {
        if (null != mLoadingProgress) {
            mLoadingProgress.dismiss();
            mLoadingProgress = null;
        }
    }*/

    public static void showprogress(Context context, CharSequence msg) {
        if (null == mLoadingProgress)
            mLoadingProgress = new LoadingDialog(context, R.style.loading_dialog);
        mLoadingProgress.setCanceledOnTouchOutside(false);
        mLoadingProgress.setTitle("");
        mLoadingProgress.setContentView(R.layout.loading_layout);

        mLoadingProgress.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        ((DYLoadingView) mLoadingProgress.findViewById(R.id.loading_dot)).start();
        if (null == msg || TextUtils.isEmpty(msg)) {
            mLoadingProgress.findViewById(R.id.loading_tv).setVisibility(View.GONE);
        } else {
            TextView tv = (TextView) mLoadingProgress.findViewById(R.id.loading_tv);
            tv.setText(msg);
        }
        mLoadingProgress.setCancelable(false);
        mLoadingProgress.show();

    }

    public static void dismissprogress() {
        if (null != mLoadingProgress) {
            ((DYLoadingView) mLoadingProgress.findViewById(R.id.loading_dot)).stop();
            mLoadingProgress.dismiss();
            mLoadingProgress = null;
        }
    }
}
