package jason.com.rxremvplib.global;

import java.util.Map;

import io.reactivex.Observable;
import jason.com.rxremvplib.base.BaseModel;
import okhttp3.ResponseBody;

/**
 * 接收参数进行网络请求，返回回调实例
 * Created by jason on 18/9/8.
 */

public interface GlobalModel extends BaseModel {

    /**
     * @param url post请求url
     * @param map post请求参数，可以加入公共参数
     * @return
     */
    Observable<ResponseBody> httpMap(String url, Map<String, String> map);
}
