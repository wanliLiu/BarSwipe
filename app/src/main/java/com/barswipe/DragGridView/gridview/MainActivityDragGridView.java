package com.barswipe.DragGridView.gridview;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Toast;

import com.barswipe.DragGridView.gridview.lib.DragReorderGridView;
import com.barswipe.DragGridView.gridview.lib.DragReorderListener;
import com.barswipe.R;

import java.util.ArrayList;
import java.util.List;

public class MainActivityDragGridView extends Activity {

    private DragReorderGridView mGridView;
    private MyAdapter mAdapter;
    private List<Item> mItems;

    private String[] sCheeseStrings = {"沪深市场", "港股行情", "天汇宝2号", "融资融券",
            "新股发行", "手机开户", "全部行情", "自选资讯", "港股通", "场内基金"};
    private int[] sCheeseIcons = {R.mipmap.ht_iggt, R.mipmap.ht_ihssc,
            R.mipmap.ht_iqbhq, R.mipmap.ht_iqqhq, R.mipmap.ht_irwz,
            R.mipmap.ht_irzrj, R.mipmap.ht_isjkh, R.mipmap.ht_ithbeh,
            R.mipmap.ht_iwdzx, R.mipmap.ht_ixgsg};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid);

        mGridView = (DragReorderGridView) findViewById(R.id.grid);

        initData();

        mAdapter = new MyAdapter(mItems, this);
        mGridView.setAdapter(mAdapter);
        mGridView
                .setDragReorderListener(R.id.item_delete, mDragReorderListener);

        findViewById(R.id.test).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = mGridView.findViewByPosition(0);
                View view1 = mGridView.findViewByPosition(4);
                View view2 = mGridView.findViewByPosition(8);
                View view3 = mGridView.findViewByPosition(12);
                View view4 = mGridView.findViewByPosition(3);

                TranslateAnimation translate = new TranslateAnimation(
                        Animation.ABSOLUTE, view.getWidth(), Animation.ABSOLUTE, 0,
                        Animation.ABSOLUTE, 0, Animation.ABSOLUTE, 0);
                translate.setDuration(1000);
                translate.setFillEnabled(true);
                translate.setFillAfter(false);

                TranslateAnimation translate1 = new TranslateAnimation(
                        Animation.RELATIVE_TO_SELF, 0.8f, Animation.ABSOLUTE, 0,
                        Animation.RELATIVE_TO_SELF, 0.5f, Animation.ABSOLUTE, 0);
                translate1.setDuration(1000);
                translate1.setFillEnabled(true);
                translate1.setFillAfter(false);

                TranslateAnimation translate2 = new TranslateAnimation(
                        Animation.RELATIVE_TO_PARENT, 0.8f, Animation.RELATIVE_TO_SELF, 0.2f,
                        Animation.RELATIVE_TO_PARENT, 0.5f, Animation.RELATIVE_TO_SELF, 0.3f);
                translate2.setDuration(1000);
                translate2.setFillEnabled(true);
                translate2.setFillAfter(true);

                TranslateAnimation translate3 = new TranslateAnimation(
                        Animation.RELATIVE_TO_SELF, 0.8f, Animation.RELATIVE_TO_SELF, 0.2f,
                        Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.2f);
                translate3.setDuration(1000);
                translate3.setFillEnabled(true);
                translate3.setFillAfter(true);

                TranslateAnimation translate4 = new TranslateAnimation(
                        Animation.RELATIVE_TO_SELF, -0.8f, Animation.RELATIVE_TO_SELF, 0.2f,
                        Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.2f);
                translate4.setDuration(1000);
                translate4.setFillEnabled(true);
                translate4.setFillAfter(true);


                view.clearAnimation();
                view.startAnimation(translate);

                view1.clearAnimation();
                view1.startAnimation(translate1);

                view2.clearAnimation();
                view2.startAnimation(translate2);

                view3.clearAnimation();
                view3.startAnimation(translate3);

                view4.clearAnimation();
                view4.startAnimation(translate4);
            }
        });
    }

    private void initData() {
        mItems = new ArrayList<Item>();
        int count = sCheeseStrings.length;
        for (int i = 0; i < count; i++) {
            Item item = new Item();
            item.setLabel(sCheeseStrings[i]);
            item.setIcon(sCheeseIcons[i]);
            if (i < 3) {
                item.setRemovable(false);
            }
            mItems.add(item);
        }

        Item addBtn = new Item();
        addBtn.setLabel("");
        addBtn.setIcon(R.mipmap.add_func);
        addBtn.setFixed(true);
        mItems.add(addBtn);
        for (int i = 0; i < 100; i++) {

            Item add12Btn = new Item();
            add12Btn.setLabel("Num_" + i);
            add12Btn.setIcon(R.mipmap.icon);
            mItems.add(add12Btn);
        }
    }

    @Override
    public void onBackPressed() {
        if (mGridView.isDragEditMode()) {
            mGridView.quitEditMode();
            return;
        }
        super.onBackPressed();
    }

    private DragReorderListener mDragReorderListener = new DragReorderListener() {

        @Override
        public void onReorder(int fromPosition, int toPosition) {
            Log.e("fromPositionTo", fromPosition + "--" + toPosition);
            ((MyAdapter) mGridView.getAdapter()).reorder(fromPosition, toPosition);
        }

        @Override
        public void onDragEnded() {
            // TODO Auto-generated method stub

        }

        @Override
        public void onItemLongClicked(int position) {
            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            v.vibrate(50);
        }

        @Override
        public void onItemClicked(int position) {
            if (mItems.get(position).getLabel().equals("")) {
                Item item = new Item();
                item.setIcon(R.mipmap.icon);
                int insertPos = mItems.size() - 1;
                item.setLabel("" + insertPos);
                mItems.add(insertPos, item);
                mAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(MainActivityDragGridView.this,
                        "click item " + mItems.get(position).getLabel(),
                        Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onEditAction(int position) {
            Toast.makeText(MainActivityDragGridView.this,
                    "deleting " + mAdapter.getData().get(position).getLabel(),
                    Toast.LENGTH_SHORT).show();
            mAdapter.removeItem(position);
            mAdapter.notifyDataSetChanged();

        }

    };

}
