package cn.uicp.letmesleep.video.gstreamer;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.animation.PropertyValuesHolder;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.freedesktop.gstreamer.GStreamer;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import cn.uicp.letmesleep.video.R;
import cn.uicp.letmesleep.video.util.HttpUtil;
import cn.uicp.letmesleep.video.util.ViewBitmapSaver;


public class Gstreamer3 extends Activity implements SurfaceHolder.Callback,View.OnTouchListener {
    private float bx, by, ax, ay;

    private native void nativeInit();     // Initialize native code, build pipeline, etc

    private native void nativeFinalize(); // Destroy pipeline and shutdown native code

    private native void nativePlay();     // Set pipeline to PLAYING

    private native void nativePause();    // Set pipeline to PAUSED

    private static native boolean nativeClassInit(); // Initialize native class: cache Method IDs for callbacks

    private native void nativeSurfaceInit(Object surface);

    private native void nativeSurfaceFinalize();

    private long native_custom_data;      // Native code will use this to keep private data

    private boolean is_playing_desired;   // Whether the user asked to go to PLAYING

    private SurfaceView sv;
    private String monitorId;
    private LinearLayout operatorLayer;
    private ImageButton openVoice;
    private ImageButton saveScreen;
    private TextView clockTv;

    private ImageView videoRise;
    private ImageView videoDrop;
    //private TemporaryData temporaryData;
    // Called when the activity is first created.
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // Initialize GStreamer and warn if it fails
        try {
            GStreamer.init(this);
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        setContentView(R.layout.activity_video);
        initOperatorLayer();
        monitorId = getIntent().getExtras().getString("monitorId","null");
        ImageButton play = (ImageButton) this.findViewById(R.id.button_play);



        //视频已开启
//        temporaryData=TemporaryData.getInstance();
//        temporaryData.setFlagIsVideo(true);

        play.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                is_playing_desired = true;
                Log.d("chen", "playing");
                nativePlay();
            }
        });

        ImageButton pause = (ImageButton) this.findViewById(R.id.button_stop);
        pause.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                is_playing_desired = false;
                Log.d("chen", "pause");
                nativePause();
            }
        });

        //final RelativeLayout setNameRelative=(RelativeLayout)findViewById(R.id.relative_set_name);


        sv = (SurfaceView) this.findViewById(R.id.surface_video);
        SurfaceHolder sh = sv.getHolder();

        sh.addCallback(this);

        sv.setOnTouchListener(this);
        if (savedInstanceState != null) {
            is_playing_desired = savedInstanceState.getBoolean("playing");
            Log.i("GStreamer", "Activity created. Saved state is playing:" + is_playing_desired);
        } else {
            is_playing_desired = false;
            Log.i("GStreamer", "Activity created. There is no saved state, playing: false");
        }

        // Start with disabled buttons, until native code is initialized
        this.findViewById(R.id.button_play).setEnabled(false);
        this.findViewById(R.id.button_stop).setEnabled(false);

        nativeInit();

        is_playing_desired = true;
        nativePlay();
        handler.post(clockRunnable);
        Intent intent = new Intent(Gstreamer_test2.PAUSE_VOICE);
        sendBroadcast(intent);
    }

    private void showOperatorLayer(){
        ((LinearLayout)findViewById(R.id.video_operator_layer_riseup)).setVisibility(View.VISIBLE);
        PropertyValuesHolder pvhX = PropertyValuesHolder.ofFloat("alpha", 0f,
                0.5f);
        PropertyValuesHolder pvhY = PropertyValuesHolder.ofFloat("Translate", 0,
                80, 0,0);
        ObjectAnimator anim = ObjectAnimator.ofPropertyValuesHolder(findViewById(R.id.video_operator_layer), pvhX, pvhY);
        anim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                operatorLayer.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        anim.setDuration(500).start();


    }
    private void closeOperatorLayer(){
        ((LinearLayout)findViewById(R.id.video_operator_layer_riseup)).setVisibility(View.GONE);
        PropertyValuesHolder pvhX = PropertyValuesHolder.ofFloat("alpha", 0.5f,
                0f);
        PropertyValuesHolder pvhY = PropertyValuesHolder.ofFloat("Translate", 0,
                0, 0,80);
        ObjectAnimator anim = ObjectAnimator.ofPropertyValuesHolder(findViewById(R.id.video_operator_layer), pvhX, pvhY);
        anim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                operatorLayer.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        anim.setDuration(500).start();

    }

    private void initOperatorLayer() {

        operatorLayer = (LinearLayout)findViewById(R.id.video_operator_layer);
        openVoice = (ImageButton) findViewById(R.id.video_operator_open_voice);
        saveScreen = (ImageButton) findViewById(R.id.video_operator_save_screen);
        clockTv = (TextView) findViewById(R.id.video_clock_text);
        videoRise = (ImageView)findViewById(R.id.video_rise);
        videoDrop = (ImageView)findViewById(R.id.video_drop);
        videoRise.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                HttpUtil.sendHttpRequest(HttpUtil.URL, "pi10001 CAMERA RISE", null);
            }
        });
        videoDrop.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                HttpUtil.sendHttpRequest(HttpUtil.URL, "pi10001 CAMERA DROP", null);
            }
        });
        openVoice.setOnTouchListener(this);
        saveScreen.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                final Bitmap bmp = screenshot();
                LayoutInflater inflater = getLayoutInflater();
                View v = inflater.inflate(R.layout.fragment_monitor_add_monitor_dialog
                        , (ViewGroup) findViewById(R.id.add_item_dialog));
                final MaterialEditText monitorId = (MaterialEditText) v.findViewById(R.id.add_item_id);
                final MaterialEditText monitorName = (MaterialEditText) v.findViewById(R.id.add_item_name);
                monitorName.setVisibility(View.GONE);
                monitorId.setHint("输入保存图片名");
                AlertDialog.Builder builder = new AlertDialog.Builder(Gstreamer3.this);
                builder.setTitle("保存图片");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(monitorId.getText().toString().equals(""))
                            ViewBitmapSaver.saveBitmapFromView(bmp,new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date()));
                        else
                            ViewBitmapSaver.saveBitmapFromView(bmp,monitorId.getText().toString().trim());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(Gstreamer3.this, "保存成功：目录 /sdcard/"+monitorId.getText().toString().trim(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
                builder.setCancelable(true);
                builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        onBackPressed();
                    }
                });
                builder.setView(v);
                builder.show();
            }
        });

    }


    private Bitmap screenshot()
    {
        // 获取屏幕
        View dView = getWindow().getDecorView();
        dView.setDrawingCacheEnabled(true);
        dView.buildDrawingCache();
        Bitmap bmp = dView.getDrawingCache();
        return bmp;

    }


    protected void onSaveInstanceState(Bundle outState) {
        Log.d("GStreamer", "Saving state, playing:" + is_playing_desired);
        outState.putBoolean("playing", is_playing_desired);
    }


    @Override
    public void onBackPressed() {
        if(!monitorId.equals("null"))
            ViewBitmapSaver.saveBitmapFromView(screenshot(),monitorId);

        setResult(1, null);

        finish();
    }

    protected void onDestroy() {
        handler.removeCallbacks(clockRunnable);

        //TemporaryData.getInstance().setFlagIsVideo(false);
        nativeFinalize();
        //new Thread(new HttpThread("VIDEO_STOP")).start();
        super.onDestroy();
    }

    // Called from native code. This sets the content of the TextView from the UI thread.
    private void setMessage(final String message) {
        // TODO: 16-9-19 textview
//        final TextView tv = (TextView) this.findViewById(R.id.textview_message);
//        runOnUiThread(new Runnable() {
//            public void run() {
//                tv.setText(message);
//            }
//        });
    }



    // Called from native code. Native code calls this once it has created its pipeline and
    // the main loop is running, so it is ready to accept commands.
    private void onGStreamerInitialized() {
        Log.i("GStreamer", "Gst initialized. Restoring state, playing:" + is_playing_desired);
        // Restore previous playing state
        if (is_playing_desired) {
            nativePlay();
        } else {
            nativePause();
        }

        // Re-enable buttons, now that GStreamer is initialized
        final Activity activity = this;
        runOnUiThread(new Runnable() {
            public void run() {
                //TODO
                activity.findViewById(R.id.button_play).setEnabled(true);
                activity.findViewById(R.id.button_stop).setEnabled(true);
            }
        });
    }

    static {
        System.loadLibrary("gstreamer_android");
        System.loadLibrary("gstreamer3");
        nativeClassInit();
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
        Log.d("GStreamer", "Surface changed to format " + format + " width "
                + width + " height " + height);
        nativeSurfaceInit(holder.getSurface());

    }

    public void surfaceCreated(SurfaceHolder holder) {
        Log.d("GStreamer", "Surface created: " + holder.getSurface());
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d("GStreamer", "Surface destroyed");
        nativeSurfaceFinalize();
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (v.getId() == R.id.surface_video)
        {
            switch (event.getAction()) {

                case MotionEvent.ACTION_DOWN:
                    ax = event.getX();
                    ay = event.getY();
                    break;

                case MotionEvent.ACTION_UP:
                    bx = event.getX();
                    by = event.getY();
                    if ((by - ay) > (bx - ax) && (by - ay) > (ax - bx) && (by - ay) > 50) {
                        // 上
                        HttpUtil.sendHttpRequest(HttpUtil.URL, "pi10001 CAMERA UP", null);
                    }
                    if ((ay - by) > (bx - ax) && (ay - by) > (ax - bx) && (by - ay) < -50) {
                        //下
                        HttpUtil.sendHttpRequest(HttpUtil.URL, "pi10001 CAMERA DOWN", null);
                    }
                    if ((bx - ax) > (ay - by) && (bx - ax) > (by - ay) && (bx - ax) > 50) {
                        //左
                        HttpUtil.sendHttpRequest(HttpUtil.URL, "pi10001 CAMERA LEFT", null);
                    }
                    if ((ax - bx) > (ay - by) && (ax - bx) > (by - ay) && (bx - ax) < -50) {
                        //右
                        HttpUtil.sendHttpRequest(HttpUtil.URL, "pi10001 CAMERA RIGHT", null);
                    }
                    if(((ax-bx)*(ax-bx)+(ay-by)*(ay-by))<4000){   //操作页面显示和隐藏
                        if(operatorLayer.getVisibility()==View.VISIBLE)
                            closeOperatorLayer();
                        else
                            showOperatorLayer();
                    }

                    break;
            }
        }else if(v.getId()==R.id.video_operator_open_voice){
            switch (event.getAction()) {

                case MotionEvent.ACTION_DOWN:
                    Intent intent = new Intent(Gstreamer_test2.OPEN_VOICE);
                    sendBroadcast(intent);
                    break;
                case MotionEvent.ACTION_UP:

                    Intent intent1 = new Intent(Gstreamer_test2.PAUSE_VOICE);
                    sendBroadcast(intent1);
                    break;
            }
        }

        return true;
    }
    Handler handler = new Handler();
    Runnable clockRunnable = new Runnable() {
        @Override
        public void run() {
            clockTv.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            handler.postDelayed(clockRunnable,1000);
        }
    };
}
