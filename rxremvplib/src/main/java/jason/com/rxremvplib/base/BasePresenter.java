package jason.com.rxremvplib.base;


import io.reactivex.disposables.Disposable;

public interface BasePresenter {

    void start();

    void detach();

    void addDispoable(Disposable subscription);

    void unDisposable();
}
