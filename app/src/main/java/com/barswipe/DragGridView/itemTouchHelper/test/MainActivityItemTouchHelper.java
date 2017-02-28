/*
 * Copyright (C) 2015 Paul Burke
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.barswipe.DragGridView.itemTouchHelper.test;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.barswipe.DragGridView.itemTouchHelper.another.ChannelActivity;
import com.barswipe.DragGridView.itemTouchHelper.another.DragActivity;
import com.barswipe.DragGridView.itemTouchHelper.recycle.MyGridFragment;
import com.barswipe.DragGridView.itemTouchHelper.recycle.MyListFragment;
import com.barswipe.R;

/**
 * 参考  RecyclerView的拖动和滑动 第一部分 ：基本的ItemTouchHelper示例
 * http://blog.csdn.net/hanhailong726188/article/details/47073843
 *
 * @author Paul Burke (ipaulpro)
 */
public class MainActivityItemTouchHelper extends AppCompatActivity implements MainFragment.OnListItemClickListener {

    //简单
//    ItemTouchHelper.SimpleCallback

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main_itemtouch);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        if (savedInstanceState == null) {
            MainFragment fragment = new MainFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.content, fragment)
                    .commit();
        }
    }

    @Override
    public void onListItemClick(int position) {
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = new RecyclerListFragment();
                break;
            case 1:
                fragment = new RecyclerGridFragment();
                break;
            case 2:
                startActivity(new Intent(this, DragActivity.class));
                break;
            case 3:
                startActivity(new Intent(this, ChannelActivity.class));
                break;
            case 4:
                fragment = new MyListFragment();
                break;
            case 5:
                fragment = new MyGridFragment();
                break;
        }

        if (fragment != null)
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content, fragment)
                    .addToBackStack(null)
                    .commit();
    }

}
