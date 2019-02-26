package jason.com.rxremvplib.base;

import android.app.Application;

import java.io.File;

import jason.com.rxremvplib.global.GlobalCode;

/**
 * Created by jason on 18/6/1.
 */

public class BaseApp extends Application {

    private static BaseApp mApp;

    @Override
    public void onCreate() {
        super.onCreate();
        mApp = this;
//        CrashCatchHandler.getInstance().init(this);
//        CrashCatchHandler crashCatchHandler = CrashCatchHandler.getInstance();
//        crashCatchHandler.init(this);
//        Thread.currentThread().setUncaughtExceptionHandler(crashCatchHandler);
    }

    public static BaseApp getAppContext() {
        return mApp;
    }

    public String createImagePath() {
        int random = ((int) (1 + Math.random() * 10));
        String path = getImageCacheDir() + "/" + (System.currentTimeMillis() + random + ".jpg");
        return path;
    }

    public File getImageCacheDir() {
        return getExternalFilesDir("images");
    }

    public static void doSetIP(String ip) {
        GlobalCode.API_HOST = "http://" + ip;
    }

//                            _ooOoo_
//                           o8888888o
//                           88" . "88
//                           (| -_- |)
//                            O\ = /O
//                        ____/`---'\____
//                      .   ' \\| |// `.
//                       / \\||| : |||// \
//                     / _||||| -:- |||||- \
//                       | | \\\ - /// | |
//                     | \_| ''\---/'' | |
//                      \ .-\__ `-` ___/-. /
//                   ___`. .' /--.--\ `. . __
//                ."" '< `.___\_<|>_/___.' >'"".
//               | | : `- \`.;`\ _ /`;.`/ - ` : | |
//                 \ \ `-. \_ __\ /__ _/ .-` / /
//         ======`-.____`-.___\_____/___.-`____.-'======
//                            `=---='
//
//         .............................................
//                  佛祖保佑             永无BUG
//          佛曰:
//                  写字楼里写字间，写字间里程序员；
//                  程序人员写程序，又拿程序换酒钱。
//                  酒醒只在网上坐，酒醉还来网下眠；
//                  酒醉酒醒日复日，网上网下年复年。
//                  但愿老死电脑间，不愿鞠躬老板前；
//                  奔驰宝马贵者趣，公交自行程序员。
//                  别人笑我忒疯癫，我笑自己命太贱；
//                  不见满街漂亮妹，哪个归得程序员？
}
