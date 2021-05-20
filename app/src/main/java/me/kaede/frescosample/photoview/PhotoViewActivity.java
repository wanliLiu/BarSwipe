package me.kaede.frescosample.photoview;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import com.barswipe.BaseActivity;
import com.barswipe.R;
import com.barswipe.draweePhotoView.MyPhotoView;

import java.util.List;

import me.kaede.frescosample.ImageApi;
import me.kaede.util.HackyViewPager;

public class PhotoViewActivity extends BaseActivity {

    private static final String ISLOCKED_ARG = "isLocked";
    private ViewPager mViewPager;
    private int position;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_view);
        mViewPager = (HackyViewPager) findViewById(R.id.viewpager);

        mViewPager.setAdapter(new SamplePagerAdapter());
        if (getIntent() != null) {
            position = getIntent().getIntExtra("position", 0);
            mViewPager.setCurrentItem(position);
        }

        if (savedInstanceState != null) {
            boolean isLocked = savedInstanceState.getBoolean(ISLOCKED_ARG, false);
            ((HackyViewPager) mViewPager).setLocked(isLocked);
        }

    }

    static class SamplePagerAdapter extends PagerAdapter {

        List<String> datas = ImageApi.jk.getUrls();

        @Override
        public int getCount() {
            return datas.size();
        }

        @Override
        public View instantiateItem(ViewGroup container, int position) {
            MyPhotoView photoView = new MyPhotoView(container.getContext());
            photoView.setImageUri(datas.get(position), null);

            // Now just add PhotoView to ViewPager and return it
            container.addView(photoView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

            return photoView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

    }

    private boolean isViewPagerActive() {
        return (mViewPager != null && mViewPager instanceof HackyViewPager);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        if (isViewPagerActive()) {
            outState.putBoolean(ISLOCKED_ARG, ((HackyViewPager) mViewPager).isLocked());
        }
        super.onSaveInstanceState(outState);
    }
}
