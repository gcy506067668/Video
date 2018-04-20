package cn.uicp.letmesleep.video.ui.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.uicp.letmesleep.video.R;
import cn.uicp.letmesleep.video.bean.Monitor;
import cn.uicp.letmesleep.video.gstreamer.Gstreamer_test2;
import cn.uicp.letmesleep.video.ui.SweetAlertDialog;
import cn.uicp.letmesleep.video.ui.adapter.MonitorAdapter;
import cn.uicp.letmesleep.video.util.HttpUtil;
import cn.uicp.letmesleep.video.util.MyApplication;
import cn.uicp.letmesleep.video.util.PreferenceUtil;


/**
 * Created by letmesleep on 2016/11/15.
 * QQ:506067668
 */

public class MonitorFragment extends BaseFragment {


    @Bind(R.id.monitor_list)
    ListView monitorList;
    @Bind(R.id.fragment_additem)
    FloatingActionButton fragmentAdditem;
    private List<Monitor> list;
    private MonitorAdapter adapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_monitor, container, false);
        ButterKnife.bind(this, v);

        initList();
        return v;
    }

    private void initList() {
        list = new ArrayList<>();
        String monitorId = PreferenceUtil.load(MyApplication.getContext(),"monitorId","null");
        if(!monitorId.equals("null")){
            Monitor m = new Monitor();
            m.setId(monitorId);
            m.setName(PreferenceUtil.load(MyApplication.getContext(),"monitorName","null"));
            m.setDate(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date()));
            list.add(m);
        }
        adapter = new MonitorAdapter(getActivity(), list);
        monitorList.setAdapter(adapter);
        monitorList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                final MaterialDialog dialog1 = new MaterialDialog.Builder(getActivity())
                        .title("连接中...")
                        .content("正在获取IP中...")
                        .progress(true, 100)
                        .show();
                HttpUtil.sendHttpRequest(HttpUtil.URL, list.get(position).getId()+" get_ip", new HttpUtil.HttpCallbackListener() {
                    @Override
                    public void onFinish(final String response) {
                        dialog1.dismiss();
                        Intent videoIntent = new Intent(getActivity(), Gstreamer_test2.class);
                        videoIntent.putExtra(Gstreamer_test2.REMOTE_VIDEO_IP, response);
                        videoIntent.putExtra("monitorId", list.get(position).getId());
                        videoIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        getActivity().startActivity(videoIntent);
                    }

                    @Override
                    public void onError(final Exception e) {
                        dialog1.dismiss();
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getActivity(), "网络异常！", Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                });

            }
        });

        monitorList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, final View view, final int position, long id) {
                final SweetAlertDialog dialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE);
                dialog.setTitleText("移除设备")
                        .setContentText("是否移除监控" + list.get(position).getName() + "?")
                        .setConfirmText("移除")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                final Monitor m = list.get(position);
                                list.remove(position);
                                adapter.notifyDataSetChanged();
                                dialog.dismiss();
                                PreferenceUtil.clearAll(MyApplication.getContext());
                                Snackbar.make(view, "撤销移除", Snackbar.LENGTH_LONG).setAction("撤销", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        list.add(m);
                                        PreferenceUtil.save(MyApplication.getContext(),"monitorName",m.getName());
                                        PreferenceUtil.save(MyApplication.getContext(),"monitorId",m.getId());
                                        adapter.notifyDataSetChanged();
                                    }
                                }).show();

                            }
                        })
                        .showCancelButton(true)
                        .setCancelText("取消")
                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                dialog.dismiss();
                            }
                        })
                        .show();
                return true;
            }
        });
        fragmentAdditem.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.fragment_monitor_add_monitor_dialog
                , (ViewGroup) getActivity().findViewById(R.id.add_item_dialog));
        final MaterialEditText monitorId = (MaterialEditText) v.findViewById(R.id.add_item_id);
        final MaterialEditText monitorName = (MaterialEditText) v.findViewById(R.id.add_item_name);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("添加设备");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Monitor m = new Monitor();
                m.setId(monitorId.getText().toString().trim());
                m.setName(monitorName.getText().toString());
                m.setDate(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date()));
                PreferenceUtil.save(MyApplication.getContext(),"monitorName",m.getName());
                PreferenceUtil.save(MyApplication.getContext(),"monitorId",m.getId());
                list.add(m);
                adapter.notifyDataSetChanged();
            }
        });
        builder.setCancelable(true);
        builder.setView(v);
        builder.show();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);

    }


}
