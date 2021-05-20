package com.barswipe.ViewDragHelper;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;

import com.barswipe.BaseActivity;
import com.barswipe.R;

/**
 * Created by soli on 5/29/16.
 */
public class ViewDragHelperStudyActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.view_drag_helper);

        findViewById(R.id.testBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}
