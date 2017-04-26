package com.barswipe.draweePhotoView.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;

import com.barswipe.R;
import com.barswipe.draweePhotoView.activity.BaseActivity;
import com.barswipe.draweePhotoView.activity.MyPhotoViewActivity;
import com.facebook.drawee.view.SimpleDraweeView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * ========================================================== <br>
 * <b>版权</b>：　　　别志华 版权所有(c) 2015 <br>
 * <b>作者</b>：　　　别志华 biezhihua@163.com<br>
 * <b>创建日期</b>：　15-9-26 <br>
 * <b>描述</b>：　　　<br>
 * <b>版本</b>：　   V1.0 <br>
 * <b>修订历史</b>：　<br>
 * ========================================================== <br>
 */
public class TwoFragment extends BaseFragment {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.fab)
    FloatingActionButton fab;
    @Bind(R.id.draweeView)
    SimpleDraweeView draweeView;

    public static TwoFragment newInstance() {
        TwoFragment fragment = new TwoFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public int inflateContentView() {
        return R.layout.bzh_ui_two_fragment;
    }

    @Override
    public void layoutInit(LayoutInflater inflater, Bundle savedInstanceState) {
        super.layoutInit(inflater, savedInstanceState);
        ButterKnife.bind(this, getRootView());
        initToolbar();

        if (savedInstanceState == null) {
            draweeView.setAspectRatio(1F);
            draweeView.setImageURI(Uri.parse("http://img5.duitang.com/uploads/item/201511/04/20151104214718_FfnST.jpeg"));
        }
    }

    @OnClick(R.id.draweeView)
    public void onClickDraweeView(View view) {
        startActivity(new Intent(getContext(), MyPhotoViewActivity.class));
    }

    private void initToolbar() {
        if (toolbar != null && getActivity() != null && getActivity() instanceof BaseActivity) {
            final BaseActivity activity = (BaseActivity) getActivity();
            activity.setSupportActionBar(toolbar);
            activity.getSupportActionBar().setDisplayShowHomeEnabled(false);
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setTitle("测试Fresco的aspect");
            toolbar.setNavigationIcon(R.mipmap.ic_arrow_back_white_24dp);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    activity.finish();
                }
            });
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(getRootView());
    }
}
