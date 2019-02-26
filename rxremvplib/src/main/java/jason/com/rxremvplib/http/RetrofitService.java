package jason.com.rxremvplib.http;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * 在本类写所有的请求接口
 * Created by jason on 18/5/31.
 */

public interface RetrofitService {
    //    String BASE_IP = "http://www.huitwo.com/";
    String BASE_IP = "https://api.douban.com/";

    String House_IP = "http://120.24.44.102/fuc/api/";

    String Course_IP = "http://api.stay4it.com";

    class UserBean {
    }

    class ResultBean {

    }

    class HttpResponse {

    }


    //用@path就是”/”,用@Query ="? &"
//    @Path：所有在网址中的参数（URL的问号前面），如：
//    http://102.10.10.132/api/Accounts/{accountId}
//    @Query：URL问号后面的参数，如：
//    http://102.10.10.132/api/Comments?access_token={access_token}
//    @QueryMap：相当于多个@Query
//    @Field：用于POST请求，提交单个数据
//    @Body：相当于多个@Field，以对象的形式提交

    //    @GET("News")      //注解中必须要有一部分的url地址，不能光是请求体，否则请求失败
    @GET("v2/movie/top250?start=2&count=2")
// 这个ResultBean 是返回结果体 的封装类，不是请求数据实体
//https://192.168.02.190/News  --注意都是 "/ ",不是"？"连接
    Observable<ResultBean> doLogin();

    @GET("News/{newsId}")
        //https://192.168.02.190/News/newsid
    Observable<UserBean> doLogin(@Path("newsId") String newsId);

//    @GET("News/{newsId}/{type}")
    //https://192.168.02.190/News/newsid/type  --注意都是 "/ ",不是"？"连接
//    Observable<UserBean> doLogin(@Path("newsId") String newsId, @Path("type") String type);

//    @GET("News")    //https://192.168.02.190/News?newsId=1  //这里有？连接还有key-value
//    Observable<UserBean>doLogin(@Query("newsId") String newsId);

//    @GET("News") //https://192.168.02.190/News?newsId=1&type=2  //这里 ？ & 连接
//    Observable<UserBean> doLogin(@Query("newsId") String newsId,@Query("type") String type);

    @GET("News")
        //https://192.168.02.190/News?arg1=1&arg2=2&... //参数不确定
    Observable<UserBean> doLogin(@QueryMap Map<String, String> map);

    @FormUrlEncoded
    @POST("Comments/{newsId}")
        //https://192.168.02.190/newsid
    Observable<UserBean> doLogin(@Path("newsId") String newsid, @Field("reason") String reason);

    @FormUrlEncoded
    @POST("Comments/{newsId}")
        //https://192.168.02.190/newsid?access_token=token
    Observable<UserBean> doLogin(@Path("newsId") String newsid,
                                 @Query("access_token") String token, @Field("reason") String reason);

    //用自定义的实体做请求参数 @body  自定义的接收实体-UserBean
    @FormUrlEncoded //表单数据发送
    @POST("Comments/{newsId}")
    //https://192.168.02.190/newsid?access_token=token
    Observable<UserBean> doLogin(@Path("newsId") String newsid,
                                 @Query("access_token") String token,
                                 @Body UserBean bean);

    //使用map post所有参数，使用同一接口返回实体
    @POST("//")
    Observable<HttpResponse> doLogin(@QueryMap HashMap<String, String> parasMap);


    @GET
        //可以重新定义接口地址，地址以参数的形式传入
    Observable<UserBean> updateLogin(@Url String url, @QueryMap Map<String, String> map);

    @POST("part_url")
    Observable<HttpResponse> doRegiste(@QueryMap HashMap<String, String> map);

    @Multipart  //文件上传的 编码
    @POST("/api/upload")
    Observable<RequestBody> uploadFile(@Part MultipartBody.Part file);

    @FormUrlEncoded
    @POST("/fuc/api/web/userapi/login.html")
    Observable<ResultBean> doHouseLogin(@Path("phone") String phone, @Path("password") String pwd, @Path("type") String type);

    @POST("/fuc/api/web/userapi/login.html")
    Observable<ResultBean> doHouseLogin2();

    @FormUrlEncoded
    @POST("/fuc/api/web/userapi/login.html")
        //数据没有补充到URL后面
    Observable<ResultBean> doHouseLogin3(@Field("key") String key, @Field("data") String data);

    @POST("/fuc/api/web/userapi/login.html")
    Observable<ResultBean> doHouseLogin4(@Query("key") String key, @Query("data") String data);

    @POST("/fuc/api/web/userapi/login.html")
    Observable<ResultBean> doHouseLogin5(@Query("phone") String phone, @Query("password") String pwd, @Query("type") String type);

    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @POST("/fuc/api/web/userapi/login.html")
    Observable<ResponseBody> dohouseLogin6(@Body RequestBody body);

    /*post-请求参数是放在请求体中，不是url的后面，retrofit这里只有这个符合我们要求*/
    @FormUrlEncoded
    @POST("/fuc/api/web/userapi/login.html")
    Observable<ResponseBody> doHouseLogin7(@FieldMap Map<String, String> map);

//    @FormUrlEncoded
//    @POST("{url1}")   //两个拼接 @POST("{url1}/{url2}")
//    Observable<ResponseBody> doHouseLogin8(@Path("url1") String url1, @FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST
        //可以共用 请求的url方法
    Observable<ResponseBody> doHouseLogin8(@Url String url, @FieldMap Map<String, String> map);

    @Streaming
    @POST
    Observable<ResponseBody> downLoadFile(@Url String url, @FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST("/v2/course/list")
    Observable<ResponseBody> getRelistData(@FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST()
    Observable<ResponseBody> doRequestUrl(@Url String url, @FieldMap Map<String, String> map); //共用post

    @GET("{url}")
    Observable<ResponseBody> doGetRequestUrl(@Path("url") String url, @QueryMap Map<String, String> maps);//未验证 共用get
}


