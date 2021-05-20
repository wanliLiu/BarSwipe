package me.kaede.frescosample.samplelist;

import android.os.Bundle;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.widget.Toolbar;

import com.barswipe.BaseActivity;
import com.barswipe.R;

public class SampleListActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recyclerview);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Freso demo");

        TabLayout tabLayout = (TabLayout) this.findViewById(R.id.tablayout);
        ViewPager viewPager = (ViewPager) this.findViewById(R.id.viewpager);
        viewPager.setAdapter(new MyAdapter(getSupportFragmentManager()));
        viewPager.setOffscreenPageLimit(1);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
    }

    public class MyAdapter extends FragmentStatePagerAdapter {
        public String[] pagers = new String[]{"视图", "自定义视图", "代码窥视"};

        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            position %= pagers.length;
            if (position == 2) return SnippetFragment.newInstance();
            return SampleListFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            return pagers.length * 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return pagers[position % pagers.length];
        }
    }

}
