package jason.com.rxremvplib.base;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class BaseFragment<P extends BasePresenter> extends Fragment implements BaseView {

    protected P mPresenter;
//    protected View mRootView;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(getContentViewId(), container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mPresenter = onCreatePresenter();
        initFragment();
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onPause() {
        super.onPause();
        if (mPresenter != null) {
            mPresenter.unDisposable();
        }
    }

    @Override
    public Activity getCurContext() {
        return getActivity();
    }


    public <T extends View> T bindView(int id) {
        return (T) getView().findViewById(id);
    }


    protected abstract void initFragment();

    protected abstract P onCreatePresenter();

    protected abstract int getContentViewId();

}
