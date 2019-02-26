package jason.com.rxremvplib.http;

/**
 * Created by jason on 18/6/4.
 */

public interface ProgressListener {

    /**
     * @param progress 已经下载或商场的字节数
     * @param total    总字节数
     */
    void onProgress(long progress, long total);
}
