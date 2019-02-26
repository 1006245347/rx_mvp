package jason.com.rxremvplib.base;

import android.content.Intent;
import android.os.Bundle;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import jason.com.rxremvplib.R;
import jason.com.rxremvplib.global.GlobalCode;
import jason.com.rxremvplib.utils.ActivityStackUtil;

/**
 * Created by jason on 18/6/1.
 */

public abstract class BasePresenterImpl<V extends BaseView> implements BasePresenter {
    public BasePresenterImpl(V view) {
        this.mView = view;
        start();
    }

    protected V mView;

    @Override
    public void detach() {
        this.mView = null;
        unDisposable();
    }


    @Override
    public void start() {

    }

    private CompositeDisposable compositeDisposable;

    @Override
    public void addDispoable(Disposable subscription) {
        if (compositeDisposable == null || compositeDisposable.isDisposed()) {
            compositeDisposable = new CompositeDisposable();
        }
        compositeDisposable.add(subscription);
    }


    @Override
    public void unDisposable() {
        if (compositeDisposable != null) {
            compositeDisposable.dispose();
        }
    }

    public void swich2Aty(Class<?> cls) {
        Intent intent = new Intent(mView.getCurContext(), cls);
        mView.getCurContext().startActivity(intent);
        ActivityStackUtil.getScreenManager().clearAllActivity();
        mView.getCurContext().overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
    }
    public void swich2Aty(Class<?> cls, Bundle bundle) {
        Intent intent = new Intent(mView.getCurContext(), cls);
        intent.putExtra("data",bundle);
        mView.getCurContext().startActivity(intent);
        mView.getCurContext().overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
    }

    //数据传参 将自定义对象中的字段取出来放到map
    public <T> Map<String, String> setArgField(T obj) {

        if (obj == null) return null;
        Map<String, String> map = new HashMap<>();
        Field[] fieldArray = getT(obj).getClass().getDeclaredFields();
        for (int i = 0; i < fieldArray.length; i++) {
            Object o = getFieldValueByName(fieldArray[i].getName(), getT(obj));
            GlobalCode.printLog(fieldArray[i].getName() + "=" + (o == null ? "" : (o).toString()));
            map.put(fieldArray[i].getName(), (o == null ? "" : (o).toString()));
        }
        return GlobalCode.encryArgs(map);
    }

    public <T> T getT(T t) {
        return t;
    }


    private Object getFieldValueByName(String fieldName, Object o) {
        try {
            String firstLetter = fieldName.substring(0, 1).toUpperCase();
            String getter = "get" + firstLetter + fieldName.substring(1);
            Method method = o.getClass().getMethod(getter, new Class[]{});
            Object value = method.invoke(o, new Object[]{});
            return value;
        } catch (Exception e) {
            return null;
        }
    }
}
