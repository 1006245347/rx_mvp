package jason.com.rxremvplib.global;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.widget.Toast;

import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import jason.com.rxremvplib.base.BasePresenterImpl;

import jason.com.rxremvplib.bean.HttpListResponse;
import jason.com.rxremvplib.http.ApiEngine;
import jason.com.rxremvplib.http.RxSchedulers;
import okhttp3.ResponseBody;

/**
 * 侧滑与 下拉上拉 功能不能共用
 * 列表共用的 presenter
 * Created by jason on 18/9/10.
 */

public abstract class ReHttpListPresenter<T extends ReContract.IRecycleView, D> extends BasePresenterImpl<T> implements GlobalPresenter,
        SwipeMenuRecyclerView.LoadMoreListener, SwipeRefreshLayout.OnRefreshListener {
    protected GlobalModel mModel;
    protected int mCurrentPage = 0;
    protected int mPageCount = 0;
    protected Map<String, String> mArgsMap = new HashMap<>();
    protected String mHttpUrl = null;
    protected List<D> mDataList = new ArrayList<>();

    //必须继承
    public ReHttpListPresenter(T view, String url) {
        super(view);
        this.mHttpUrl = url;
        this.mModel = new GlobalModel() {
            @Override
            public Observable<ResponseBody> httpMap(String url, Map<String, String> map) {
                return ApiEngine.getInstance().getApiService().doRequestUrl(url + "?page=" + mCurrentPage, map) //注意加密
                        .compose(RxSchedulers.<ResponseBody>io2main())
                        .doOnSubscribe(new Consumer<Disposable>() {
                            @Override
                            public void accept(@NonNull Disposable disposable) throws Exception {
                                addDispoable(disposable);
                            }
                        });
            }
        };
    }

    @Override
    public void doHttpRequest(Map<String, String> map) {
        mView.$recycleview().setLayoutManager(new LinearLayoutManager(mView.getCurContext()));
        mView.$recycleview().setLoadMoreListener(this);
        mView.$refreshlayout().setOnRefreshListener(this);
        onRefresh();
    }

    @Override
    public void onRefresh() {
        setReAdapter();
        mArgsMap.clear();
        loadData(setArgsMap(), false);
    }

    @Override
    public void onLoadMore() {
        GlobalCode.printLog("load_more>>>>");
        if (mCurrentPage < mPageCount) {
            mArgsMap.clear();
            loadData(setArgsMap(), true);
        } else {
            Toast.makeText(mView.getCurContext(), "已经到底了!", Toast.LENGTH_SHORT).show();
//            mView.$recycleview().loadMoreFinish(false,true);
        }
    }

    //必须重写
    protected void setReAdapter() {
        mCurrentPage = 0;
        mDataList.clear();
    }


    //传参数-必须重写
    protected Map<String, String> setArgsMap() {
        return mArgsMap;
    }

    //数据处理-必须重写
    protected abstract void handlerData(String result, boolean ismore);

    protected void loadData(Map<String, String> map, final boolean ismore) {
        handlerData(str_json2, ismore);
     /*   mModel.httpMap(mHttpUrl, GlobalCode.encryArgs(map)).subscribe(new Consumer<ResponseBody>() {
            @Override
            public void accept(@NonNull ResponseBody responseBody) throws Exception {
                handlerData(responseBody.string(), ismore);
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(@NonNull Throwable throwable) throws Exception {
                GlobalCode.printLog(Log.getStackTraceString(throwable));
                if (!ismore) {
                    mView.$refreshlayout().setRefreshing(false);
                }
                mView.$recycleview().loadMoreFinish(false, true);
            }
        });*/
    }

    String str_json2 = "{\"code\":\"0\",\"message\":\"获取成功\"," +
            "\"data\":{\"select\":[{\"id\":4,\"user_id\":14,\"name\":\"罗泉清\",\"phone\":\"15989778215\",\"gender\":1,\"car_plate\":\"粤C2N661\",\"id_card\":\"441481199010124458\",\"car_type\":\"大货车\"}," +
            "{\"id\":6,\"user_id\":14,\"name\":\"罗泉清\",\"phone\":\"15989778215\",\"gender\":1,\"car_plate\":\"粤C2N661\",\"id_card\":\"441481199010124999\",\"car_type\":\"大货车\"}" +
            ",{\"id\":7,\"user_id\":15,\"name\":\"罗泉清\",\"phone\":\"15989778215\",\"gender\":1,\"car_plate\":\"粤C2N661\",\"id_card\":\"441481199010124999\",\"car_type\":\"大货车\"}" +
            ",{\"id\":7,\"user_id\":15,\"name\":\"罗泉清\",\"phone\":\"15989778215\",\"gender\":1,\"car_plate\":\"粤C2N661\",\"id_card\":\"441481199010124999\",\"car_type\":\"大货车\"}" +
            ",{\"id\":7,\"user_id\":15,\"name\":\"罗泉清\",\"phone\":\"15989778215\",\"gender\":1,\"car_plate\":\"粤C2N661\",\"id_card\":\"441481199010124999\",\"car_type\":\"大货车\"}" +
            ",{\"id\":7,\"user_id\":15,\"name\":\"罗泉清\",\"phone\":\"15989778215\",\"gender\":1,\"car_plate\":\"粤C2N661\",\"id_card\":\"441481199010124999\",\"car_type\":\"大货车\"}" +
            ",{\"id\":7,\"user_id\":15,\"name\":\"罗泉清\",\"phone\":\"15989778215\",\"gender\":1,\"car_plate\":\"粤C2N661\",\"id_card\":\"441481199010124999\",\"car_type\":\"大货车\"}" +
            "]," +
            "\"pagination\":{\"totalCount\":4,\"pageCount\":3}}}";

}
