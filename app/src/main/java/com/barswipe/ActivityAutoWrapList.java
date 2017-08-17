package com.barswipe;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.barswipe.widget.AutoWrapAdapter;
import com.barswipe.widget.AutoWrapListView;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by soli on 17/08/2017.
 */

public class ActivityAutoWrapList extends BaseActivity {

    @BindView(R.id.list1)
    AutoWrapListView list1;

    @BindView(R.id.list2)
    AutoWrapListView list2;

    @BindView(R.id.id_flowlayout)
    TagFlowLayout id_flowlayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_auto_wrap);
        ButterKnife.bind(this);

        list1.setMaxNumRows(3);
        list1.setAdapter(new AutoWrapTestAdapter(this, getData()));

        list2.setAdapter(new AutoWrapTestAdapter(this, getData()));

        id_flowlayout.setAdapter(new TagAdapter<String>(getData()) {
            @Override
            public View getView(FlowLayout parent, int position, String o) {
                TextView view = (TextView) getLayoutInflater().inflate(R.layout.item_auto_wrap, null);
                view.setText(o);
                return view;
            }
        });
    }


    private List<String> getData() {
        List<String> data = new ArrayList<>();
        data.add("SD存储卡");

        data.add("是一种基助理（外语缩助理（外语缩于半导体快闪记忆器的新");
        data.add("一代记忆设备，由于它体积小、数据传输速度助理（外语缩助理（外语缩快");
        data.add("可热插拔");
//        data.add("等优良的");
//        data.add("特性");
//        data.add("它被广泛地于便携式装置上使用");
//        data.add("，例如数码相机、个人数码助理（外语缩写P助理（外语缩助理（外语缩DA）和");
//        data.add("多媒体播放器等");
//        data.add("Secure Digital Memory Card/SD card");


        return data;
    }

    private class AutoWrapTestAdapter extends AutoWrapAdapter<String> {


        public AutoWrapTestAdapter(Context context, List<String> list) {
            super(context, list);
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView view = (TextView) mInflater.inflate(R.layout.item_auto_wrap, null);
            view.setText(getItem(position).toString());

            return view;
        }
    }


}
