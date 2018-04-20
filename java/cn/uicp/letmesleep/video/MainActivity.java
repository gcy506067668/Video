package cn.uicp.letmesleep.video;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.IdRes;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.afollestad.materialdialogs.MaterialDialog;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.uicp.letmesleep.video.gstreamer.Gstreamer_test2;
import cn.uicp.letmesleep.video.service.MyService;
import cn.uicp.letmesleep.video.ui.SweetAlertDialog;
import cn.uicp.letmesleep.video.ui.fragment.LocalFragment;
import cn.uicp.letmesleep.video.ui.fragment.MonitorFragment;
import cn.uicp.letmesleep.video.ui.fragment.MyFragment;
import cn.uicp.letmesleep.video.util.HttpUtil;
import cn.uicp.letmesleep.video.util.MyApplication;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Bind(R.id.bottomBar)
    BottomBar bottomBar;


    private FragmentManager fragmentManager;
    private MonitorFragment monitorFragment;
    private LocalFragment localFragment;
    private MyFragment myFragment;

    private OpenVideoReceiver receiver;


    private Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        startService(new Intent(this, MyService.class));

        receiver = new OpenVideoReceiver();
        fragmentManager = getFragmentManager();
        FragmentTransaction begin = fragmentManager.beginTransaction();
        monitorFragment = new MonitorFragment();

        begin.add(R.id.main_fragment, monitorFragment);
        begin.commit();
        bottomBar.selectTabAtPosition(0);


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
                FragmentTransaction begin = fragmentManager.beginTransaction();
                switch (tabId) {
                    case R.id.tab_main:
                        toolbar.setTitle("监控");
                        if (monitorFragment == null)
                            monitorFragment = new MonitorFragment();
                        begin.replace(R.id.main_fragment, monitorFragment);
                        begin.commit();
                        break;
                    case R.id.tab_collect:
                        toolbar.setTitle("本地");
                        if (localFragment == null)
                            localFragment = new LocalFragment();
                        begin.replace(R.id.main_fragment, localFragment);
                        begin.commit();
                        break;
                    case R.id.tab_my:
                        toolbar.setTitle("我的");
                        if (myFragment == null)
                            myFragment = new MyFragment();
                        begin.replace(R.id.main_fragment, myFragment);
                        begin.commit();
                        break;
                }
            }
        });




        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        IntentFilter filter = new IntentFilter();
        filter.addAction("OPENVIDEO_FROM_SERIVCE");
        registerReceiver(receiver,filter);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }if(id==R.id.nav_teacher){
            //startActivity(new Intent(MainActivity.this,AboutUsActivity.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }






    @Override
    protected void onDestroy() {
        super.onDestroy();

        unregisterReceiver(receiver);
    }

    public class OpenVideoReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals("OPENVIDEO_FROM_SERIVCE"))
            {
                final SweetAlertDialog dialog = new SweetAlertDialog(MyApplication.getContext());
                Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                long[] pattern = {400, 500, 400, 500};
                vibrator.vibrate(pattern, -1);
                final SweetAlertDialog waring = new SweetAlertDialog(MainActivity.this, SweetAlertDialog.SUCCESS_TYPE);
                //waring.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                waring.setTitleText("有人来了!")
                        .setContentText("是否打开视频？")
                        .setConfirmText("打开视频")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {

                                final MaterialDialog dialog1 = new MaterialDialog.Builder(MyApplication.getContext())
                                        .title("请稍候...")
                                        .content("正在获取IP中...")
                                        .progress(true, 100)
                                        .build();
                                //dialog1.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                                dialog1.show();

                                HttpUtil.sendHttpRequest(HttpUtil.URL, "pi10001 get_ip", new HttpUtil.HttpCallbackListener() {
                                    @Override
                                    public void onFinish(final String response) {
                                        dialog1.dismiss();
                                        Intent videoIntent = new Intent(getApplicationContext(), Gstreamer_test2.class);
                                        videoIntent.putExtra(Gstreamer_test2.REMOTE_VIDEO_IP, response);
                                        videoIntent.putExtra("monitorId","pi10001");
                                        //videoIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(videoIntent);
                                    }
                                    @Override
                                    public void onError(final Exception e) {
                                        dialog1.dismiss();}
                                });
                                waring.dismiss();

                            }
                        })
                        .showCancelButton(true)
                        .setCancelText("忽略")
                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                waring.dismiss();
                            }
                        })
                        .show();
            }
        }
    }


}
