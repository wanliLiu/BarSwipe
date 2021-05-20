package com.barswipe.fram;

import android.graphics.Color;
import android.os.Bundle;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.widget.Toolbar;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import com.barswipe.BaseActivity;
import com.barswipe.R;
import com.jakewharton.rxbinding2.view.RxView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * AppBarLayout CollapsingToolbarLayout 的进一步使用
 * http://blog.csdn.net/litengit/article/details/52958721
 */
public class NewScrollingActivity extends BaseActivity {

    private String[] title = new String[]{"演出", "相册", "详情"};

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tablayout)
    TabLayout tablayout;
    @BindView(R.id.viewpage)
    ViewPager viewpage;

    @BindView(R.id.app_bar)
    AppBarLayout app_bar;
//    @BindView(R.id.toolbTitle)
//    TextView toolbTitle;

    @BindView(R.id.tesdkcon)
    View tesdkcon;

    private boolean isExpand = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling_new);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
//        setTitle("");

        viewpage.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return TestFramgnt.newInstance(position);
            }

            @Override
            public int getCount() {
                return title.length;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return title[position];
            }
        });
        viewpage.setOffscreenPageLimit(3);
//        toolbTitle.setText("小酒馆音乐空间");
//        final View group = findViewById(R.id.toolbarContent);
//        app_bar.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
//            int scrollRangle = appBarLayout.getTotalScrollRange();
//            //初始verticalOffset为0，不能参与计算。
//            Log.e("位置", scrollRangle + "  ----------  " + verticalOffset);
//            if (verticalOffset == 0) {
////                toolbTitle.setAlpha(0.0f);
//                group.setBackgroundColor(getColorWithAlpha(0.0f, this.getResources().getColor(R.color.actionbar_color)));
//            } else {
//                //保留一位小数
//                double alpha = Math.abs(verticalOffset) * 1.0 / scrollRangle * 1.0;
////                toolbTitle.setAlpha(alpha);
//                group.setBackgroundColor(getColorWithAlpha(alpha, this.getResources().getColor(R.color.actionbar_color)));
//            }
//        });


        initTag();
        viewpage.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                tablayout.getTabAt(position).select();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        tablayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewpage.setCurrentItem(tab.getPosition());
                TextView textView = tab.getCustomView().findViewById(R.id.text);
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                textView.setTextColor(Color.parseColor("#FF4081"));
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                TextView textView = tab.getCustomView().findViewById(R.id.text);
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                textView.setTextColor(Color.parseColor("#96333333"));
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        RxView.clicks(app_bar)
                .subscribe(te -> app_bar.setExpanded(isExpand = !isExpand));
    }

    private void initTag() {
        for (int i = 0; i < title.length; i++) {
            tablayout.addTab(getTag(i, i == 0 ? true : false));
        }
    }

    private TabLayout.Tab getTag(int post, boolean iselect) {
        TabLayout.Tab tab = tablayout.newTab();
        View view = getLayoutInflater().inflate(R.layout.custom_title, null);
        TextView textView = view.findViewById(R.id.text);
        textView.setText(title[post]);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, iselect ? 18 : 14);
        textView.setTextColor(Color.parseColor(iselect ? "#FF4081" : "#96333333"));
        tab.setCustomView(view);
        return tab;
    }
}
