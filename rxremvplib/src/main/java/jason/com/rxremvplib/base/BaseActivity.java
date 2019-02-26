package jason.com.rxremvplib.base;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import jason.com.rxremvplib.R;
import jason.com.rxremvplib.global.GlobalCode;
import jason.com.rxremvplib.utils.ActivityStackUtil;
import jason.com.rxremvplib.utils.NetUtil;
import jason.com.rxremvplib.utils.ViewUtil;

public abstract class BaseActivity<P extends BasePresenter> extends AppCompatActivity implements BaseView {

    protected P mPresenter;

    /**
     * Root view
     */
    protected LinearLayout mRootLayout;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BaseInit();
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        setContentView(View.inflate(this, layoutResID, null));
    }


    @Override
    public void setContentView(View view) {
        this.mRootLayout = (LinearLayout) findViewById(R.id.root_layout);
        if (null == mRootLayout) return;
        mRootLayout.addView(view,
                new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        initToolbar();
        initAty();
    }

    protected void BaseInit() {
        mPresenter = onCreatePresenter();
        super.setContentView(R.layout.activity_my_basic_aty);
//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        if (!NetUtil.isConnected(this)) {
            GlobalCode.alert(this, "", "请打开网络连接");
        }
        ActivityStackUtil.getScreenManager().pushActivity(this);
    }


    /**
     * 初始化Toolbar
     */
    private void initToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        /** 设置支持ActionBar，这样就能正常显示overflowmenu了,否则设置了menu也无法在Toolbar中显示和使用 */
        setSupportActionBar(mToolbar);
        /** 去除ActionBar默认Title显示*/
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        if (mToolbar != null) {
            showToolbar(true);

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

    public void showToolbar(boolean show) {         //运行了两次使高度叠加

        if (mToolbar == null) {
//            Log.v("TAG", "Toolbar is null!");
        } else {
            int paddingTop = mToolbar.getPaddingTop();
            int paddingBottom = mToolbar.getPaddingBottom();
            int paddingLeft = mToolbar.getPaddingLeft();
            int paddingRight = mToolbar.getPaddingRight();
            int statusHeight = getStatusHeight(this); //有这句话会变高
//            int statusHeight = 0;
            ViewGroup.LayoutParams params = mToolbar.getLayoutParams();
            int height = params.height;
            /**
             * 利用状态栏的高度，4.4及以上版本给Toolbar设置一个paddingTop值为status_bar的高度，
             * Toolbar延伸到status_bar顶部
             **/
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                //是否添加一层阴影
                setTranslucentStatus(show);
                if (show) {
                    paddingTop += statusHeight;
                    height += statusHeight;
                } else {
                    paddingTop -= statusHeight;
                    height -= statusHeight;
                }
            }
            params.height = height;
            mToolbar.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
            mToolbar.setVisibility(show ? View.VISIBLE : View.GONE);
            mToolbar.setLayoutParams(params);
        }
//        setBarBackground(ContextCompat.getColor(this, R.color.color_basic));
    }

    /**
     * 设置透明状态栏
     * 对4.4及以上版本有效
     *
     * @param on
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;  //有阴影
        } else {
            winParams.flags &= ~bits;     //带颜色 清除FLAG_TRANSLUCENT_STATUS
        }

/*        if (on) {
//            winParams.flags |= bits;
//            winParams.flags &= ~bits;
        } else {
            winParams.flags &= ~bits;
//            winParams.flags |= bits;
        }*/
  /*      if (on) {
            winParams.flags &= ~bits;
//            winParams.flags&=~bit1;
        } else {
            winParams.flags &= ~bits;
        }*/
        win.setAttributes(winParams);
    }

    /**
     * 获得状态栏的高度
     *
     * @param context
     * @return px
     */
    public int getStatusHeight(Context context) {

        int statusHeight = -1;
        try {
            Class<?> clazz = Class.forName("com.android.internal.R$dimen");
            Object object = clazz.newInstance();
            int height = Integer.parseInt(clazz.getField("status_bar_height").get(object).toString());
            statusHeight = context.getResources().getDimensionPixelSize(height);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return statusHeight;
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
        if (mPresenter != null) {
            mPresenter.unDisposable();
        }
        ActivityStackUtil.getScreenManager().popActivity(this);
    }

    protected abstract void initAty();

    @Override
    public Activity getCurContext() {
        return this;
    }

    protected abstract P onCreatePresenter();

    public <T> T bindView(int id) {
        return (T) ViewUtil.f(this, id);
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
