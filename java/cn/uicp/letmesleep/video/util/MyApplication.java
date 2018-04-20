package cn.uicp.letmesleep.video.util;

import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Mr.G on 2016/6/14.
 */
public class MyApplication extends Application {
    private static Context context;

    public static ProgressDialog progressDialog;
    public static boolean isOnline = false;
    public static Map<String,Boolean> map;

    public static String VIDEO_STATUE = "1";
    public static String ALERT_STATUE = "2";
    //public static String OPEN_VIDEO = "1";

    @Override
    public void onCreate() {
        context = getApplicationContext();
        map = new HashMap<>();

        map.put(VIDEO_STATUE,false);
        map.put(ALERT_STATUE,false);

        //super.onCreate();
    }
    public static Context getContext() {
        return context;
    }

}
