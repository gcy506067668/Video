package cn.uicp.letmesleep.video.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import cn.uicp.letmesleep.video.R;


/**
 * Created by letmesleep on 2016/11/15.
 * QQ:506067668
 */

public class LocalFragment extends BaseFragment{



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_local, container, false);
        ButterKnife.bind(this, v);
        return v;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }


}
