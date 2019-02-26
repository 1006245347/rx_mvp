package jason.com.rxremvplib.http;

import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import io.reactivex.observers.DefaultObserver;
import jason.com.rxremvplib.base.BaseApp;
import jason.com.rxremvplib.global.GlobalCode;
import jason.com.rxremvplib.utils.ACacheUtil;
import okhttp3.ResponseBody;

/**
 * 文件下载
 * Created by jason on 18/7/4.
 */

public abstract class FileDownLoadObserver<T> extends DefaultObserver<T> {
    @Override
    public void onNext(T t) {
        onDownLoadSuccess(t);
    }

    @Override
    public void onError(Throwable e) {
        onDownLoadFail(e);
    }

    //可以重写，具体可由子类实现
    @Override
    public void onComplete() {
    }

    //下载成功的回调
    public abstract void onDownLoadSuccess(T t);

    //下载失败回调
    public abstract void onDownLoadFail(Throwable throwable);

    //下载进度监听
    public abstract void onProgress(int progress, long total);

    /**
     * 将文件写入本地
     *
     * @param responseBody 请求结果全体
     * @param destFileDir  目标文件夹
     * @param destFileName 目标文件名
     * @return 写入完成的文件
     * @throws IOException IO异常
     */
    public File saveFile(ResponseBody responseBody, String destFileDir, String destFileName) throws IOException {
        InputStream is = null;
        byte[] buf = new byte[2048];
        int len = 0;
        FileOutputStream fos = null;
        try {
            is = responseBody.byteStream();
            long total = responseBody.contentLength();
            Log.v("TAG", "Save_file>>>" + total);
            long sum = 0;

            File dir = new File(destFileDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File file = new File(dir, destFileName);
//            File file = new File(destFileName);
            fos = new FileOutputStream(file);
            while ((len = is.read(buf)) != -1) {
                sum += len;
                fos.write(buf, 0, len);
                long finalSum = sum;
                //这里就是对进度的监听回调
                onProgress((int) (finalSum * 100 / total), total);
                //100%代表完成下载
                if ((int) (100 * sum / total) == 100) {
                    onComplete();
                }
            }
            fos.flush();

            return file;

        } finally {
            try {
                if (is != null) is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (fos != null) fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public File savePointFile(long range, ResponseBody responseBody, String desFileDir, String desFileName) {

        InputStream inputStream = null;
        RandomAccessFile randomAccessFile = null;
        File file = null;
            long total = range;
        try {
            long responseLength = 0;
            byte[] buf = new byte[2048];
            int len = 0;
            responseLength = responseBody.contentLength();
            inputStream = responseBody.byteStream();
            String filePath = desFileDir;
            file = new File(filePath, desFileName);
            File dir = new File(filePath);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            randomAccessFile = new RandomAccessFile(file, "rwd");
            if (range == 0) {
                randomAccessFile.setLength(responseLength);
            }
            randomAccessFile.seek(range);
            int progress = 0;
            int lastProgress = 0;
            while ((len = inputStream.read(buf)) != -1) {
                randomAccessFile.write(buf, 0, len);
                total += len;
                lastProgress = progress;
                progress = (int) (total * 100 / randomAccessFile.length());
                if (progress > 0 && progress != lastProgress) {
                    //返回progress
                    onProgress(progress, responseLength);
                }
            }
            onComplete();
        } catch (Exception e) {
            onError(e);
            e.printStackTrace();
        } finally {
            {
                try {
                    if (randomAccessFile != null) {
                        randomAccessFile.close();
                    }
                    if (inputStream != null) {
                        inputStream.close();
                    }
                    ACacheUtil.setLongCache(BaseApp.getAppContext(), "service", total);
                    GlobalCode.printLog("set_long="+total);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return file;
            }
        }

    }
}
