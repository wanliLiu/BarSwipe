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
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.barswipe.FloatView.FloatWindowService;

import java.util.ArrayList;
import java.util.List;

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


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
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

            for (int i = 0; info != null && i < info.length; i++) {
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
            holder.studyDesc.setText(info.nonLocalizedLabel);
            holder.icon.setImageResource(info.icon);


            return convertView;
        }


        private class ViewHolder {
            private ImageView icon;
            private TextView studyDesc;

            public ViewHolder(View view) {
                icon = (ImageView) view.findViewById(R.id.icon);
                studyDesc = (TextView) view.findViewById(R.id.studyDesc);
                view.setTag(this);
            }
        }
    }

}