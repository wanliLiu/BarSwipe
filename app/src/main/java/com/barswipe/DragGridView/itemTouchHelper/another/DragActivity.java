package com.barswipe.DragGridView.itemTouchHelper.another;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;

import com.barswipe.R;

import java.util.ArrayList;
import java.util.List;


/**
 * 拖拽
 * Created by YoKeyword on 16/1/4.
 */
public class DragActivity extends AppCompatActivity {
    private RecyclerView mRecy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo_tei);

        mRecy = (RecyclerView) findViewById(R.id.recy);
        init();
    }

    private void init() {
        final List<String> items = new ArrayList<>();
        for (int i = 0; i < 18; i++) {
            items.add("Index " + i);
        }

        GridLayoutManager manager = new GridLayoutManager(this, 2);
        mRecy.setLayoutManager(manager);

        ItemDragHelperCallback callback = new ItemDragHelperCallback(){
            @Override
            public boolean isLongPressDragEnabled() {
                // 长按拖拽打开
                return true;
            }
        };
        ItemTouchHelper helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(mRecy);

        DragAdapter adapter = new DragAdapter(this, items);
        mRecy.setAdapter(adapter);
    }
}