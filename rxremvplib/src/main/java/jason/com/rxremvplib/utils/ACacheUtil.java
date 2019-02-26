package jason.com.rxremvplib.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;


/**
 * 文件缓存，以文件过期为条件可以删除
 */
public class ACacheUtil {
    public static final int TIME_HOUR = 60 * 60;
    public static final int TIME_DAY = TIME_HOUR * 24;
    private static final int MAX_SIZE = 1000 * 1000 * 100; // 50 mb
    private static final int MAX_COUNT = Integer.MAX_VALUE; // 不限制存放数据的数量
    private static Map<String, ACacheUtil> mInstanceMap = new HashMap<String, ACacheUtil>();
    private ACacheManager mCache;
    private static final String SAFE_KEY = "kxDYERo75XJXpwen";

    /**
     * 使用方法实例-不被调用:步骤
     * //往缓存实例存入数据
     * //新建的文件名为key的整型哈希码。回到put(String key, String value)中，
     * //然后通过out.write(value);将数据存入文件。最后调用mCache.put(file);进行ACacheManager实例的更新操作
     */
    private void myExample(Context context) {
        final ACacheUtil mCacheUtil = ACacheUtil.get(context);
        //缓存drawable \bitmap
        Resources res = context.getResources();
//        Bitmap bitmap = BitmapFactory.decodeResource(res, R.drawable.ic_launcher);
//        mCacheUtil.put("bitmap", bitmap);   //缓存的实际调用方法，不同类型都一样
        //获取
        Bitmap bitmap_get = mCacheUtil.getAsBitmap("bitmap");   //bitmap
        Drawable drawable_get = mCacheUtil.getAsDrawable("drawable");   //drawable
        //(Myobject) mCache.getAsObject("testObject");    //自定义类型对象
        //clear
        mCacheUtil.remove("bitmap");    //不同类型都一样

        //同理 缓存的不同数据类型
        mCacheUtil.getAsJSONArray("jsonarray");
        mCacheUtil.getAsJSONObject("jsonobject");

        //缓存视频-
        new Thread(new Runnable() {
            @Override
            public void run() {
                OutputStream ostream = null;
                try {
                    ostream = mCacheUtil.put("media");  //缓存
                    if (ostream == null) {
                        Log.v("TAG", "open stream error!");
                        return;
                    }
                    URL url = new URL("http://www.baidu.com");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.connect();
                    InputStream instream = conn.getInputStream();

                    byte[] buff = new byte[1024];
                    int counter;
                    while ((counter = instream.read(buff)) > 0) {
                        ostream.write(buff, 0, counter);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        //cache update
                        ostream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Log.v("TAG", "cache_media_done!");  //成功后的处理
                }
            }
        }).start();
        //读取视频
        InputStream in = null;
        try {
            in = mCacheUtil.get("media");
            if (in == null) {
                Log.v("TAG", "media bitmap cache is null>>");
                return;
            }
            Log.v("TAG", "read_data_size=" + in.available());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setStringCache(Context context, String key, String value) {
        ACacheUtil aCacheUtil = ACacheUtil.get(context.getApplicationContext());
        aCacheUtil.put(key, value);
    }

    public static void setBooleanCache(Context context, String key, boolean flag) {
        ACacheUtil aCacheUtil = ACacheUtil.get(context.getApplicationContext());
        if (flag) {
            aCacheUtil.put(key, "1");
        } else {
            aCacheUtil.put(key, "0");
        }
    }

    public static void setBitmapCache(Context context, String key, Bitmap bitmap) {
        ACacheUtil aCacheUtil = ACacheUtil.get(context.getApplicationContext());
        aCacheUtil.put(key, bitmap);
    }

    public static void setIntCache(Context context, String key, int flag) {
        ACacheUtil aCacheUtil = ACacheUtil.get(context.getApplicationContext());
        aCacheUtil.put(key, String.valueOf(flag));
    }

    public static void setLongCache(Context context, String key, long flag) {
        ACacheUtil aCacheUtil = ACacheUtil.get(context.getApplicationContext());
        aCacheUtil.put(key, flag+"");
    }

    public static Long getLongCache(Context context, String key) {
        ACacheUtil aCacheUtil = ACacheUtil.get(context.getApplicationContext());
        String value = aCacheUtil.getAsString(key);
        return Long.valueOf(value);
    }

    public static void setObjCache(Context context, String key, Object obj) {
        ACacheUtil aCacheUtil = ACacheUtil.get(context.getApplicationContext());
        aCacheUtil.put(key, (Serializable) obj);
    }

    public static String getCache(Context context, String key) {
        ACacheUtil aCacheUtil = ACacheUtil.get(context.getApplicationContext());
        return aCacheUtil.getAsString(key);
    }

    public static boolean getBooleanCache(Context context, String key) {
        ACacheUtil aCacheUtil = ACacheUtil.get(context.getApplicationContext());
        String flag = aCacheUtil.getAsString("key");
        return flag.equals("1") ? true : false;
    }

    public static Bitmap getBitmapCache(Context context, String key) {
        ACacheUtil aCacheUtil = ACacheUtil.get(context.getApplicationContext());
        return aCacheUtil.getAsBitmap(key);
    }

    public static int getIntCache(Context context, String key) {
        ACacheUtil aCacheUtil = ACacheUtil.get(context.getApplicationContext());
        String value = aCacheUtil.getAsString(key);
        return Integer.valueOf(value);
    }

    //使用时我们调用此API，后调用后面的get()方法
    public static ACacheUtil get(Context ctx) {
        return get(ctx, "ACacheUtil");  //默认缓存路径 文件名-ACacheUtil
    }

    //新建缓存目录  路径 /data/data/app-package-name/cache/ACacheUtil
    public static ACacheUtil get(Context ctx, String cacheName) {
        File f = new File(ctx.getCacheDir(), cacheName);
        return get(f, MAX_SIZE, MAX_COUNT);
    }

    public static ACacheUtil get(File cacheDir) {
        return get(cacheDir, MAX_SIZE, MAX_COUNT);
    }

    public static ACacheUtil get(Context ctx, long max_zise, int max_count) {
        File f = new File(ctx.getCacheDir(), "ACacheUtil");
        return get(f, max_zise, max_count);
    }

    //新建缓存实例，存入实例map，key为缓存目录+每次应用开启的进程id
    public static ACacheUtil get(File cacheDir, long max_zise, int max_count) {
        ACacheUtil manager = mInstanceMap.get(cacheDir.getAbsoluteFile() + myPid());
        if (manager == null) {
            manager = new ACacheUtil(cacheDir, max_zise, max_count);
            mInstanceMap.put(cacheDir.getAbsolutePath() + myPid(), manager);
        }
        return manager;
    }

    private static String myPid() {
        return "_" + android.os.Process.myPid();
    }

    private ACacheUtil(File cacheDir, long max_size, int max_count) {
        if (!cacheDir.exists() && !cacheDir.mkdirs()) {            //缓存目录不存在并且无法创建时，抛出异常
            throw new RuntimeException("can't make dirs in " + cacheDir.getAbsolutePath());
        }
        mCache = new ACacheManager(cacheDir, max_size, max_count);
    }

    /**
     * Provides a means to save a cached file before the data are available.
     * Since writing about the file is complete, and its close method is called,
     * its contents will be registered in the cache. Example of use:
     * <p>
     * ACacheUtil cache = new ACacheUtil(this) try { OutputStream stream =
     * cache.put("myFileName") stream.write("some bytes".getBytes()); // now
     * update cache! stream.close(); } catch(FileNotFoundException e){
     * e.printStackTrace() }
     */
    class xFileOutputStream extends FileOutputStream {
        File file;

        public xFileOutputStream(File file) throws FileNotFoundException {
            super(file);
            this.file = file;
        }

        public void close() throws IOException {
            super.close();
            mCache.put(file);
        }
    }

    // =======================================
    // ============ String数据 读写 ==============
    // =======================================

    /**
     * 保存 String数据 到 缓存中
     *
     * @param key   保存的key
     * @param value 保存的String数据
     */
    public void put(String key, String value) {
        File file = mCache.newFile(key);
        BufferedWriter out = null;
        try {
            out = new BufferedWriter(new FileWriter(file), 1024);
//            out.write(AESUtil.encrypt(value, SAFE_KEY));
            out.write(value);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.flush();
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            mCache.put(file);
        }
    }

    /**
     * 保存 String数据 到 缓存中
     *
     * @param key      保存的key
     * @param value    保存的String数据
     * @param saveTime 保存的时间，单位：秒
     */
    public void put(String key, String value, int saveTime) {
        put(key, Utils.newStringWithDateInfo(saveTime, value));
    }

    /**
     * 读取 String数据
     * getAsString(String key)方法里首先通过缓存管理器的mCache.get(key)方法获取文件，
     * 然后用Utils.isDue(readString)**判断是否字符串数据到期，未到期返回去除时间信息的字符串内容；到期则移除缓存，返回空。**Utils.isDue(readString)调用了isDue(byte[] data)判断：
     * JsonObject、JsonArray、Bitmap、Drawable、序列化的存入缓存都是转化为字符串/byte格式，再调用函数put(String key, String value)即可。
     *
     * @param key
     * @return String 数据
     */
    public String getAsString(String key) {
        File file = mCache.get(key);            //获取文件
        if (!file.exists())
            return null;
        boolean removeFile = false;
        BufferedReader in = null;
        try {
            in = new BufferedReader(new FileReader(file));
            String readString = "";
            String currentLine;
            while ((currentLine = in.readLine()) != null) {        //逐行遍历
                readString += currentLine;            //每行字符串连接
            }
//            if (!Utils.isDue(AESUtil.desEncrypt(readString, SAFE_KEY))) {            //string数据未到期
            if (!Utils.isDue(readString)) {            //string数据未到期
                return Utils.clearDateInfo(readString);        //去除时间信息的字符串内容
            } else {
                removeFile = true;            //移除文件标志位为真
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (removeFile)
                remove(key);        //移除缓存
        }
    }

    // =======================================
    // ============= JSONObject 数据 读写 ==============
    // =======================================

    /**
     * 保存 JSONObject数据 到 缓存中
     *
     * @param key   保存的key
     * @param value 保存的JSON数据
     */
    public void put(String key, JSONObject value) {
        put(key, value.toString());
    }

    /**
     * 保存 JSONObject数据 到 缓存中
     *
     * @param key      保存的key
     * @param value    保存的JSONObject数据
     * @param saveTime 保存的时间，单位：秒
     */
    public void put(String key, JSONObject value, int saveTime) {
        put(key, value.toString(), saveTime);
    }

    /**
     * 读取JSONObject数据
     *
     * @param key
     * @return JSONObject数据
     */
    public JSONObject getAsJSONObject(String key) {
        String JSONString = getAsString(key);
        try {
            JSONObject obj = new JSONObject(JSONString);
            return obj;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // =======================================
    // ============ JSONArray 数据 读写 =============
    // =======================================

    /**
     * 保存 JSONArray数据 到 缓存中
     *
     * @param key   保存的key
     * @param value 保存的JSONArray数据
     */
    public void put(String key, JSONArray value) {
        put(key, value.toString());
    }

    /**
     * 保存 JSONArray数据 到 缓存中
     *
     * @param key      保存的key
     * @param value    保存的JSONArray数据
     * @param saveTime 保存的时间，单位：秒
     */
    public void put(String key, JSONArray value, int saveTime) {
        put(key, value.toString(), saveTime);
    }

    /**
     * 读取JSONArray数据
     *
     * @param key
     * @return JSONArray数据
     */
    public JSONArray getAsJSONArray(String key) {
        String JSONString = getAsString(key);
        try {
            JSONArray obj = new JSONArray(JSONString);
            return obj;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // =======================================
    // ============== byte 数据 读写 =============
    // =======================================

    /**
     * 保存 byte数据 到 缓存中
     *
     * @param key   保存的key
     * @param value 保存的数据
     */
    public void put(String key, byte[] value) {
        File file = mCache.newFile(key);
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
            out.write(value);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.flush();
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            mCache.put(file);
        }
    }

    /**
     * Cache for a stream
     *
     * @param key the file name.
     * @return OutputStream stream for writing data.
     * @throws FileNotFoundException if the file can not be created.
     */
    public OutputStream put(String key) throws FileNotFoundException {
        return new xFileOutputStream(mCache.newFile(key));
    }

    /**
     * @param key the file name.
     * @return (InputStream or null) stream previously saved in cache.
     * @throws FileNotFoundException if the file can not be opened
     */
    public InputStream get(String key) throws FileNotFoundException {
        File file = mCache.get(key);
        if (!file.exists())
            return null;
        return new FileInputStream(file);
    }

    /**
     * 保存 byte数据 到 缓存中
     *
     * @param key      保存的key
     * @param value    保存的数据
     * @param saveTime 保存的时间，单位：秒
     */
    public void put(String key, byte[] value, int saveTime) {
        put(key, Utils.newByteArrayWithDateInfo(saveTime, value));
    }

    /**
     * 获取 byte 数据
     *
     * @param key
     * @return byte 数据
     */
    public byte[] getAsBinary(String key) {
        RandomAccessFile RAFile = null;
        boolean removeFile = false;
        try {
            File file = mCache.get(key);
            if (!file.exists())
                return null;
            RAFile = new RandomAccessFile(file, "r");
            byte[] byteArray = new byte[(int) RAFile.length()];
            RAFile.read(byteArray);
            if (!Utils.isDue(byteArray)) {
                return Utils.clearDateInfo(byteArray);
            } else {
                removeFile = true;
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (RAFile != null) {
                try {
                    RAFile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (removeFile)
                remove(key);
        }
    }

    // =======================================
    // ============= 序列化 数据 读写 ===============
    // =======================================

    /**
     * 保存 Serializable数据 到 缓存中
     *
     * @param key   保存的key
     * @param value 保存的value
     */
    public void put(String key, Serializable value) {
        put(key, value, -1);
    }

    /**
     * 保存 Serializable数据到 缓存中
     *
     * @param key      保存的key
     * @param value    保存的value
     * @param saveTime 保存的时间，单位：秒
     */
    public void put(String key, Serializable value, int saveTime) {
        ByteArrayOutputStream baos = null;
        ObjectOutputStream oos = null;
        try {
            baos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(baos);
            oos.writeObject(value);
            byte[] data = baos.toByteArray();
            if (saveTime != -1) {
                put(key, data, saveTime);
            } else {
                put(key, data);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                oos.close();
            } catch (IOException e) {
            }
        }
    }

    /**
     * 读取 Serializable数据
     *
     * @param key
     * @return Serializable 数据
     */
    public Object getAsObject(String key) {
        byte[] data = getAsBinary(key);
        if (data != null) {
            ByteArrayInputStream bais = null;
            ObjectInputStream ois = null;
            try {
                bais = new ByteArrayInputStream(data);
                ois = new ObjectInputStream(bais);
                Object reObject = ois.readObject();
                return reObject;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            } finally {
                try {
                    if (bais != null)
                        bais.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    if (ois != null)
                        ois.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;

    }

    // =======================================
    // ============== bitmap 数据 读写 =============
    // =======================================

    /**
     * 保存 bitmap 到 缓存中
     *
     * @param key   保存的key
     * @param value 保存的bitmap数据
     */
    public void put(String key, Bitmap value) {
        put(key, Utils.Bitmap2Bytes(value));
    }

    /**
     * 保存 bitmap 到 缓存中
     *
     * @param key      保存的key
     * @param value    保存的 bitmap 数据
     * @param saveTime 保存的时间，单位：秒
     */
    public void put(String key, Bitmap value, int saveTime) {
        put(key, Utils.Bitmap2Bytes(value), saveTime);
    }

    /**
     * 读取 bitmap 数据
     *
     * @param key
     * @return bitmap 数据
     */
    public Bitmap getAsBitmap(String key) {
        if (getAsBinary(key) == null) {
            return null;
        }
        return Utils.Bytes2Bimap(getAsBinary(key));
    }

    // =======================================
    // ============= drawable 数据 读写 =============
    // =======================================

    /**
     * 保存 drawable 到 缓存中
     *
     * @param key   保存的key
     * @param value 保存的drawable数据
     */
    public void put(String key, Drawable value) {
        put(key, Utils.drawable2Bitmap(value));
    }

    /**
     * 保存 drawable 到 缓存中
     *
     * @param key      保存的key
     * @param value    保存的 drawable 数据
     * @param saveTime 保存的时间，单位：秒
     */
    public void put(String key, Drawable value, int saveTime) {
        put(key, Utils.drawable2Bitmap(value), saveTime);
    }

    /**
     * 读取 Drawable 数据
     *
     * @param key
     * @return Drawable 数据
     */
    public Drawable getAsDrawable(String key) {
        if (getAsBinary(key) == null) {
            return null;
        }
        return Utils.bitmap2Drawable(Utils.Bytes2Bimap(getAsBinary(key)));
    }

    /**
     * 获取缓存文件
     *
     * @param key
     * @return value 缓存的文件
     */
    public File file(String key) {
        File f = mCache.newFile(key);
        if (f.exists())
            return f;
        return null;
    }

    /**
     * 移除某个key
     *
     * @param key
     * @return 是否移除成功
     */
    public boolean remove(String key) {
        return mCache.remove(key);
    }

    /**
     * 清除所有数据
     */
    public void clear() {
        mCache.clear();
    }

    public static void clearCache(Context context) {
        ACacheUtil cacheUtil = ACacheUtil.get(context.getApplicationContext());
        cacheUtil.clear();
    }

    /**
     * @author 杨福海（michael） www.yangfuhai.com
     * @version 1.0
     * @title 缓存管理器
     */
    public class ACacheManager {
        private final AtomicLong cacheSize;    //量子类，也就是线程安全的
        private final AtomicInteger cacheCount;
        private final long sizeLimit;
        private final int countLimit;
        private final Map<File, Long> lastUsageDates = Collections.synchronizedMap(new HashMap<File, Long>());
        protected File cacheDir;

        private ACacheManager(File cacheDir, long sizeLimit, int countLimit) {
            this.cacheDir = cacheDir;
            this.sizeLimit = sizeLimit;
            this.countLimit = countLimit;
            cacheSize = new AtomicLong();        //原子类实例cacheSize，不用加锁保证线程安全
            cacheCount = new AtomicInteger();    //原子类实例cacheCount，不用加锁保证线程安全
            calculateCacheSizeAndCacheCount();
        }

        /**
         * 计算 cacheSize和cacheCount
         */
        private void calculateCacheSizeAndCacheCount() {
            new Thread(new Runnable() {
                @Override
                public void run() {
//					int size = 0;
                    long size = 0;
                    int count = 0;
                    File[] cachedFiles = cacheDir.listFiles();
                    if (cachedFiles != null) {
                        for (File cachedFile : cachedFiles) {    //返回缓存目录cacheDir下的文件数组
                            size += calculateSize(cachedFile);     //对文件数组遍历
                            count += 1;
                            lastUsageDates.put(cachedFile, cachedFile.lastModified());    //将缓存文件和最后修改时间插入map
                        }
                        cacheSize.set(size);    //设置为给定值
                        cacheCount.set(count);
                    }
                }
            }).start();
        }

        private void put(File file) {
            int curCacheCount = cacheCount.get();        //获取数量
            while (curCacheCount + 1 > countLimit) {    //大于上限
                long freedSize = removeNext();            //移除旧的文件，返回文件大小
                cacheSize.addAndGet(-freedSize);        //更新 size

                curCacheCount = cacheCount.addAndGet(-1);//更新 count
            }
            cacheCount.addAndGet(1);//更新 count

            long valueSize = calculateSize(file);        //计算文件大小
            long curCacheSize = cacheSize.get();        //获取当前缓存大小
            while (curCacheSize + valueSize > sizeLimit) {    //大于上限
                long freedSize = removeNext();            //移除旧的文件，返回文件大小
                curCacheSize = cacheSize.addAndGet(-freedSize);
            }
            cacheSize.addAndGet(valueSize);

            Long currentTime = System.currentTimeMillis();
            file.setLastModified(currentTime);            //设置文件最后修改时间
            lastUsageDates.put(file, currentTime);        //插入map
        }

        //修改时间
        private File get(String key) {
            File file = newFile(key);
            Long currentTime = System.currentTimeMillis();
            file.setLastModified(currentTime);
            lastUsageDates.put(file, currentTime);
            return file;
        }

        private File newFile(String key) {
            return new File(cacheDir, key.hashCode() + "");
        }

        private boolean remove(String key) {
            File image = get(key);
            return image.delete();
        }

        private void clear() {
            lastUsageDates.clear();
            cacheSize.set(0);
            File[] files = cacheDir.listFiles();
            if (files != null) {
                for (File f : files) {
                    f.delete();
                }
            }
        }

        /**
         * 移除旧的文件
         *
         * @return
         */
        private long removeNext() {
            if (lastUsageDates.isEmpty()) {
                return 0;
            }

            Long oldestUsage = null;
            File mostLongUsedFile = null;
            Set<Entry<File, Long>> entries = lastUsageDates.entrySet();
            synchronized (lastUsageDates) {
                for (Entry<File, Long> entry : entries) {
                    if (mostLongUsedFile == null) {
                        mostLongUsedFile = entry.getKey();
                        oldestUsage = entry.getValue();
                    } else {
                        Long lastValueUsage = entry.getValue();
                        if (lastValueUsage < oldestUsage) {
                            oldestUsage = lastValueUsage;
                            mostLongUsedFile = entry.getKey();
                        }
                    }
                }
            }

            long fileSize = calculateSize(mostLongUsedFile);
            if (mostLongUsedFile.delete()) {
                lastUsageDates.remove(mostLongUsedFile);
            }
            return fileSize;
        }

        private long calculateSize(File file) {
            return file.length();
        }
    }

    /**
     * @author 杨福海（michael） www.yangfuhai.com
     * @version 1.0
     * @title 时间计算工具类
     */
    private static class Utils {

        /**
         * 判断缓存的String数据是否到期
         *
         * @param str
         * @return true：到期了 false：还没有到期
         */
        private static boolean isDue(String str) {
            return isDue(str.getBytes());
        }

        /**
         * 判断缓存的byte数据是否到期
         *
         * @param data
         * @return true：到期了 false：还没有到期
         */
        private static boolean isDue(byte[] data) {
            String[] strs = getDateInfoFromDate(data);
            if (strs != null && strs.length == 2) {
                String saveTimeStr = strs[0];
                while (saveTimeStr.startsWith("0")) {
                    saveTimeStr = saveTimeStr.substring(1, saveTimeStr.length());
                }
                long saveTime = Long.valueOf(saveTimeStr);
                long deleteAfter = Long.valueOf(strs[1]);
                if (System.currentTimeMillis() > saveTime + deleteAfter * 1000) {
                    return true;
                }
            }
            return false;
        }

        //在value内容前面加上时间信息
        private static String newStringWithDateInfo(int second, String strInfo) {
            return createDateInfo(second) + strInfo;
        }

        private static byte[] newByteArrayWithDateInfo(int second, byte[] data2) {
            byte[] data1 = createDateInfo(second).getBytes();
            byte[] retdata = new byte[data1.length + data2.length];
            System.arraycopy(data1, 0, retdata, 0, data1.length);
            System.arraycopy(data2, 0, retdata, data1.length, data2.length);
            return retdata;
        }

        private static String clearDateInfo(String strInfo) {
            if (strInfo != null && hasDateInfo(strInfo.getBytes())) {
                strInfo = strInfo.substring(strInfo.indexOf(mSeparator) + 1, strInfo.length());
            }
            return strInfo;
        }

        private static byte[] clearDateInfo(byte[] data) {
            if (hasDateInfo(data)) {
                return copyOfRange(data, indexOf(data, mSeparator) + 1, data.length);
            }
            return data;
        }

        private static boolean hasDateInfo(byte[] data) {
            return data != null && data.length > 15 && data[13] == '-' && indexOf(data, mSeparator) > 14;
        }

        private static String[] getDateInfoFromDate(byte[] data) {
            if (hasDateInfo(data)) {
                String saveDate = new String(copyOfRange(data, 0, 13));
                String deleteAfter = new String(copyOfRange(data, 14, indexOf(data, mSeparator)));
                return new String[]{saveDate, deleteAfter};
            }
            return null;
        }

        private static int indexOf(byte[] data, char c) {
            for (int i = 0; i < data.length; i++) {
                if (data[i] == c) {
                    return i;
                }
            }
            return -1;
        }

        private static byte[] copyOfRange(byte[] original, int from, int to) {
            int newLength = to - from;
            if (newLength < 0)
                throw new IllegalArgumentException(from + " > " + to);
            byte[] copy = new byte[newLength];
            System.arraycopy(original, from, copy, 0, Math.min(original.length - from, newLength));
            return copy;
        }

        private static final char mSeparator = ' ';

        private static String createDateInfo(int second) {
            String currentTime = System.currentTimeMillis() + "";
            while (currentTime.length() < 13) {        //小于13，前面补0
                currentTime = "0" + currentTime;
            }
            return currentTime + "-" + second + mSeparator;    //格式：当前时间-保存时间
        }

        /*
         * Bitmap → byte[]
         */
        private static byte[] Bitmap2Bytes(Bitmap bm) {
            if (bm == null) {
                return null;
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
            return baos.toByteArray();
        }

        /*
         * byte[] → Bitmap
         */
        private static Bitmap Bytes2Bimap(byte[] b) {
            if (b.length == 0) {
                return null;
            }
            return BitmapFactory.decodeByteArray(b, 0, b.length);
        }

        /*
         * Drawable → Bitmap
         */
        private static Bitmap drawable2Bitmap(Drawable drawable) {
            if (drawable == null) {
                return null;
            }
            // 取 drawable 的长宽
            int w = drawable.getIntrinsicWidth();
            int h = drawable.getIntrinsicHeight();
            // 取 drawable 的颜色格式
            Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565;
            // 建立对应 bitmap
            Bitmap bitmap = Bitmap.createBitmap(w, h, config);
            // 建立对应 bitmap 的画布
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, w, h);
            // 把 drawable 内容画到画布中
            drawable.draw(canvas);
            return bitmap;
        }

        /*
         * Bitmap → Drawable
         */
        @SuppressWarnings("deprecation")
        private static Drawable bitmap2Drawable(Bitmap bm) {
            if (bm == null) {
                return null;
            }
            BitmapDrawable bd = new BitmapDrawable(bm);
            bd.setTargetDensity(bm.getDensity());
            return new BitmapDrawable(bm);
        }
    }

}
