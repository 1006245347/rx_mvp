package jason.com.rxremvplib.http;

import java.io.IOException;

import jason.com.rxremvplib.base.BaseApp;
import jason.com.rxremvplib.utils.NetUtil;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class NetWorkInterceptor implements Interceptor {

    private ProgressListener listener;

    public NetWorkInterceptor() {

    }

    public NetWorkInterceptor(ProgressListener listener1) {
        listener = listener1;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {

        Request request = chain.request();
        if (listener != null) {
            Request build = request.newBuilder()    //上传文件 带进度条
                    .method(request.method(), new CountingRequestBody(request.body()
                            , listener)).build();
            return chain.proceed(build);
        }


        //无网络时强制使用缓存
        if (!NetUtil.isConnected(BaseApp.getAppContext())) {
            request = request.newBuilder()
                    .cacheControl(CacheControl.FORCE_CACHE)
                    .build();
        }

        Response response = chain.proceed(request);

        if (NetUtil.isConnected(BaseApp.getAppContext())) {
            // 有网络时，设置超时为0
            int maxStale = 0;
            response.newBuilder()
                    .header("Cache-Control", "public, max-age=" + maxStale)
                    .removeHeader("Pragma")// 清除头信息，因为服务器如果不支持，会返回一些干扰信息，不清除下面无法生效
                    .build();
        } else {
            // 无网络时，设置超时为3周
            int maxStale = 60 * 60 * 24 * 21;
            response.newBuilder()
                    .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                    .removeHeader("Pragma")
                    .build();
        }

        return response;
    }

}
