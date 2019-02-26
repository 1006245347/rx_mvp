package jason.com.rxremvplib.utils;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import jason.com.rxremvplib.R;


/**
 * Created by jason on 18/4/16.
 */

public class GetPicUtil {

    /**
     * 选择图片对话框
     */
    public static void showPicDialog(final Activity mActivity) {
        Dialog dialog = null;
        View localView = LayoutInflater.from(mActivity).inflate(
                R.layout.dialog_add_picture2, null);
        TextView tv_camera = (TextView) localView.findViewById(R.id.tv_camera);
        TextView tv_gallery = (TextView) localView.findViewById(R.id.tv_gallery);
        dialog = new Dialog(mActivity, R.style.custom_dialog);
        dialog.setContentView(localView);
        dialog.getWindow().setGravity(Gravity.CENTER);
        // 设置全屏
        WindowManager windowManager = mActivity.getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.width = display.getWidth() - 80; // 设置宽度
        dialog.getWindow().setAttributes(lp);
        dialog.show();

        final Dialog finalDialog = dialog;
        tv_camera.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finalDialog.dismiss();
                // 拍照
                xiangjiClick(mActivity);
            }
        });

        final Dialog finalDialog1 = dialog;
        tv_gallery.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finalDialog1.dismiss();
                // 从系统相册选取照片
                select_photo(mActivity);
            }
        });
    }

    public static final int REQUEST_PERMISSION_CAMERA = 0x13;
    public static final int REQUEST_PERMISSION_WRITE = 0x14;
    public static final int TAKE_PHOTO = 0x11;//启动相机标识
    public static final int SELECT_PHOTO = 0x12;//启动相册标识
    private static File outputImagepath;//存储拍完照后的图片
    private static Bitmap orc_bitmap;//拍照和相册获取图片的Bitmap

    /**
     * 打开相机
     *
     * @param mActivity
     */
    public static void xiangjiClick(final Activity mActivity) {
        //checkSelfPermission 检测有没有 权限
//        PackageManager.PERMISSION_GRANTED 有权限
//        PackageManager.PERMISSION_DENIED  拒绝权限
        //一定要先判断权限,再打开相机,否则会报错
        if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            //权限发生了改变 true  //  false,没有权限时
            if (ActivityCompat.shouldShowRequestPermissionRationale(mActivity, Manifest.permission.CAMERA)) {
                new AlertDialog.Builder(mActivity).setTitle("PERMISSION REQUEST!")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // 请求授权
                                ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION_CAMERA);
                            }
                        }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //不请求权限的操作
                    }
                }).create().show();
            } else {
                ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION_CAMERA);
            }
        } else {
            take_photo(mActivity);//已经授权了就调用打开相机的方法
        }
    }

    /**
     * 从相册中获取图片
     */
    public static void select_photo(Activity mActivity) {
        if (ContextCompat.checkSelfPermission(mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION_WRITE);
        } else {
            openAlbum(mActivity);
        }
    }

    public static void openAlbum(Activity mActivty) {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        mActivty.startActivityForResult(intent, SELECT_PHOTO);
    }

    /**
     * 拍照获取图片
     **/
    public static void take_photo(Activity mActivity) {
        //获取系統版本
        int currentapiVersion = Build.VERSION.SDK_INT;
        // 激活相机
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // 判断存储卡是否可以用，可用进行存储
        if (hasSdcard()) {
            SimpleDateFormat timeStampFormat = new SimpleDateFormat(
                    "yyyy_MM_dd_HH_mm_ss");
            String filename = timeStampFormat.format(new Date());
            outputImagepath = new File(Environment.getExternalStorageDirectory(),
                    filename + ".jpg");
            if (currentapiVersion < 24) {
                // 从文件中创建uri
                Uri uri = Uri.fromFile(outputImagepath);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            } else {
                //兼容android7.0 使用共享文件的形式
                ContentValues contentValues = new ContentValues(1);
                contentValues.put(MediaStore.Images.Media.DATA, outputImagepath.getAbsolutePath());
                Uri uri = mActivity.getApplication().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            }
        }
        // 开启一个带有返回值的Activity，请求码为PHOTO_REQUEST_CAREMA
        mActivity.startActivityForResult(intent, TAKE_PHOTO);
    }

    /*
 * 判断sdcard是否被挂载
 */
    public static boolean hasSdcard() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }

    //在Activity的onActivityResult()调用,拿到图片路径同时图片可以显示在控件上
    public static String handlePicResult(Activity mActivity, Intent data, int requestCode, int resultCode) {
        String imagepath = null;
        switch (requestCode) {
            //打开相机后返回
            case TAKE_PHOTO:
                if (resultCode == mActivity.RESULT_OK) {
                    /**
                     * 这种方法是通过内存卡的路径进行读取图片，所以的到的图片是拍摄的原图
                     */
                    imagepath = displayImage(mActivity, outputImagepath.getAbsolutePath());
//                    imagepath = myCompressBitmap(mActivity, outputImagepath.getAbsolutePath());

                    Log.v("TAG", "拍照图片路径>>>>" + outputImagepath);
                }
                break;
            //打开相册后返回
            case SELECT_PHOTO:
                if (resultCode == mActivity.RESULT_OK) {
                    //判断手机系统版本号
                    if (Build.VERSION.SDK_INT > 19) {
                        //4.4及以上系统使用这个方法处理图片
                        String imgPath = handleImgeOnKitKat(mActivity, data);
//                        imagepath = myCompressBitmap(mActivity, imgPath);
                        imagepath = imgPath;
                    } else {
                        String imgPath = handleImageBeforeKitKat(mActivity, data);
//                        imagepath = myCompressBitmap(mActivity, imgPath);
                        imagepath = imgPath;
                    }
                }
                break;
            default:
                break;
        }
        return imagepath;
    }

    //压缩图片
    public static String myCompressBitmap(Activity activity, String pathName) {
        String newPath = createImagePath(activity);
        // 设置参数
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true; // 只获取图片的大小信息，而不是将整张图片载入在内存中，避免内存溢出
        BitmapFactory.decodeFile(pathName, options);
        int height = options.outHeight;
        int width = options.outWidth;
        int inSampleSize = 2; // 默认像素压缩比例，压缩为原图的1/2
        int minLen = Math.min(height, width); // 原图的最小边长
        if (minLen > 100) { // 如果原始图像的最小边长大于100dp（此处单位我认为是dp，而非px）
            float ratio = (float) minLen / 330.0f; // 计算像素压缩比例  3M->120kb
            inSampleSize = (int) ratio;
        }
        options.inJustDecodeBounds = false; // 计算好压缩比例后，这次可以去加载原图了
        options.inSampleSize = inSampleSize; // 设置为刚才计算的压缩比例
        Bitmap bm = BitmapFactory.decodeFile(pathName, options); // 解码文件
//        img_idcard.setImageBitmap(bm);

        Log.v("TAG", bm.getByteCount() + " size");
        FileOutputStream fos = null;
        try {

            fos = new FileOutputStream(newPath);
            bm.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return newPath;
    }

    private static String createImagePath(Activity activity) {
        String path = getImagesCacheDir(activity) + "/" + (System.currentTimeMillis() + ".jpg");
        return path;
    }

    private static File getImagesCacheDir(Activity activity) {
        return activity.getExternalFilesDir("images");
    }

    /**
     * 4.4以下系统处理图片的方法
     */
    private static String handleImageBeforeKitKat(Activity mActivity, Intent data) {
        Uri uri = data.getData();
        String imagePath = getImagePath(mActivity, uri, null);
        return displayImage(mActivity, imagePath);

    }

    /**
     * 4.4及以上系统处理图片的方法
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private static String handleImgeOnKitKat(Activity mActivity, Intent data) {
        String imagePath = null;
        Uri uri = data.getData();
        Log.d("uri=intent.getData :", "" + uri);
        if (DocumentsContract.isDocumentUri(mActivity, uri)) {
            String docId = DocumentsContract.getDocumentId(uri);        //数据表里指定的行
            Log.d("getDocumentId(uri) :", "" + docId);
            Log.d("uri.getAuthority() :", "" + uri.getAuthority());
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1];
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(mActivity, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(mActivity, contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            imagePath = getImagePath(mActivity, uri, null);
        }
        return displayImage(mActivity, imagePath);

    }

    /**
     * 通过uri和selection来获取真实的图片路径,从相册获取图片时要用
     */
    private static String getImagePath(Activity mActivity, Uri uri, String selection) {
        String path = null;
        Cursor cursor = mActivity.getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    /**
     * 拍完照和从相册获取玩图片都要执行的方法(根据图片路径显示图片)
     */
    private static String displayImage(Activity activity, String imagePath) {
        String newpath = null;
        if (!TextUtils.isEmpty(imagePath)) {
//            orc_bitmap = comp(BitmapFactory.decodeFile(imagePath)); //压缩图片
//            saveBitmap2Path(activity, orc_bitmap);  //路劲不对
            newpath = myCompressBitmap(activity, imagePath);
//            ImgUpdateDirection(imagePath, imageView);//显示图片,并且判断图片显示的方向,如果不正就放正
        } else {
//            Toast.makeText(this, "图片获取失败", Toast.LENGTH_LONG).show();
        }
        return newpath;
    }

    //比例压缩
    private static Bitmap comp(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        if (baos.toByteArray().length / 1024 > 1024) {//判断如果图片大于1M,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出
            baos.reset();//重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//这里压缩50%，把压缩后的数据存放到baos中
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        //开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        //现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
        float hh = 800f;//这里设置高度为800f
        float ww = 480f;//这里设置宽度为480f
        //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;//be=1表示不缩放
        if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;//设置缩放比例
        newOpts.inPreferredConfig = Bitmap.Config.RGB_565;//降低图片从ARGB888到RGB565
        //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        isBm = new ByteArrayInputStream(baos.toByteArray());
        bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        return bitmap;//压缩好比例大小后再进行质量压缩
    }

    //改变拍完照后图片方向不正的问题
    private static void ImgUpdateDirection(String filepath, ImageView img_pic) {
        int digree = 0;//图片旋转的角度
        //根据图片的URI获取图片的绝对路径
        Log.i("tag", ">>>>>>>>>>>>>开始");
        //String filepath = ImgUriDoString.getRealFilePath(getApplicationContext(), uri);
        Log.i("tag", "》》》》》》》》》》》》》》》" + filepath);
        //根据图片的filepath获取到一个ExifInterface的对象
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(filepath);
            Log.i("tag", "exif》》》》》》》》》》》》》》》" + exif);
            if (exif != null) {

                // 读取图片中相机方向信息
                int ori = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);

                // 计算旋转角度
                switch (ori) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        digree = 90;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        digree = 180;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        digree = 270;
                        break;
                    default:
                        digree = 0;
                        break;
                }

            }
            //如果图片不为0
            if (digree != 0) {
                // 旋转图片
                Matrix m = new Matrix();
                m.postRotate(digree);
                orc_bitmap = Bitmap.createBitmap(orc_bitmap, 0, 0, orc_bitmap.getWidth(),
                        orc_bitmap.getHeight(), m, true);
            }
            if (orc_bitmap != null) {
//                img_pic.setImageBitmap(orc_bitmap);
                img_pic.setImageBitmap(compressImage(orc_bitmap));
            }
        } catch (IOException e) {
            e.printStackTrace();
            exif = null;
        }
    }

    /**
     * 质量压缩方法
     *
     * @param image
     * @return
     */
    public static Bitmap compressImage(Bitmap image) {
        if (image != null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
            int options = 100;
            while (baos.toByteArray().length / 1024 > 100) { //循环判断如果压缩后图片是否大于100kb,大于继续压缩
                baos.reset();//重置baos即清空baos
                options -= 10;//每次都减少10
                image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中

            }
            ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
            Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
            return bitmap;
        } else {
            return null;
        }
    }

    /**
     * 保存bitmap到本地
     *
     * @param mBitmap
     * @return
     */
    public static String saveBitmap2Path(Activity activity, Bitmap mBitmap) {

        String savePath = createImagePath(activity);
        File filePic;

        try {
            filePic = new File(savePath);
            if (!filePic.exists()) {
                filePic.getParentFile().mkdirs();
                filePic.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(filePic);
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }

        return filePic.getAbsolutePath();
    }

    //设置新的宽高
    public Bitmap zoomImg(Bitmap bm, int newWidth, int newHeight) {
        // 获得图片的宽高
        int width = bm.getWidth();
        int height = bm.getHeight();
        // 计算缩放比例
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // 得到新的图片   www.2cto.com
        Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
        return newbm;
    }

    /**
     * 删除文件夹
     * GetPicUtil.deleteAllFiles(this.getExternalFilesDir("images"));
     */
    public static void deleteAllFiles(File root) {

        File files[] = root.listFiles();
        if (files != null)
            for (File f : files) {
                if (f.isDirectory()) { // 判断是否为文件夹
                    deleteAllFiles(f);
                    try {
                        f.delete();
                    } catch (Exception e) {
                    }
                } else {
                    if (f.exists()) { // 判断是否存在
                        deleteAllFiles(f);
                        try {
                            f.delete();
                        } catch (Exception e) {
                        }
                    }
                }
            }
    }

    //删除文件
    public static void delFile(String Path) {
        try {
            File file = new File(Path);
            if (file.isFile()) {
                file.delete();
            }
            file.exists();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
