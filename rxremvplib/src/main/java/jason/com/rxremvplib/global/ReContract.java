package jason.com.rxremvplib.global;

import android.support.v4.widget.SwipeRefreshLayout;

import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;

import jason.com.rxremvplib.base.BaseView;

/**
 * Created by jason on 18/9/8.
 */

public class ReContract {

    public interface IRecycleView extends BaseView {
        public SwipeMenuRecyclerView $recycleview();

        SwipeRefreshLayout $refreshlayout();
    }
}
