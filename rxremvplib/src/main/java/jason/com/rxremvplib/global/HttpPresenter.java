package jason.com.rxremvplib.global;

import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import jason.com.rxremvplib.base.BasePresenterImpl;
import jason.com.rxremvplib.base.BaseView;
import jason.com.rxremvplib.global.GlobalModel;
import jason.com.rxremvplib.global.GlobalPresenter;
import jason.com.rxremvplib.http.ApiEngine;
import jason.com.rxremvplib.http.RxSchedulers;
import jason.com.rxremvplib.utils.LoadingDialog;
import okhttp3.ResponseBody;

/**
 * 一个http请求的共用model
 * Created by jason on 18/9/9.
 */

public class HttpPresenter<T extends BaseView> extends BasePresenterImpl<T> implements GlobalPresenter {

    protected GlobalModel mModel;

    public HttpPresenter(T view) {
        super(view);
        this.mModel = new GlobalModel() {
            @Override
            public Observable<ResponseBody> httpMap(String url, Map<String, String> map) {
                return ApiEngine.getInstance().getApiService().doRequestUrl(url, map)
                        .compose(RxSchedulers.<ResponseBody>io2main())
                        .doOnSubscribe(new Consumer<Disposable>() {  //在subscribe（）前调用
                            @Override
                            public void accept(@NonNull Disposable disposable) throws Exception {
                                addDispoable(disposable);   //记录订阅
                                LoadingDialog.showprogress(mView.getCurContext(), "");    //显示等待框
                            }
                        })
                        .doOnTerminate(new Action() {   //action没有返回值，function有返回值
                            @Override
                            public void run() throws Exception {
                                LoadingDialog.dismissprogress();
                                System.out.println("onTerminate>>>>>");
                            }
                        });
            }
        };
    }

    @Override
    public void doHttpRequest(Map<String, String> map) {

    }

    public void doHttpRequest2(Object obj) {

    }

}
