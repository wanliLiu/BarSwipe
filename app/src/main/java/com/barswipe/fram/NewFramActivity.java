package com.barswipe.fram;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.view.View;
import android.widget.ToggleButton;

import com.barswipe.BaseActivity;
import com.barswipe.R;
import com.jakewharton.rxbinding.widget.RxCompoundButton;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.functions.Action1;

/**
 * Created by soli on 29/10/2016.
 */

public class NewFramActivity extends BaseActivity {

    @Bind(R.id.themeCheck)
    ToggleButton btn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(R.style.lightTheme);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_frame);
        ButterKnife.bind(this);


//        setTheme(R.style.lightTheme);

        RxCompoundButton.checkedChanges(btn)
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
//                        setTheme(aBoolean ? R.style.darkTheme : R.style.lightTheme);
                        if (aBoolean) {
//                            startActivity(new Intent(NewFramActivity.this, ScrollingActivity.class));
                            showBottomSheet();
                        }
                    }
                });

    }

    private void showBottomSheet()
    {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        dialog.setTitle("BottomSheetDialog 学习");
        dialog.setContentView(R.layout.test_bottom_sheet);
        dialog.show();
    }
}
