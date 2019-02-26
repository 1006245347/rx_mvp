package jason.com.rxremvplib.global;

import jason.com.rxremvplib.base.BaseView;

/**
 * Created by jason on 18/9/28.
 */

public class TabPresenter<V extends BaseView>extends HttpPresenter<V> {
    public TabPresenter(V view) {
        super(view);
    }

    @Override
    public void doHttpRequest2(Object obj) {

    }
}
