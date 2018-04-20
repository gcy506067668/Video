package cn.uicp.letmesleep.video.gstreamer;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.freedesktop.gstreamer.GStreamer;

import cn.uicp.letmesleep.video.R;
import cn.uicp.letmesleep.video.util.MyApplication;


public class Gstreamer_test2 extends Activity {
    public static String REMOTE_VIDEO_IP = "remoteip";

    private native void send_nativeInit();     // Initialize native code, build pipeline, etc
    private native void send_nativeFinalize(); // Destroy pipeline and shutdown native code
    private native void send_nativePlay();     // Set pipeline to PLAYING
    private native void send_nativePause();    // Set pipeline to PAUSED
    private static native boolean send_nativeClassInit(); // Initialize native class: cache Method IDs for callbacks
    private long native_custom_data;      // Native code will use this to keep private data
    private int ip1,ip2,ip3,ip4;

    private boolean is_playing_desired;   // Whether the user asked to go to PLAYING

    private boolean isOnLine = true;

    public static String OPEN_VOICE = "openvoice";
    public static String PAUSE_VOICE = "pausevoice";

    private VoiceReceiver receiver;
    private IntentFilter mFilter;
    // Called when the activity is first created.
    @Override
    public void onCreate(Bundle savedInstanceState)
    {


        super.onCreate(savedInstanceState);

        // Initialize GStreamer and warn if it fails

        try {
            GStreamer.init(this);
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            finish(); 
            return;
        }

        setContentView(R.layout.main_ttt);


        MyApplication.map.put(MyApplication.VIDEO_STATUE,true);

        String remoteIP = getIntent().getExtras().getString(REMOTE_VIDEO_IP,"192.168.1.1");
        String monitorId = getIntent().getExtras().getString("monitorId","null");
        if(remoteIP!=null){
            String str[] = remoteIP.split("\\.");

            if(str.length==4){
                ip1 = Integer.parseInt(str[0]);
                ip2 = Integer.parseInt(str[1]);
                ip3 = Integer.parseInt(str[2]);
                ip4 = Integer.parseInt(str[3]);
                Log.e("IP地址：",ip1+"."+ip2+"."+ip3+"."+ip4);
            }



        }else{//设备离线！
            isOnLine = false;
        }
        ImageButton play = (ImageButton) this.findViewById(R.id.button_play);
        play.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                is_playing_desired = true;
                send_nativePlay();
            }
        });

        ImageButton pause = (ImageButton) this.findViewById(R.id.button_stop);
        pause.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                is_playing_desired = false;
                send_nativePause();
            }
        });

        if (savedInstanceState != null) {
            is_playing_desired = savedInstanceState.getBoolean("playing");
            Log.i ("GStreamer", "Activity created. Saved state is playing:" + is_playing_desired);
        } else {
            is_playing_desired = false;
            Log.i ("GStreamer", "Activity created. There is no saved state, playing: false");
        }

        // Start with disabled buttons, until native code is initialized
        this.findViewById(R.id.button_play).setEnabled(false);
        this.findViewById(R.id.button_stop).setEnabled(false);

        send_nativeInit();

        is_playing_desired = true;
        send_nativePlay();

        Intent intent = new Intent(Gstreamer_test2.this,Gstreamer3.class);
        intent.putExtra("monitorId",monitorId);
        startActivityForResult(intent,1);


        mFilter = new IntentFilter();
        mFilter.addAction(OPEN_VOICE);
        mFilter.addAction(PAUSE_VOICE);
        receiver = new VoiceReceiver();
        registerReceiver(receiver,mFilter);

    }

    protected void onSaveInstanceState (Bundle outState) {
        Log.d ("GStreamer", "Saving state, playing:" + is_playing_desired);
        outState.putBoolean("playing", is_playing_desired);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode==1){
            finish();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    protected void onDestroy() {
        send_nativeFinalize();
        super.onDestroy();
        MyApplication.map.put(MyApplication.VIDEO_STATUE,false);
        unregisterReceiver(receiver);

    }

    // Called from native code. This sets the content of the TextView from the UI thread.
    private void setMessage(final String message) {
        final TextView tv = (TextView) this.findViewById(R.id.textview_message);
        runOnUiThread (new Runnable() {
          public void run() {
            tv.setText(message);
          }
        });
    }


    // Called from native code. Native code calls this once it has created its pipeline and
    // the main loop is running, so it is ready to accept commands.
    private void onGStreamerInitialized () {
        Log.i ("GStreamer", "Gst initialized. Restoring state, playing:" + is_playing_desired);
        // Restore previous playing state
        if (is_playing_desired) {
            send_nativePlay();
        } else {
            send_nativePause();
        }

        // Re-enable buttons, now that GStreamer is initialized
        final Activity activity = this;
        runOnUiThread(new Runnable() {
            public void run() {
                activity.findViewById(R.id.button_play).setEnabled(true);
                activity.findViewById(R.id.button_stop).setEnabled(true);
            }
        });
    }

    static {

        System.loadLibrary("gstreamer_android");
        System.loadLibrary("sendsound");
        send_nativeClassInit();
    }

    public class VoiceReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()){
                case "openvoice":

                    is_playing_desired = true;
                    send_nativePlay();
                    break;
                case "pausevoice":

                    is_playing_desired = false;
                    send_nativePause();
                    break;
            }
        }
    }
}
