package cn.uicp.letmesleep.video.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cn.uicp.letmesleep.video.R;


/**
 * Created by letmesleep on 2016/11/15.
 * QQ:506067668
 */

public class MyFragment extends BaseFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_my,container,false);
        return v;
    }
}
