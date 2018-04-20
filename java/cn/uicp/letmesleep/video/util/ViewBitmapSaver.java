package cn.uicp.letmesleep.video.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by letmesleep on 2016/11/21.
 * QQ:506067668
 */

public class ViewBitmapSaver {
    public static void saveBitmapFromView(Bitmap screenshot, String name) {
        String fileName = name + ".png";


        File f = new File("/sdcard/", fileName);
        if (f.exists()) {
            f.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(f);
            screenshot.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {

            e.printStackTrace();
        } catch (IOException e) {

            e.printStackTrace();
        }
    }
    public static Bitmap loadBitmapByName(String name){
        String fileName = name + ".png";
        File f = new File("/sdcard/", fileName);

        if(!f.exists())
        {
            return null;
        }

        Bitmap bm = BitmapFactory.decodeFile("/sdcard/"+fileName);
        return bm;
    }
}
