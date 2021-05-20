package com.barswipe.flowlayout;

import android.os.Bundle;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.barswipe.R;
import com.barswipe.flowlayout.view.FlowLayout;
import com.barswipe.flowlayout.view.TagAdapter;
import com.barswipe.flowlayout.view.TagFlowLayout;

import java.util.ArrayList;
import java.util.List;

public class CategoryActivity extends AppCompatActivity {

    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    private String[] mTabTitles = new String[]
            {"Muli Selected", "Limit 3",
                    "Event Test", "ScrollView Test", "Single Choose", "Gravity", "ListView Sample"};

    private TagFlowLayout tagLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        mTabLayout = (TabLayout) findViewById(R.id.id_tablayout);
        mViewPager = (ViewPager) findViewById(R.id.id_viewpager);

        tagLayout = (TagFlowLayout) findViewById(R.id.tagLayout);

        tagLayout.setAdapter(new TagAdapter<String>(getData()) {
            @Override
            public View getView(FlowLayout parent, int position, String s) {
                TextView tv = (TextView) getLayoutInflater().inflate(R.layout.tv, tagLayout, false);
                tv.setText(s);
                return tv;
            }
        });

        mViewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int i) {
                switch (i) {
                    case 0:
                        return new SimpleFragment();
                    case 1:
                        return new LimitSelectedFragment();
                    case 2:
                        return new EventTestFragment();
                    case 3:
                        return new ScrollViewTestFragment();
                    case 4:
                        return new SingleChooseFragment();
                    case 5:
                        return new GravityFragment();
                    case 6:
                        return new ListViewTestFragment();
                    default:
                        return new EventTestFragment();
                }
            }

            @Override
            public CharSequence getPageTitle(int position) {

                return mTabTitles[position];
            }

            @Override
            public int getCount() {
                return mTabTitles.length;
            }
        });


        mTabLayout.setupWithViewPager(mViewPager);


        findViewById(R.id.clike).setOnClickListener(view -> Toast.makeText(CategoryActivity.this, "CategoryActivity", Toast.LENGTH_LONG).show());
    }

    private List<String> getData() {
        List<String> data = new ArrayList<>();
        data.add("一代记忆设备，由于它体积小、数据传输速度助理（外语缩助理（外积小、数据传输速度助理（外语缩助理（外积小、数据传输速度助理（外语缩助理（外语缩快");
        data.add("SD存储卡");
        data.add("可热插拔");
        data.add("等优良的");
        data.add("特性");
        data.add("是一种基助理（外语缩助理（外语缩于半导体快闪记忆器的新");
        data.add("它被广泛地于便携式装置上使用");
        data.add("，例如数码相机、个人数码助理（外语缩写P助理（外语缩助理（外语缩DA）和");
        data.add("多媒体播放器等");
        data.add("Secure Digital Memory Card/SD card");


        return data;
    }

}
