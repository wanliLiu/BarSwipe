package com.barswipe.PhotoDraweeView.example;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.os.Bundle;
import androidx.viewpager.widget.PagerAdapter;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;

import com.barswipe.PhotoDraweeView.lib.OnPhotoTapListener;
import com.barswipe.PhotoDraweeView.lib.PhotoDraweeView;
import com.barswipe.R;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.imagepipeline.image.ImageInfo;

public class ViewPagerActivity extends AppCompatActivity {

    public static void startActivity(Context context) {
        context.startActivity(new Intent(context, ViewPagerActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewpager);

        ((Toolbar) findViewById(R.id.toolbar)).setNavigationOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onBackPressed();
                    }
                });

        MultiTouchViewPager viewPager = (MultiTouchViewPager) findViewById(R.id.view_pager);
        viewPager.setAdapter(new DraweePagerAdapter());
    }

    public class DraweePagerAdapter extends PagerAdapter {

        private int[] mDrawables = new int[]{
                R.drawable.viewpager_1, R.drawable.viewpager_2, R.drawable.viewpager_3
        };

        @Override
        public int getCount() {
            return mDrawables.length + 3;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public Object instantiateItem(ViewGroup viewGroup, int position) {
            if (position != getCount() - 1) {
                final PhotoDraweeView photoDraweeView = new PhotoDraweeView(viewGroup.getContext());
                PipelineDraweeControllerBuilder controller = Fresco.newDraweeControllerBuilder();
                photoDraweeView.getHierarchy().setProgressBarImage(new ImageLoadingDrawable());
                controller.setAutoPlayAnimations(true);
                if (position == getCount() - 3)
                    controller.setUri("http://dynamic-image.yesky.com/740x-/uploadImages/2015/163/50/690V3VHW0P77.jpg");
                else if (position == getCount() - 2)
                    controller.setUri("http://wimg.spriteapp.cn/ugc/2016/08/06/57a5b6fd46c06.gif");
                else
                    controller.setUri(Uri.parse("res:///" + mDrawables[position]));
                controller.setOldController(photoDraweeView.getController());
                controller.setControllerListener(new BaseControllerListener<ImageInfo>() {
                    @Override
                    public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable) {
                        super.onFinalImageSet(id, imageInfo, animatable);
                        if (imageInfo == null) {
                            return;
                        }
                        photoDraweeView.update(imageInfo.getWidth(), imageInfo.getHeight());
                    }
                });
                photoDraweeView.setController(controller.build());

                try {
                    viewGroup.addView(photoDraweeView, ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                photoDraweeView.setOnPhotoTapListener(new OnPhotoTapListener() {
                    @Override
                    public void onPhotoTap(View view, float x, float y) {
                        ViewPagerActivity.this.finish();
                    }
                });

                return photoDraweeView;
            } else {
                SubsamplingScaleImageView subsamplingScaleImageView = new SubsamplingScaleImageView(viewGroup.getContext());
//                subsamplingScaleImageView.setImageUri("http://img01.starfans.com/100016_699e814fe7a18ae66b48bc72f3e59ede[600_8986_561].jpg");
                subsamplingScaleImageView.tesst();

                try {
                    viewGroup.addView(subsamplingScaleImageView, ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return subsamplingScaleImageView;
            }
        }
    }
}
