package com.barswipe;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.barswipe.flowlayout.CategoryActivity;
import com.barswipe.flowlayout.view.FlowLayout;
import com.barswipe.flowlayout.view.TagAdapter;
import com.barswipe.flowlayout.view.TagFlowLayout;
import com.barswipe.volume.pcm.pcm2amr.ToastUtil;
import com.barswipe.widget.AutoWrapAdapter;
import com.barswipe.widget.AutoWrapListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

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

    private List<String> data;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_auto_wrap);
        ButterKnife.bind(this);

        data = getData();

        list1.setMaxNumRows(1);
        list1.setAdapter(new AutoWrapTestAdapter(this, data));
        list1.setOnItemClickListener(new clickListener());

        list2.setAdapter(new AutoWrapTestAdapter(this, data));
        list2.setOnItemClickListener(new clickListener());

        id_flowlayout.setMaxRows(3);
        id_flowlayout.setAdapter(new TagAdapter<String>(data) {
            @Override
            public View getView(FlowLayout parent, int position, String o) {
                TextView view = (TextView) getLayoutInflater().inflate(R.layout.tv, null);
                view.setText(o);
                return view;
            }
        });

        findViewById(R.id.clike).setOnClickListener(view -> Toast.makeText(ActivityAutoWrapList.this, "ActivityAutoWrapList", Toast.LENGTH_LONG).show());
    }


    @OnClick({R.id.thrid})
    public void onClick(View view) {
        startActivity(new Intent(this, CategoryActivity.class));
    }

    private List<String> getData() {
        List<String> data = new ArrayList<>();
//        data.add("一代记忆设备，由于它体积小、数据传输速度助理（外语缩助理（外语缩快");
        data.add("SD存储卡");
        data.add("可热插拔");
        data.add("等优良的");
        data.add("特性");

        data.add("一代记忆设备，由于它体积小、数据传输速度助理（外语缩助理（外语缩快");
        data.add("，例如数码相机、个人数码助理（外语缩写P助理（外语缩助理（外语缩DA）和");
        data.add("SD存储卡");
        data.add("是一种基助理（外语缩助理（外语缩于半导体快闪记忆器的新");

        data.add("可热插拔");
        data.add("等优良的");
        data.add("特性");
        data.add("它被广泛地于便携式装置上使用");

        data.add("多媒体播放器等");
        data.add("Secure Digital Memory Card/SD card");

        data.add("可热插拔");
        data.add("等优良的");
        data.add("特性");
        data.add("可热插拔");
        data.add("等优良的");
        data.add("特性");
        data.add("可热插拔");
        data.add("等优良的");
        data.add("特性");


        return data;
    }

    private class clickListener implements AdapterView.OnItemClickListener {


        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            ToastUtil.showShort(ActivityAutoWrapList.this, data.get(i));
        }
    }

    private class AutoWrapTestAdapter extends AutoWrapAdapter<String> {


        public AutoWrapTestAdapter(Context context, List<String> list) {
            super(context, list);
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView view = (TextView) mInflater.inflate(R.layout.tv, null);
            view.setText(getItem(position).toString());

            return view;
        }
    }


}
