package jason.com.rxremvplib.base;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import jason.com.rxremvplib.R;

/**
 * Created by jason on 18/11/2.
 */

public abstract class BaseTabAty<P extends BasePresenter> extends BaseActivity<P> implements TabHost.OnTabChangeListener {

    protected TabHost mTabHost;
    protected ImageView img_unread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected P onCreatePresenter() {
        return null;
    }

    @Override
    protected void initAty() {
        this.mTabHost = bindView(R.id.tabhost);
        this.mTabHost.setup();
        this.mTabHost.setOnTabChangedListener(this);
    }

    protected void setNavBar(String label, int selectID, int normalID, int tabID) {
        StateListDrawable drawable = new StateListDrawable();

        drawable.addState(new int[]{-android.R.attr.state_focused, -android.R.attr.state_selected, -android.R.attr.state_pressed},
                ContextCompat.getDrawable(this, normalID));
        drawable.addState(new int[]{-android.R.attr.state_focused, android.R.attr.state_selected, -android.R.attr.state_pressed},
                ContextCompat.getDrawable(this, selectID));
        drawable.addState(new int[]{android.R.attr.state_focused, -android.R.attr.state_selected, -android.R.attr.state_pressed},
                ContextCompat.getDrawable(this, selectID));
        drawable.addState(new int[]{android.R.attr.state_focused, android.R.attr.state_selected, -android.R.attr.state_pressed},
                ContextCompat.getDrawable(this, selectID));
        this.addTab(label, drawable, tabID);
    }

    protected void selectTab(String label) {
    }

    @Override
    public void onTabChanged(String label) {
        switch (label) {
            case "A":
                System.out.println("create_a");
                break;
            case "B":
                System.out.println("create_B");
                break;
            case "C":
                System.out.println("create_c");
                break;
        }
        selectTab(label);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        System.out.println("Tab_onNewIntent>>>");
        overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
    }

    /***
     * 添加Tab
     */
    protected void addTab(String label, Drawable drawable, int tabId) {
        TabHost.TabSpec spec = mTabHost.newTabSpec(label);

        View tabIndicator = LayoutInflater.from(this).inflate(R.layout.tabbar_indicator, mTabHost.getTabWidget(), false);

        TextView title = (TextView) tabIndicator.findViewById(R.id.title);
        title.setText(label);

        ImageView icon = (ImageView) tabIndicator.findViewById(R.id.icon);
        icon.setImageDrawable(drawable);
        icon.setScaleType(ImageView.ScaleType.FIT_CENTER);

        addMsgTip(tabIndicator,label);

        spec.setIndicator(tabIndicator);
        spec.setContent(tabId);
        mTabHost.addTab(spec);
    }

    protected void addMsgTip(View tabindicator, String label) {
    }
}
