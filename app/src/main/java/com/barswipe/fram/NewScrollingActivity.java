package com.barswipe.fram;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.barswipe.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * AppBarLayout CollapsingToolbarLayout 的进一步使用
 * http://blog.csdn.net/litengit/article/details/52958721
 */
public class NewScrollingActivity extends AppCompatActivity {

    private String[] title = new String[]{"演出", "相册", "详情"};

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tablayout)
    TabLayout tablayout;
    @BindView(R.id.viewpage)
    ViewPager viewpage;

    @BindView(R.id.app_bar)
    AppBarLayout app_bar;
    @BindView(R.id.toolbTitle)
    TextView toolbTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling_new);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        setTitle("");

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

        tablayout.setupWithViewPager(viewpage);

        toolbTitle.setText("小酒馆音乐空间");
        app_bar.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            int scrollRangle = appBarLayout.getTotalScrollRange();
            //初始verticalOffset为0，不能参与计算。
            if (verticalOffset == 0) {
                toolbTitle.setAlpha(0.0f);
            } else {
                //保留一位小数
                float alpha = Math.abs(Math.round(1.0f * verticalOffset / scrollRangle) * 10) / 10;
                toolbTitle.setAlpha(alpha);
            }
        });
    }
}
