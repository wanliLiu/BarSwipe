package me.kaede.frescosample.rec;

import android.os.Bundle;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.widget.Toolbar;

import com.barswipe.BaseActivity;
import com.barswipe.R;

public class RecyclerViewActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recyclerview);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TabLayout tabLayout = (TabLayout) this.findViewById(R.id.tablayout);
        ViewPager viewPager = (ViewPager) this.findViewById(R.id.viewpager);
        viewPager.setAdapter(new MyAdapter(getSupportFragmentManager()));
        viewPager.setOffscreenPageLimit(3);
        tabLayout.setupWithViewPager(viewPager);
    }

    public class MyAdapter extends FragmentStatePagerAdapter {
        public String[] pagers = new String[]{"ヒトツ", "フタツ", "ミツ"};

        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return RecyclerViewFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            return pagers.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return pagers[position];
        }
    }
}
