package com.barswipe.marquee.app;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.barswipe.R;
import com.barswipe.marquee.MarqueeLayout;
import com.barswipe.marquee.MarqueeLayoutAdapter;
import com.barswipe.marquee.OnItemClickListener;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MarqueeMainActivity extends AppCompatActivity {

    private MarqueeLayout mMarqueeLayout;
    private MarqueeLayout mMarqueeLayout1;

    private List<String> mSrcList;
    private MarqueeLayoutAdapter<String> mSrcAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_marquee);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mMarqueeLayout = (MarqueeLayout) findViewById(R.id.marquee_layout);
        mSrcList = new ArrayList<>();
        mSrcList.add("我听见了你的声音 也藏着颗不敢见的心");
        mSrcList.add("我们的爱情到这刚刚好 剩不多也不少 还能忘掉");
        mSrcList.add("像海浪撞过了山丘以后还能撑多久 他可能只为你赞美一句后往回流");
        mSrcList.add("少了有点不甘 但多了太烦");
        mSrcAdapter = new MarqueeLayoutAdapter<String>(mSrcList) {
            @Override
            public int getItemLayoutId() {
                return R.layout.item_simple_text;
            }

            @Override
            public void initView(View view, int position, String item) {
                ((TextView) view.findViewById(R.id.test)).setText(item);

            }
        };
        mMarqueeLayout.setAdapter(mSrcAdapter);
        mMarqueeLayout.start();

        mMarqueeLayout1 = (MarqueeLayout) findViewById(R.id.marquee_layout1);
        final List<String> imgs = new ArrayList<>();
        imgs.add("http://img3.imgtn.bdimg.com/it/u=936722914,2010466745&fm=11&gp=0.jpg");
        imgs.add("http://img5.imgtn.bdimg.com/it/u=793061750,504065085&fm=11&gp=0.jpg");
        imgs.add("http://img5.imgtn.bdimg.com/it/u=506823331,38014690&fm=11&gp=0.jpg");
        imgs.add("http://h.hiphotos.baidu.com/baike/pic/item/2fdda3cc7cd98d10e6a5b4aa273fb80e7bec903c.jpg");
        MarqueeLayoutAdapter<String> adapter1 = new MarqueeLayoutAdapter<String>(imgs) {
            @Override
            public int getItemLayoutId() {
                return R.layout.item_simple_image;
            }

            @Override
            public void initView(View view, int position, String item) {
                Glide.with(view.getContext()).load(item).into((ImageView) view);
            }
        };
        adapter1.setItemClickListener(new OnItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                Log.e("TAG", "MainActivity-74行-onClick(): " + position);
            }
        }, R.id.iv);
        mMarqueeLayout1.setAdapter(adapter1);
        mMarqueeLayout1.start();

    }

    // 删歌词
    public void deleteSrc(View view) {
        if (mSrcList.size() != 0) {
            mSrcList.remove(mSrcList.size() - 1);
            mSrcAdapter.notifyDataSetChanged();
        }
    }

    // 添加歌词
    public void addSrc(View view) {
        if (mSrcList != null) {
            Random random = new Random();
            mSrcList.add("添加歌词: " + random.nextInt(12345));
            mSrcAdapter.notifyDataSetChanged();
        }
    }

}
