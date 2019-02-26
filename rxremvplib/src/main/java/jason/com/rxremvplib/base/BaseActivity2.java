package jason.com.rxremvplib.base;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import jason.com.rxremvplib.R;
import jason.com.rxremvplib.global.GlobalCode;
import jason.com.rxremvplib.utils.ActivityStackUtil;
import jason.com.rxremvplib.utils.NetUtil;
import jason.com.rxremvplib.utils.StatusBarUtil;
import jason.com.rxremvplib.utils.ViewUtil;

public abstract class BaseActivity2 extends AppCompatActivity {
    /**
     * Root view
     */
    protected LinearLayout mRootLayout;
    protected View mViewBar;

    /**
     * Toolbar instance
     */
    protected Toolbar mToolbar;
    public ImageView mBarLeftImg;
    public TextView mBarLeftTxt;
    public ImageView mBarCenterImg;
    public TextView mBarCenterTxt;
    public ImageView mBarRightImg;
    public TextView mBarRightTxt;
    public ImageView mBarRightImg2;

    public Toolbar getToolbar() {
        return mToolbar;
    }

    protected Activity mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BaseInit();
    }

    protected void BaseInit() {
        super.setContentView(R.layout.activity_my_basic_aty);
        if (!NetUtil.isConnected(this)) {
            GlobalCode.alert(this, "", "请打开网络连接");
        }
        ActivityStackUtil.getScreenManager().pushActivity(this);
    }

    @Override
    public void setContentView(int layoutResID) {
        setContentView(View.inflate(this, layoutResID, null));
    }

    @Override
    public void setContentView(View view) {
        this.mRootLayout = findViewById(R.id.root_layout);
        if (null == mRootLayout) return;
        mRootLayout.addView(view,
                new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        initToolbar();
        this.mActivity=this;
        initAty();
    }

    private void initToolbar() {
        mToolbar = findViewById(R.id.toolbar);
        mViewBar = findViewById(R.id.viewbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        floatStatusBar();
        if (mToolbar != null) {

            mBarLeftImg = (ImageView) findViewById(R.id.toolbar_left_img);
            mBarLeftTxt = (TextView) findViewById(R.id.toolbar_left_tv);
            mBarCenterImg = (ImageView) findViewById(R.id.toolbar_center_img);
            mBarCenterTxt = (TextView) findViewById(R.id.toolbar_center_tv);
            mBarRightImg = (ImageView) findViewById(R.id.toolbar_right_img);
            mBarRightTxt = (TextView) findViewById(R.id.toolbar_right_tv);
            mBarRightImg2 = (ImageView) findViewById(R.id.toolbar_right_img2);

            mBarLeftImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    navigatBarLeft();
                }
            });
            mBarLeftTxt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    navigatBarLeft();
                }
            });
            mBarRightTxt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    navigatBarRight();
                }
            });
            mBarRightImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    navigatBarRight();
                }
            });
            mBarRightImg2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    navigatBarRight2();
                }
            });
        }
    }

    private void floatStatusBar() {
        StatusBarUtil.fullScreen(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            LinearLayout.LayoutParams titleParams = (LinearLayout.LayoutParams) mViewBar.getLayoutParams();
            // 标题栏在上方留出一段距离，看起来仍在状态栏下方
            titleParams.topMargin = StatusBarUtil.getStatusBarHeight(this);
            mViewBar.setLayoutParams(titleParams);
        }
    }

    protected void showToolbar(boolean isShow) {
        if (!isShow) {
            mToolbar.setVisibility(View.GONE);
        } else {
            mToolbar.setVisibility(View.VISIBLE);
        }
    }

    protected abstract void initAty();

    public <T> T bindView(int id) {
        return (T) ViewUtil.f(this, id);
    }

    // ----------------------------
    // 返回按键（硬件）
    // ----------------------------
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            this.navigatBarLeft();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        if (mPresenter != null) {
//            mPresenter.unDisposable();
//        }
        ActivityStackUtil.getScreenManager().popActivity(this);
    }

    /***
     * 隐藏键盘
     */
    public void hideKeyboard(boolean isHideKeyboard) {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (isHideKeyboard) {
            if (imm.isActive() && getCurrentFocus() != null) {
                if (getCurrentFocus().getWindowToken() != null) {
                    imm.hideSoftInputFromWindow(getCurrentFocus().getApplicationWindowToken(), 0);
                }
            }
        }
    }


    public void setBarTitle(String txt) {
        mBarCenterTxt.setText(txt);
    }

    public void setBarRightTxt(String txt) {
        mBarRightTxt.setText(txt);
    }

    public void setBarRightImg(int res) {
        mBarRightImg.setVisibility(View.VISIBLE);
        mBarRightImg.setImageResource(res);
    }

    public void setBarLeftImg(int res) {
        mBarLeftImg.setImageResource(res);
    }

    public void setBarBackground(int color) {
        mToolbar.setBackgroundColor(color);
    }

    public void navigatBarLeft() {
        finish();
    }

    public void navigatBarRight() {

    }

    public void navigatBarRight2() {

    }
}
