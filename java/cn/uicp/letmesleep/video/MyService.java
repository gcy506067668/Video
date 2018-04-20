package cn.uicp.letmesleep.video.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import java.util.Map;

import cn.uicp.letmesleep.video.util.CustomUtil;
import cn.uicp.letmesleep.video.util.HttpUtil;
import cn.uicp.letmesleep.video.util.MyApplication;

public class MyService extends Service {


    private static final String ACTION_PLAY = "letmesleep.uicp.cn.open";
    private static final int REQUEST_CODE = 100;
    private static final int OPEN_VIDEO = 1;
    private String remoteIp = "";
    private boolean flag = true;
    private String MSG = "";
    //private String deviceId ;

    //private MBroadcastReceive mBroadcastReceive;

    public MyService() {
        //deviceId = PreferenceUtil.load(MyApplication.getContext(),"monitorId","null");
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    Runnable runnableIP = new Runnable() {
        @Override
        public void run() {
//            if(deviceId.equals("null"))
//                return;
            while(flag){
                HttpUtil.sendHttpRequest(HttpUtil.URL, "pi10001 ip "+ CustomUtil.getLocalIPWhenWifi(MyApplication.getContext()), null);
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                HttpUtil.sendHttpRequest(HttpUtil.URL, "pi10001 get_message ", new HttpUtil.HttpCallbackListener() {
                    @Override
                    public void onFinish(String response)
                    {
                        Map<String,Boolean> map = MyApplication.map;
                        /*****
                         * ***/

                        if(!response.equals("OK"))      //有人访问仅弹出一次alertdialog
                        {

                            if(map.get(MyApplication.ALERT_STATUE)||map.get(MyApplication.VIDEO_STATUE)){
                                return;
                            }
                            else{
                                map.put(MyApplication.ALERT_STATUE,true);
                            }
                            Message msg = new Message();
                            msg.what = OPEN_VIDEO;
                            handler.sendMessage(msg);

                        }else{                            //无人访问
                            map.put(MyApplication.ALERT_STATUE,false);
                        }
                    }

                    @Override
                    public void onError(Exception e) {
                        MyApplication.map.put(MyApplication.ALERT_STATUE,false);

                    }
                });
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }
    };




    @Override
    public void onCreate() {
        Thread t = new Thread(runnableIP);
        t.start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        flag = true;
        return super.onStartCommand(intent, flags, startId);
    }

//    public class MBroadcastReceive extends BroadcastReceiver {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            switch(intent.getAction()){
//                case ACTION_PLAY:
//                    Message msg = new Message();
//                    msg.what = OPEN_VIDEO;
//                    handler.sendMessage(msg);
//                    break;
//
//                default:
//                    break;
//            }
//        }
//    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case OPEN_VIDEO:

                    Intent intent = new Intent();
                    intent.setAction("OPENVIDEO_FROM_SERIVCE");
                    sendBroadcast(intent);
//                    final SweetAlertDialog dialog = new SweetAlertDialog(MyApplication.getContext());
//                    Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
//                    long[] pattern = {400, 500, 400, 500};
//                    vibrator.vibrate(pattern, -1);
//                    final SweetAlertDialog waring = new SweetAlertDialog(getApplicationContext(), SweetAlertDialog.SUCCESS_TYPE);
//                    waring.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
//                    waring.setTitleText("有人来了!")
//                            .setContentText(MSG)
//                            .setConfirmText("打开视频")
//                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
//                                @Override
//                                public void onClick(SweetAlertDialog sDialog) {
//
//                                    final MaterialDialog dialog1 = new MaterialDialog.Builder(MyApplication.getContext())
//                                            .title("请稍候...")
//                                            .content("正在获取IP中...")
//                                            .progress(true, 100)
//                                            .build();
//                                    dialog1.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
//                                    dialog1.show();
//
//                                    HttpUtil.sendHttpRequest(HttpUtil.URL, "pi10001 get_ip", new HttpUtil.HttpCallbackListener() {
//                                        @Override
//                                        public void onFinish(final String response) {
//                                            dialog1.dismiss();
//                                            Intent videoIntent = new Intent(getApplicationContext(), Gstreamer_test2.class);
//                                            videoIntent.putExtra(Gstreamer_test2.REMOTE_VIDEO_IP, response);
//                                            videoIntent.putExtra("monitorId","pi10001");
//                                            videoIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                            startActivity(videoIntent);
//                                        }
//                                        @Override
//                                        public void onError(final Exception e) {
//                                            dialog1.dismiss();}
//                                    });
//                                    waring.dismiss();
//
//                                }
//                            })
//                            .showCancelButton(true)
//                            .setCancelText("忽略")
//                            .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
//                                @Override
//                                public void onClick(SweetAlertDialog sweetAlertDialog) {
//                                    waring.dismiss();
//                                }
//                            })
//                            .show();

                break;
            }

        }
    };

    @Override
    public void onDestroy() {
        flag = false;
    }
}
