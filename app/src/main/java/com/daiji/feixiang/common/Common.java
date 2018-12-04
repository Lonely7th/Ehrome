package com.daiji.feixiang.common;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.DownloadManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.util.Base64;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.daiji.feixiang.BrowserActivity;
import com.daiji.feixiang.application.MyApplication;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Common {
    /**
     * md5加密
     *
     * @param str
     * @return
     * @throws NoSuchAlgorithmException
     * @throws UnsupportedEncodingException
     */
    public static String md5(String str) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest md5 = null;
        String result = "";
        md5 = MessageDigest.getInstance("MD5");

        byte[] digest;
        digest = md5.digest(str.getBytes("UTF-8"));


        for (Byte b : digest) {
            String tmp = Integer.toHexString(b & 0xff);
            if (tmp.length() == 1) {
                tmp = "0" + tmp;
            }
            result += tmp;
        }
        return result;
    }

    /**
     * 存储Preferences数据
     *
     * @param context
     * @param key
     * @param value
     * @return
     */
    public static boolean setPreferences(Context context, String key, String value) {
        SharedPreferences preferences = context.getSharedPreferences("config", Activity.MODE_PRIVATE);
        SharedPreferences.Editor edit = preferences.edit();
        edit.putString(key, value);
        return edit.commit();
    }

    /**
     * 获取Preferences值
     *
     * @param context
     * @param key
     * @return
     */
    public static String getPreferences(Context context, String key, String defValue) {
        SharedPreferences preferences = context.getSharedPreferences("config", Activity.MODE_PRIVATE);
        return preferences.getString(key, defValue);
    }

    /**
     * 存储Preferences数据
     *
     * @param context
     * @param key
     * @param value
     * @return
     */
    public static boolean setPreferences(Context context, String key, boolean value) {
        SharedPreferences preferences = context.getSharedPreferences("config", Activity.MODE_PRIVATE);
        SharedPreferences.Editor edit = preferences.edit();
        edit.putBoolean(key, value);
        return edit.commit();
    }

    /**
     * 存储Preferences数据
     *
     * @param context
     * @param key
     * @param value
     * @return
     */
    public static boolean setPreferences(Context context, String key, int value) {
        SharedPreferences preferences = context.getSharedPreferences("config", Activity.MODE_PRIVATE);
        SharedPreferences.Editor edit = preferences.edit();
        edit.putInt(key, value);
        return edit.commit();
    }

    public static boolean setPreferences(Context context, String key, long value) {
        SharedPreferences preferences = context.getSharedPreferences("config", Activity.MODE_PRIVATE);
        SharedPreferences.Editor edit = preferences.edit();
        edit.putLong(key, value);
        return edit.commit();
    }

    /**
     * 获取Preferences值
     *
     * @param context
     * @param key
     * @return
     */
    public static boolean getPreferences(Context context, String key, boolean defValue) {
        SharedPreferences preferences = context.getSharedPreferences("config", Activity.MODE_PRIVATE);
        return preferences.getBoolean(key, defValue);
    }

    /**
     * 获取Preferences值
     *
     * @param context
     * @param key
     * @param defValue
     * @return
     */
    public static int getPreferences(Context context, String key, int defValue) {
        SharedPreferences preferences = context.getSharedPreferences("config", Activity.MODE_PRIVATE);
        return preferences.getInt(key, defValue);
    }

    public static long getPreferences(Context context, String key, long defValue) {
        SharedPreferences preferences = context.getSharedPreferences("config", Activity.MODE_PRIVATE);
        return preferences.getLong(key, defValue);
    }

    /**
     * 删除
     *
     * @param context
     * @param key
     * @return
     */
    public static boolean removePreferences(Context context, String key) {
        SharedPreferences preferences = context.getSharedPreferences("config", Activity.MODE_PRIVATE);
        SharedPreferences.Editor edit = preferences.edit();
        edit.remove(key);
        return edit.commit();
    }

    /**
     * 获取联系人，需要读取联系人权限
     *
     * @param contentResolver
     * @return
     */
    public static List<HashMap<String, String>> getContacts(ContentResolver contentResolver) {
        List<HashMap<String, String>> lists = new ArrayList<>();
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        Cursor query = contentResolver.query(uri, null, null, null, null);
        while (query.moveToNext()) {
            HashMap<String, String> hashMap = new HashMap<>();
            String display_name = query.getString(query.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String display_number = query.getString(query.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            hashMap.put("name", display_name);
            hashMap.put("number", display_number);
            lists.add(hashMap);
        }
        query.close();
        return lists;
    }

    /**
     * 获取权限
     *
     * @param activity
     * @param permission
     */
    public static void grantedPermission(Activity activity, String permission, int requestCode) {
        int selfPermission = ActivityCompat.checkSelfPermission(activity, permission);
        if (selfPermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{permission}, requestCode);
        }
    }

    /**
     * 批量获取权限
     *
     * @param activity
     * @param permissions
     * @param requestCode
     */
    public static void grantedPermissions(Activity activity, String[] permissions, int requestCode) {
        List<String> p = new ArrayList<>();
        for (String permission : permissions) {
            int selfPermission = ActivityCompat.checkSelfPermission(activity, permission);
            if (selfPermission != PackageManager.PERMISSION_GRANTED) {
                p.add(permission);
            }
        }
        int len = p.size();
        String[] p2 = new String[len];
        for (int i = 0; i < len; i++) {
            p2[i] = p.get(i);
        }
        ActivityCompat.requestPermissions(activity, p2, requestCode);
    }

    /**
     * 判断权限是否已授予
     *
     * @param activity
     * @param permission
     * @return
     */
    public static boolean getPermission(Activity activity, String permission) {
        int selfPermission = ActivityCompat.checkSelfPermission(activity, permission);
        if (selfPermission != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        return true;
    }

    /**
     * 批量判断权限
     *
     * @param activity
     * @param permissions
     * @return
     */
    public static boolean getPermissions(Activity activity, String[] permissions) {
        for (String permission : permissions) {
            int selfPermission = ActivityCompat.checkSelfPermission(activity, permission);
            if (selfPermission != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断给定的服务是否运行
     *
     * @param context
     * @param clazz
     * @return
     */
    public static boolean isRunningService(Context context, Class clazz) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> runningServices = activityManager.getRunningServices(200);
        int len = runningServices.size();
        if (len > 0) {
            for (int i = 0; i < len; i++) {
                if (runningServices.get(i).service.getClassName().equals(clazz.getName())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 判断有没有开启悬浮窗权限
     *
     * @param context
     * @return
     */
    public static boolean getAlertPermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(context)) {
                //没有开启权限
                return false;
            }
        }
        return true;
    }

    /**
     * 加密字符串
     *
     * @param data 待加密的字符串
     * @param key  16位字符串 key
     * @param iv   16位字符串 初始化向量
     * @return
     * @throws Exception
     */
    public static String encrypt(String data, String key, String iv) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        byte[] raw = key.getBytes();
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(iv.getBytes());
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, ivSpec);
        byte[] encrypted = cipher.doFinal(data.getBytes("utf-8"));
        return Base64.encodeToString(encrypted, Base64.DEFAULT);
    }

    /**
     * 解密字符串
     *
     * @param data 已加密字符串
     * @param key  16位字符串 key
     * @param iv   16位字符串 初始化向量
     * @return
     * @throws Exception
     */
    public static String decrypt(String data, String key, String iv) throws Exception {
        byte[] raw = key.getBytes("ASCII");
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        IvParameterSpec ivSpec = new IvParameterSpec(iv.getBytes());
        cipher.init(Cipher.DECRYPT_MODE, skeySpec, ivSpec);
        byte[] encrypted1 = Base64.decode(data, Base64.DEFAULT);
        byte[] original = cipher.doFinal(encrypted1);
        return new String(original, "utf-8");
    }

    /**
     * 隐藏键盘
     *
     * @param context
     * @param activity
     */
    public static void hideKeyboard(Context context, Activity activity) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
    }

    /**
     * 获取url主机
     *
     * @param url 完整URL链接
     * @return
     */
    public static String getHost(String url) {
        if (!url.startsWith("http")) {
            return "";
        }
        String u = url.substring(url.indexOf("://") + 3);
        return u.substring(0, u.indexOf("/"));
    }

    /**
     * 弹出消息
     *
     * @param msg
     */
    public static void alert(String msg) {
        Toast.makeText(MyApplication.getContext(), msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * 跳转至下载界面
     */
    public static void goDownload() {
        Intent intent = new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        MyApplication.getContext().startActivity(intent);
    }

    /**
     * 判断设备是否有写入权限
     *
     * @return
     */
    public static boolean isStoragePermission() {
        if (!Common.getPermissions(BrowserActivity.getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE})) {
            return false;
        }
        //检查挂载情况
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return false;
        }
        return true;
    }

    /**
     * 写入错误日志
     *
     * @param data
     */
    public static void writeLog(String data) throws IOException {
        if (!isStoragePermission()) {
            return;
        }
        String errorFile = getErrorFile();
        FileWriter writer = new FileWriter(errorFile);
        writer.write(data);
        writer.flush();
        writer.close();
    }

    /**
     * 日志记录
     *
     * @param fileName
     * @param data
     * @throws IOException
     */
    public static void writeLog(String fileName, String data) throws IOException {
        if (!isStoragePermission()) {
            return;
        }
        String dir = Environment.getExternalStorageDirectory() + "/feixiang/debug/";
        createDir(dir);
        String fileStr = dir + fileName;
        FileWriter writer = new FileWriter(fileStr);
        writer.write(data);
        writer.flush();
        writer.close();
    }

    /**
     * 获取错误日志文件对象
     *
     * @return
     * @throws IOException
     */
    public static String getErrorFile() throws IOException {
        String dir = Environment.getExternalStorageDirectory() + "/feixiang/error/";
        createDir(dir);
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String filename = dir + format.format(date) + ".txt";
        return filename;
    }

    /**
     * 创建文件夹
     */
    public static void createDir(String dir) {
        File dirFile = new File(dir);
        if (!dirFile.exists()) {
            dirFile.mkdirs();
        }
    }

    /**
     * 获取版本号
     *
     * @return
     * @throws PackageManager.NameNotFoundException
     */
    public static int getVersion() throws PackageManager.NameNotFoundException {
        PackageInfo info = getPackageInfo();
        return info.versionCode;
    }

    /**
     * 获取版本号
     *
     * @return
     * @throws PackageManager.NameNotFoundException
     */
    public static long getAppVersion() throws PackageManager.NameNotFoundException {
        PackageInfo info = getPackageInfo();
        return info.getLongVersionCode();
    }

    /**
     * 获取版本名称
     *
     * @return
     * @throws PackageManager.NameNotFoundException
     */
    public static String getVersionName() throws PackageManager.NameNotFoundException {
        PackageInfo info = getPackageInfo();
        return info.versionName;
    }

    /**
     * 获取应用相关信息
     *
     * @return
     * @throws PackageManager.NameNotFoundException
     */
    public static PackageInfo getPackageInfo() throws PackageManager.NameNotFoundException {
        return MyApplication.getContext().getPackageManager().getPackageInfo(MyApplication.getContext().getPackageName(), 0);
    }

    /**
     * 读取assets文件夹下的资源
     *
     * @param filename
     * @return
     * @throws IOException
     */
    public static String readAssets(String filename) throws IOException {
        InputStream open = MyApplication.getContext().getAssets().open(filename);
        int size = open.available();
        byte[] by = new byte[size];
        open.read(by);
        open.close();
        return new String(by, 0, size, "utf-8");
    }
}
