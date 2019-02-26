package jason.com.rxremvplib.http;

import io.reactivex.observers.DefaultObserver;

/**
 * 文件上传的回调
 * Created by jason on 18/6/5.
 */

public abstract class FileUploadObserver<T> extends DefaultObserver<T> {
    @Override
    public void onNext(T t) {
        onUpLoadSuccess(t);
    }

    @Override
    public void onError(Throwable e) {
        onUpLoadFail(e);
    }

    @Override
    public void onComplete() {

    }

    //监听进度的改变
    public  void onProgressChange(long bytesWritten, long contentLength) {
        onProgress((int) (bytesWritten * 100 / contentLength));
    }

    //上传成功的回调
    public abstract void onUpLoadSuccess(T t);

    //上传失败回调
    public abstract void onUpLoadFail(Throwable e);

    //上传进度回调
    public abstract void onProgress(int progress);

}
