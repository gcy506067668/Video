package cn.uicp.letmesleep.video.util;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;

import static android.content.Context.WIFI_SERVICE;

/**
 * Created by letmesleep on 2016/11/19.
 * QQ:506067668
 */

public class CustomUtil {

    public static String getLocalIPWhenWifi(Context context){
        WifiManager wm = (WifiManager) context.getSystemService(WIFI_SERVICE);
        String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
        if(ip.equals("")||ip.equals("0.0.0.0"))
            return "";
        return ip;
    }

}
