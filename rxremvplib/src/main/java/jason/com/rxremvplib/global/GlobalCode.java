package jason.com.rxremvplib.global;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.jakewharton.rxbinding2.view.RxView;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.security.PublicKey;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import jason.com.rxremvplib.bean.HttpListResponse;
import jason.com.rxremvplib.http.ApiEngine;
import jason.com.rxremvplib.http.FileDownLoadObserver;
import jason.com.rxremvplib.http.FileUploadObserver;
import jason.com.rxremvplib.http.RxSchedulers;
import jason.com.rxremvplib.utils.AESUtil;
import jason.com.rxremvplib.utils.ActivityStackUtil;
import jason.com.rxremvplib.utils.Base64Utils;
import jason.com.rxremvplib.utils.LoadingDialog;
import jason.com.rxremvplib.utils.MD5Util;
import jason.com.rxremvplib.utils.RSAUtil;
import jason.com.rxremvplib.utils.ViewUtil;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

/**
 * Created by jason on 18/9/1.
 */

public class GlobalCode {

    public static String API_HOST = "";             //服务器ip
    public static String APP_TOKEN = "";            //保持的token
    public static String USER_NAME = "";
    public static String USER_LOGO = "";

    private static boolean IS_DEBUG = true;         //本地日志是否打印
    private static boolean IS_ENCRY = false;        //服务器接口是否加密

    /**
     * 弹出警告对话框
     *
     * @param self
     * @param title
     * @param message
     */
    static public void alert(Context self, String title, String message) {
        new AlertDialog.Builder(self)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("确定", null)
                .show();
    }

    public static void showAlertDialog(Activity self, String title, String msg, DialogInterface.OnClickListener listener) {
        new AlertDialog.Builder(self)
                .setTitle(title)
                .setMessage(msg)
                .setPositiveButton("确定", listener)
                .setNegativeButton("取消", null)
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
//                        Log.v("TAG", dialog + " _dismiss>>");   //事件后自动dismiss,不然泄漏
                    }
                }).show();
    }

    public static void uploadFile(String url, Map<String, RequestBody> map, String key, File file, FileUploadObserver<ResponseBody> observer) {
        if (file != null) {
            ApiEngine.getInstance().upLoad2File(url, encryImgArgs(map), key, file, observer);
        }
    }

    public static void uploadMoreFile(String url, Map<String, RequestBody> map, String key, List<File> files, FileUploadObserver<ResponseBody> observer) {
        if (files != null && files.size() > 0) {
            ApiEngine.getInstance().upload2MoreFile(url, encryImgArgs(map), key, files, observer);
        }
    }

    public static void downloadFile(String url, String desDir, String filename, FileDownLoadObserver<File> fileFileDownLoadObserver) {
        ApiEngine.getInstance().downLoad2File(url,desDir,filename,fileFileDownLoadObserver);
    }

    /**
     * @param className 服务的名字
     * @return  服务是否在运行
     */
    public static boolean isServiceRunning(Activity activity,String className) {
        boolean isRunning=false;
        ActivityManager activityManager = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> infos = activityManager.getRunningServices(Integer.MAX_VALUE);
        if (!(infos.size() > 0)) {
            return false;
        }
        for (int i = 0; i < infos.size(); i++) {
            GlobalCode.printLog("name="+infos.get(i).service.getClassName());
            if (infos.get(i).service.getClassName().equals(className)==true) {
                isRunning=true;
                break;
            }
        }
        GlobalCode.printLog("isruning+"+isRunning);
        return isRunning;
    }
    /**
     * 周期性事件延迟
     */
    public static Disposable intervalFun(long seconds, final FunCallback callback) {
        Disposable disposable = Observable.interval(seconds, TimeUnit.SECONDS)
                .compose(RxSchedulers.<Long>io2main())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        callback.onFunCallback();
                    }
                });
        return disposable;
    }

    /**
     * 事件流延迟
     */
    public static Disposable delayFun(long seconds, final FunCallback callback) {
        Disposable disposable = Observable.timer(seconds, TimeUnit.SECONDS)
                .compose(RxSchedulers.<Long>io2main())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        callback.onFunCallback();
                    }
                });
        return disposable;
    }

    /**
     * 只会发射一次，创建一个延迟的observable
     */
    public static Disposable timerFun(long seconds, final FunCallback callback) {
        Disposable disposable = Observable.timer(seconds, TimeUnit.SECONDS)
                .compose(RxSchedulers.<Long>io2main())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        callback.onFunCallback();
                    }
                });
        return disposable;
    }

    /**
     * 控件点击响应
     */
    public static void clickFun(Activity activity, int viewid, final FunCallback callback) {
        RxView.clicks(ViewUtil.f(activity, viewid))
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        callback.onFunCallback();
                    }
                });
    }

    //加密的公共参数
    public static Map<String, String> encryArgs(Map<String, String> map) {
        if (map == null)
            map = new HashMap<>();
        String mydata = null;
        String mykey = null;
        if (!IS_ENCRY) {
            map.put("app_type", "USER");
            map.put("post_time", "123456");
            map.put("item_key", item_key);
            map.put("nonce", random_16);
            map.put("access_token", APP_TOKEN);
            map.put("encryption", getSign_key());
        } else {
            PublicKey publicKey = null;
            try {
                //key构造
                publicKey = RSAUtil.loadPublicKey(rsa_publickey);
                byte[] result = RSAUtil.encryptData(random_16.getBytes(), publicKey);
                mykey = Base64Utils.encode(result);
                //data构造
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("app_type", "USER");
                jsonObject.put("post_time", "123456");
                jsonObject.put("access_token", APP_TOKEN);
                jsonObject.put("item_key", item_key);
                jsonObject.put("nonce", random_16);
                jsonObject.put("encryption", getSign_key());

                if (map != null) {      //map-是额外的参数
                    for (Map.Entry<String, String> entry : map.entrySet()) {
                        jsonObject.put(entry.getKey(), entry.getValue());
                    }
                }
                mydata = AESUtil.encrypt(String.valueOf(jsonObject), random_16);

            } catch (Exception e) {
                e.printStackTrace();
            }
            mykey = mykey.replace("+", "%2B");
            mydata = mydata.replace("+", "%2B");
            map.put("key", mykey);
            map.put("data", mydata);
        }
        return map;
    }

    public static Map<String, RequestBody> encryImgArgs(Map<String, RequestBody> map) {
        if (map == null)
            map = new HashMap<>();
        String mykey = null;
        String mydata = null;
        if (!IS_ENCRY) {
            map.put("appcode", toRequestBody(img_appcode));
            map.put("encryption", toRequestBody(getImg_key()));
            map.put("nonce", toRequestBody(random_16));
            map.put("time", toRequestBody("123456"));
            map.put("post_time", toRequestBody("123456"));
        } else {
            PublicKey publicKey = null;
            //key构造
            try {
                publicKey = RSAUtil.loadPublicKey(rsa_publickey);
                byte[] result = RSAUtil.encryptData(random_16.getBytes(), publicKey);
                mykey = Base64Utils.encode(result);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("appcode", img_appcode);
                jsonObject.put("encryption", getSign_key());
                jsonObject.put("nonce", random_16);
                jsonObject.put("time", "123456");//json 没有用到
                jsonObject.put("post_time", "123456");
                for (Map.Entry<String, RequestBody> entry : map.entrySet()) {
                    jsonObject.put(entry.getKey(), entry.getValue().toString());
                    printLog(entry.getValue().toString());
                }
                mydata = AESUtil.encrypt(String.valueOf(jsonObject), random_16);
            } catch (Exception e) {
                e.printStackTrace();
            }
            mykey = mykey.replace("+", "%2B");
            mydata = mydata.replace("+", "%2B");
            map.put("key", toRequestBody(mykey));
            map.put("data", toRequestBody(mydata));
        }
        return map;
    }

    public static RequestBody toRequestBody(String value) {
        RequestBody requestBody = RequestBody.create(MediaType.parse("text/plain"), value);
        return requestBody;
    }

    //传入的result对象内存地址会GC，要对这个对象进行开始保存
    public static JSONObject httpJson(String result) {
        String strResult = "";
        strResult = result;
        JSONObject jsonObject = null;
        int code = -1;
        try {
            jsonObject = new JSONObject(strResult);
            code = jsonObject.getInt("code");
            GlobalCode.printLog(String.valueOf(jsonObject));
            switch (code) {
                case 0:
                    if (!IS_ENCRY) {
                        return jsonObject;
                    } else {
                        String skey = jsonObject.getString("key");
                        String sdata = jsonObject.getString("data");
                        byte[] key = null;
                        key = Base64Utils.decode(skey);
                        //16字符串
                        byte[] enctyByte = RSAUtil.decryptData(key, RSAUtil.loadPublicKey(GlobalCode.rsa_publickey));
                        //解密的结果
                        String dataResult = AESUtil.desEncrypt(sdata, new String(enctyByte));
                        String _Result = dataResult;
                        int last = _Result.lastIndexOf("}");    //有时在尾部会有不定数目的乱码
                        _Result = _Result.substring(0, last + 1);
                        _Result = "{\"data\":" + _Result + "}";
                        JSONObject jsonResult = new JSONObject(_Result);
                        return jsonResult;
                    }
                default:
                    LoadingDialog.dismissprogress();
                    GlobalCode.alert(ActivityStackUtil.getScreenManager().getCurAty(), "提示", jsonObject.getString("message"));
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //解析一个对象 //存在错误收集 httpjson
    public static <T> T getHttpResponse(String result, Class<T> cls) {
        String httpResponse = result;
        JSONObject jsonObject = null;
        jsonObject = httpJson(httpResponse);
        if (null == jsonObject) return null;
        String jsonstring = String.valueOf(jsonObject);
        T t = null;
        try {
            JSONObject jsonObject1 = new JSONObject(jsonstring);
            JSONObject jsondata = jsonObject1.getJSONObject("data");

//            printLog(String.valueOf(jsondata));
            Gson gson = new Gson();
            t = gson.fromJson(String.valueOf(jsondata), cls);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return t;
    }

    //存在错误收集 httpjson
    public static <T> HttpListResponse<T> getHttpResponseList(String result, Class<T> cls) {
        JSONObject jsonObject = null;
        jsonObject = httpJson(result);
        if (null == jsonObject) return null;
        String jsonstring = String.valueOf(jsonObject);
        List<T> list = new ArrayList<T>();
        HttpListResponse<T> httpListResponse = new HttpListResponse<>();
        try {
            JSONObject jsonObject1 = new JSONObject(jsonstring);
            JSONObject jsondata = jsonObject1.getJSONObject("data");
            JSONArray jsonArray = jsondata.getJSONArray("select");
            Gson gson = new Gson();
            JsonArray array = new JsonParser().parse(jsonArray.toString()).getAsJsonArray();
            for (JsonElement jsonElement : array) {
                list.add(gson.fromJson(jsonElement, cls));
            }

            JSONObject jsonpage = jsondata.getJSONObject("pagination");
            int pagecount = 1;
            int totalcount = 0;
            if (null != jsonpage) {
                pagecount = jsonpage.getInt("pageCount");
                totalcount = jsonpage.getInt("totalCount");
            }
            HttpListResponse.PageCount pageCount = new HttpListResponse.PageCount();
            pageCount.setPageCount(pagecount);
            pageCount.setTotalCount(totalcount);
            httpListResponse.setSelect(list);
            httpListResponse.setPagination(pageCount);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return httpListResponse;
    }

    /**
     * 根据毫秒时间戳来格式化字符串
     * 今天显示今天、昨天显示昨天、前天显示前天.
     * 早于前天的显示具体年-月-日，如2017-06-12；
     *
     * @param timeStamp 毫秒值
     * @return 今天 昨天 前天 或者 yyyy-MM-dd HH:mm:ss类型字符串
     */
    public static String formatTime1(long timeStamp) {
        long curTimeMillis = System.currentTimeMillis();
        Date curDate = new Date(curTimeMillis);
        int todayHoursSeconds = curDate.getHours() * 60 * 60;
        int todayMinutesSeconds = curDate.getMinutes() * 60;
        int todaySeconds = curDate.getSeconds();
        int todayMillis = (todayHoursSeconds + todayMinutesSeconds + todaySeconds) * 1000;
        long todayStartMillis = curTimeMillis - todayMillis;
        if (timeStamp >= todayStartMillis) {
            return "今天";
        }
        int oneDayMillis = 24 * 60 * 60 * 1000;
        long yesterdayStartMilis = todayStartMillis - oneDayMillis;
        if (timeStamp >= yesterdayStartMilis) {
            return "昨天";
        }
        long yesterdayBeforeStartMilis = yesterdayStartMilis - oneDayMillis;
        if (timeStamp >= yesterdayBeforeStartMilis) {
            return "前天";
        }
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(new Date(timeStamp));
    }

    /**
     * 根据时间戳来判断当前的时间是几天前,几分钟,刚刚
     *
     * @param long_time
     * @return
     */
    public static String formatTime2(String long_time) {
        String long_by_13 = "1000000000000";
        String long_by_10 = "1000000000";
        if (Long.valueOf(long_time) / Long.valueOf(long_by_13) < 1) {
            if (Long.valueOf(long_time) / Long.valueOf(long_by_10) >= 1) {
                long_time = long_time + "000";
            }
        }
        Timestamp time = new Timestamp(Long.valueOf(long_time));
        Timestamp now = new Timestamp(System.currentTimeMillis());
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
//    System.out.println("传递过来的时间:"+format.format(time));
//    System.out.println("现在的时间:"+format.format(now));
        long day_conver = 1000 * 60 * 60 * 24;
        long hour_conver = 1000 * 60 * 60;
        long min_conver = 1000 * 60;
        long time_conver = now.getTime() - time.getTime();
        long temp_conver;
//    System.out.println("天数:"+time_conver/day_conver);
        if ((time_conver / day_conver) < 3) {
            temp_conver = time_conver / day_conver;
            if (temp_conver <= 2 && temp_conver >= 1) {
                return temp_conver + "天前";
            } else {
                temp_conver = (time_conver / hour_conver);
                if (temp_conver >= 1) {
                    return temp_conver + "小时前";
                } else {
                    temp_conver = (time_conver / min_conver);
                    if (temp_conver >= 1) {
                        return temp_conver + "分钟前";
                    } else {
                        return "刚刚";
                    }
                }
            }
        } else {
            return format.format(time);
        }
    }

    /**
     * @return 字符串转时间搓
     */
    public static long getStringToDate(String dateString, String pattern) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern, Locale.CHINESE);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));

        Date date = new Date();
        try {
            date = dateFormat.parse(dateString);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return date.getTime() / 1000;
    }

    /**
     * 权限请求
     * 支持Activity,fragment 在onCreate()调用
     */
    public static void grantCamera(Activity activity) {
        new RxPermissions(activity)
                .requestEach(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(new Consumer<Permission>() {
                    @Override
                    public void accept(@NonNull Permission permission) throws Exception {
                        if (permission.granted) {
                            // 用户已经同意该权限
                            Log.d("TAG", permission.name + " is granted.");
                        } else if (permission.shouldShowRequestPermissionRationale) {
                            // 用户拒绝了该权限，没有选中『不再询问』（Never ask again）,那么下次再次启动时，还会提示请求权限的对话框
                            Log.d("TAG", permission.name + " is denied. More info should be provided.");
                        } else {
                            // 用户拒绝了该权限，并且选中『不再询问』
                            Log.d("TAG", permission.name + " is denied.");
                        }
                    }
                });
    }

    public static void grantLocation(Activity activity, final FunCallback funCallback) {
        new RxPermissions(activity)
                .request(Manifest.permission.ACCESS_FINE_LOCATION)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(@NonNull Boolean aBoolean) throws Exception {
                        if (aBoolean) {
                            funCallback.onFunCallback();
                        } else {
                            System.out.println("location_no >>>");
                        }
                    }
                });
    }

    /**
     * 打印日志 不限行数打印
     *
     * @param log
     */
    public static void printLog(String log) {
        if (!IS_DEBUG) return;
        if (TextUtils.isEmpty(log)) return;
        int segmentSize = 3 * 1024;
        long length = log.length();
        if (length <= segmentSize) {
//            Log.e("TAG", "////////////********0*******/////////////" + "\n" + log);
            Log.e("TAG", log);
        } else {
            while (log.length() > segmentSize) {
                String logContent = log.substring(0, segmentSize);
                log = log.replace(logContent, "");
                Log.e("TAG", "////////////***************/////////////" + "\n" + logContent);
            }
            Log.e("TAG", "////////////***************/////////////" + "\n" + log);
        }
    }

    public static void printLog(Throwable throwable) {
        Log.v("TAG", Log.getStackTraceString(throwable));
    }

    //一般传数据，不传view ，context用全局上下文
//    public static void rxCompressImg(final Context context, String filepath, final ImageView imageView) {
//        new Compressor(context)
//                .compressToFileAsFlowable(new File(filepath))
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Consumer<File>() {
//                    @Override
//                    public void accept(@NonNull File file) {
//                        Glide.with(context).load(file).into(imageView);
//                        Log.v("TAG", "压缩后大小：" + getDataSize(file.length()));
//                    }
//                }, new Consumer<Throwable>() {
//                    @Override
//                    public void accept(@NonNull Throwable throwable) throws Exception {
//                        throwable.printStackTrace();
//                        Log.v("TAG", "压缩失败》》》》");
//                    }
//                });
//
//    }

    public static void clearImgCache(final Context context) {
        //清除内存
        Glide.get(context).clearMemory();
        //清除磁盘
        new Thread(new Runnable() {
            @Override
            public void run() {
                Glide.get(context).clearDiskCache();
            }
        }).start();
    }

    /**
     * 返回byte的数据大小对应的文本
     *
     * @param size
     * @return
     */
    public static String getDataSize(long size) {
        DecimalFormat formater = new DecimalFormat("####.00");
        if (size < 1024) {
            return size + "bytes";
        } else if (size < 1024 * 1024) {
            float kbsize = size / 1024f;
            return formater.format(kbsize) + "KB";
        } else if (size < 1024 * 1024 * 1024) {
            float mbsize = size / 1024f / 1024f;
            return formater.format(mbsize) + "MB";
        } else if (size < 1024 * 1024 * 1024 * 1024) {
            float gbsize = size / 1024f / 1024f / 1024f;
            return formater.format(gbsize) + "GB";
        } else {
            return "size: error";
        }
    }

    public static final String KEY_IMG_TYPE_JPG = "data:image/jpg;base64,";
    //数据加密
    public static final String rsa_publickey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDXwbjehFtm//B/JJPN55IZMQX0" +
            "WuTh54TnmolfrrmnLV1/WTu8yxXfIz60lDbGEupPuzhobZdgqdOoWAIE3GXJ90OY" +
            "gKeEWDfXQfJUZqhWnL9g3b3iKG6VvmXwQ8/BYuB9HTppQh6GW/smKOX41gi/jezMmVkAxQeLkr5AepHS3QIDAQAB";
    public static String item_key = "X4Q2x5kvAQLgyStTRFG9SYjmg19MOBoP";
    public static String item_secret = "0xYyufV5O8LCCd55DRFte9k1rJXnp0Ik";
    public static String random_16 = "1234560000000000";

    //图片加密
    public static final String img_appcode = "WBzHByjzzmwI";
    public static final String img_appkey = "bfcbWQA2vtM1KoNE7m";

    public static String getImg_key() {
        String key = null;
        key = MD5Util.encrypBy(img_appkey + "&" + random_16);
        return key;
    }

    public static String getSign_key() {
        String sign_key = null;
        sign_key = MD5Util.encrypBy(item_secret + "&" + random_16);
        return sign_key;
    }
}
