package com.barswipe;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.barswipe.ExpandableTextView.ExpandableTextView;
import com.barswipe.FloatView.FloatWindowService;
import com.jakewharton.rxbinding.widget.RxAdapterView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.functions.Action1;

/**
 * Created by soli on 6/2/16.
 */
public class LaunchActivity extends AppCompatActivity {

    private ListView listView;

    private activityListAdapter adapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        Intent intent = new Intent(this, FloatWindowService.class);
        startService(intent);

        listView = (ListView) findViewById(R.id.lis);


        adapter = new activityListAdapter();


//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                ActivityInfo info = adapter.getItem(position);
//                Intent intent = new Intent();
//                intent.setClassName(getPackageName(), info.name);
//                startActivity(intent);
//            }
//        });

        RxAdapterView.itemClicks(listView)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer position) {
                        ActivityInfo info = adapter.getItem(position);
                        Intent intent = new Intent();
                        intent.setClassName(getPackageName(), info.name);
                        startActivity(intent);
                    }
                });

        adapter.setList(getAllActivityLists());
        listView.setAdapter(adapter);
    }

    /**
     * @return
     */
    private List<ActivityInfo> getAllActivityLists() {
        List<ActivityInfo> list = new ArrayList<>();

        try {

            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_ACTIVITIES);

            ActivityInfo[] info = packageInfo.activities;

            for (int i = info.length - 1; info != null && i >= 0; i--) {
                if (!TextUtils.isEmpty(info[i].nonLocalizedLabel)) {
                    list.add(info[i]);
                }
            }


        } catch (Exception e) {

        }

        return list;
    }

    /**
     *
     */
    private class activityListAdapter extends BaseAdapter {

        private List<ActivityInfo> list;
        private final SparseBooleanArray mCollapsedStatus;

        public activityListAdapter() {
            mCollapsedStatus = new SparseBooleanArray();

        }

        public void setList(List<ActivityInfo> list) {
            this.list = list;
        }

        @Override
        public int getCount() {
            return list != null ? list.size() : 0;
        }

        @Override
        public ActivityInfo getItem(int position) {
            return list != null ? list.get(position) : null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder = null;
            if (convertView == null) {
                convertView = View.inflate(LaunchActivity.this, R.layout.activity_list_item, null);
                holder = new ViewHolder(convertView);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            ActivityInfo info = getItem(position);
            holder.icon.setImageResource(info.icon);
            holder.expand_text_view.setText(info.nonLocalizedLabel + "\n" + getResources().getString(info.descriptionRes), mCollapsedStatus, position);

            return convertView;
        }


        private class ViewHolder {
            private ImageView icon;
            private ExpandableTextView expand_text_view;

            public ViewHolder(View view) {
                icon = (ImageView) view.findViewById(R.id.icon);
                expand_text_view = (ExpandableTextView) view.findViewById(R.id.expand_text_view);
                view.setTag(this);
            }
        }
    }

}
