package jason.com.rxremvplib.http;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import jason.com.rxremvplib.base.BaseApp;
import jason.com.rxremvplib.global.GlobalCode;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

public class ApiEngine {

    private volatile static ApiEngine apiEngine;
    private Retrofit retrofit;

    private ApiEngine() {

        //日志拦截器
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);  //不给日志-NONE

        //缓存-地址
//        int size = 1024 * 1024 * 100;
//        File cacheFile = new File(BaseApp.getAppContext().getCacheDir(), "OkHttpCache");
//        Cache cache = new Cache(cacheFile, size);

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(12, TimeUnit.SECONDS)
                .writeTimeout(12, TimeUnit.SECONDS)
                .readTimeout(12, TimeUnit.SECONDS)

//                .addNetworkInterceptor(new NetWorkInterceptor())
                .addInterceptor(loggingInterceptor) //应用上线要将它注释掉，会耗内存
//                .cache(cache)
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl(GlobalCode.API_HOST)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    public static ApiEngine getInstance() {
        if (apiEngine == null) {
            synchronized (ApiEngine.class) {
                if (apiEngine == null) {
                    apiEngine = new ApiEngine();
                }
            }
        }
        return apiEngine;
    }

    //上传文件-带图文参数 image是文件的key
    public void upLoad2File(String url, Map<String, RequestBody> map, String imgKey, File file, FileUploadObserver<ResponseBody> fileUploadObserver) {
        UploadFileRequestBody fileRequestBody = new UploadFileRequestBody(file, fileUploadObserver);
        MultipartBody.Part part = MultipartBody.Part.createFormData(imgKey, file.getName(), fileRequestBody);
        create(Upload2FileApi.class)    //上传api
                .upload2File(url, part, map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(fileUploadObserver);
    }

    public void upload2MoreFile(String url, Map<String, RequestBody> maps, String filekey, List<File> files, FileUploadObserver<ResponseBody> fileUploadObserver) {
        create(Upload2MoreFileApi.class)
                .upload2moreFile(url, filesToMultipartBodyParts(filekey, files, fileUploadObserver), maps)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(fileUploadObserver);
    }

    private List<MultipartBody.Part> filesToMultipartBodyParts(String filekey, List<File> files, FileUploadObserver<ResponseBody> fileUploadObserver) {  //图片的key =image[?]
        List<MultipartBody.Part> parts = new ArrayList<>(files.size());
        for (int i = 0; i < files.size(); i++) {
            UploadFileRequestBody fileRequestBody = new UploadFileRequestBody(files.get(i), fileUploadObserver);
//            RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), files.get(i));
            MultipartBody.Part part = MultipartBody.Part.createFormData((filekey == null ? "image" : filekey) + "[" + i + "]", files.get(i).getName(), fileRequestBody);
            parts.add(part);
        }
        return parts;
    }

    //下载文件
    public void downLoad2File(String url, final String destDir, final String filename, final FileDownLoadObserver<File> fileDownLoadObserver) {
        create(Down2FileApi.class)
                .downLoadFile(url)
                .subscribeOn(Schedulers.io())
//                .unsubscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .map(new Function<ResponseBody, File>() {
                    @Override
                    public File apply(@NonNull ResponseBody responseBody) throws Exception {
                        return fileDownLoadObserver.saveFile(responseBody, destDir, filename);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(fileDownLoadObserver);

    }

    public void pointDownLoad(String url, final long range, final String desDir, final String filename, final FileDownLoadObserver<File> fileDownLoadObserver) {
        //断点续传时请求的总长度
        File file = new File(BaseApp.getAppContext().getImageCacheDir(), filename);
        String totalLength = "-";
        if (file.exists()) {
            totalLength += file.length();
        }
        create(PointDown2FileApi.class)
                .downLoadFile("bytes=" + Long.toString(range) + totalLength, url)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .map(new Function<ResponseBody, File>() {
                    @Override
                    public File apply(ResponseBody responseBody) throws Exception {
                        return fileDownLoadObserver.savePointFile(range, responseBody, desDir, filename);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(fileDownLoadObserver);
    }


    private <T> T create(Class<T> clz) {
        return retrofit.create(clz);
    }

    public RetrofitService getApiService() {
        return retrofit.create(RetrofitService.class);
    }

    interface Upload2FileApi {  //全路径的文件上传
        @Multipart
        @POST
        Observable<ResponseBody> upload2File(@Url String url, @Part MultipartBody.Part file, @PartMap Map<String, RequestBody> map);
    }

    interface Upload2MoreFileApi {      //上传数量不定的图片
        @Multipart
        @POST
        Observable<ResponseBody> upload2moreFile(@Url String url, @Part List<MultipartBody.Part> files, @PartMap() Map<String, RequestBody> map);
    }

    interface Down2FileApi {
        @Streaming      //大文件一定要 ，oom
        @POST
        Observable<ResponseBody> downLoadFile(@Url String url);
    }

    interface PointDown2FileApi {
        @Streaming
        @GET
        Observable<ResponseBody> downLoadFile(@Header("Range") String range, @Url String url);
    }

}
