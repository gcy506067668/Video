package cn.uicp.letmesleep.video.ui.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cn.uicp.letmesleep.video.R;
import cn.uicp.letmesleep.video.bean.Monitor;
import cn.uicp.letmesleep.video.util.ViewBitmapSaver;


/**
 * Created by letmesleep on 2016/11/5.
 * QQ:506067668
 */

public class MonitorAdapter extends BaseAdapter {

    private Context mContext;
    private List<Monitor> mDataList;
    private LayoutInflater mInflater;


    public MonitorAdapter(Context context, List<Monitor> mDataList){
        mContext = context;
        mInflater = LayoutInflater.from(context);
        this.mDataList = mDataList;
    }


    @Override
    public int getCount() {
        return mDataList.size();
    }

    @Override
    public Object getItem(int i) {
        return mDataList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final ViewHolder viewHolder;
        if (view == null) {
            viewHolder = new ViewHolder();
            view = mInflater.inflate(R.layout.adapter_monitor_item, null);

            viewHolder.monitorName = (TextView) view.findViewById(R.id.monitor_name);
            viewHolder.monitortime = (TextView) view.findViewById(R.id.monitor_time);
            viewHolder.monitorImage = (ImageView) view.findViewById(R.id.monitor_image);

            view.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.monitortime.setText(mDataList.get(i).getDate());
        viewHolder.monitorName.setText(mDataList.get(i).getName());
        viewHolder.monitorImage.setImageResource(R.mipmap.testbackage);
        Bitmap bm = ViewBitmapSaver.loadBitmapByName(mDataList.get(i).getId());
//        if(bm!=null) {
//            viewHolder.monitorImage.setImageBitmap(bm);
//        }else{
//            viewHolder.monitorImage.setImageResource(R.mipmap.testbackage);
//        }
        return view;
    }
    class ViewHolder {

        TextView monitorName;
        TextView monitortime;
        ImageView monitorImage;

    }


}
