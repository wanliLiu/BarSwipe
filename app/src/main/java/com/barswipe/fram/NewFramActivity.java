package com.barswipe.fram;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.ToggleButton;

import com.barswipe.BaseActivity;
import com.barswipe.GridLayoutStudy;
import com.barswipe.R;
import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxCompoundButton;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by soli on 29/10/2016.
 */

public class NewFramActivity extends BaseActivity {

    @BindView(R.id.themeCheck)
    ToggleButton btn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(R.style.lightTheme);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_frame);
        ButterKnife.bind(this);


        setTheme(R.style.lightTheme);

        RxView.clicks(findViewById(R.id.bottomNavigation))
                .subscribe(a -> startActivity(new Intent(NewFramActivity.this, BottomNavigationActivity.class)));

        RxCompoundButton.checkedChanges(btn)
                .subscribe(aBoolean -> {
                    setTheme(aBoolean ? R.style.darkTheme : R.style.lightTheme);
                    if (aBoolean) {
                        startActivity(new Intent(NewFramActivity.this, ScrollingActivity.class));
                    }
                });


        RxView.clicks(findViewById(R.id.btnBottom))
                .subscribe(av -> showBottomSheet());


        RxView.clicks(findViewById(R.id.button6))
                .subscribe(a -> startActivity(new Intent(NewFramActivity.this, FullscreenActivity.class)));

        RxView.clicks(findViewById(R.id.btnBehavior))
                .subscribe(a -> startActivity(new Intent(NewFramActivity.this, CustomBehaviorActivity.class)));

        RxView.clicks(findViewById(R.id.btnGridLayout))
                .subscribe(a -> startActivity(new Intent(NewFramActivity.this, GridLayoutStudy.class)));

        RxView.clicks(findViewById(R.id.snackbarTest))
                .subscribe(a -> Snackbar.make(findViewById(R.id.snackbarTest), "Snackbar 和 FloatingActionButton的联系", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show());
    }

    /**
     *
     */
    private void showBottomSheet() {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        dialog.setTitle("BottomSheetDialog 学习");
        dialog.setContentView(R.layout.test_bottom_sheet);
        dialog.show();
    }
}
