package com.barswipe.draweePhotoView.activity;

import android.os.Build;
import android.os.Bundle;
import com.google.android.material.navigation.NavigationView;
import androidx.fragment.app.Fragment;
import androidx.drawerlayout.widget.DrawerLayout;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;

import com.barswipe.R;
import com.barswipe.draweePhotoView.fragment.OneFragment;
import com.barswipe.draweePhotoView.fragment.TwoFragment;
import com.barswipe.draweePhotoView.lib.MySimpleDraweeView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MainActivityPhoto extends BaseActivity {

    @BindView(R.id.navigationView)
    NavigationView navigationView;
    @BindView(R.id.drawer)
    DrawerLayout drawer;

    private Unbinder unbinder;
    MySimpleDraweeView ivMenuUserProfilePhoto;
    LinearLayout vGlobalMenuHeader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main_photoview);
        unbinder = ButterKnife.bind(this);
        final View headerView = navigationView.getHeaderView(0);
        ivMenuUserProfilePhoto = (MySimpleDraweeView) headerView.findViewById(R.id.ivMenuUserProfilePhoto);
        vGlobalMenuHeader = (LinearLayout) headerView.findViewById(R.id.vGlobalMenuHeader);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.layContentRoot, OneFragment.newInstance(), OneFragment.class.getSimpleName())
                    .commit();
        }

        initMenuHeader();

        initNavigationView();
    }

    private void initNavigationView() {
        navigationView.setNavigationItemSelectedListener(menuItem -> {

            switch (menuItem.getItemId()) {
                case R.id.menu_1:
                    Fragment oneFragment = getSupportFragmentManager().findFragmentByTag(OneFragment.class.getSimpleName());
                    if (oneFragment == null) {
                        oneFragment = OneFragment.newInstance();
                    }
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.layContentRoot, oneFragment, OneFragment.class.getSimpleName())
                            .addToBackStack("oneFragment")
                            .commit();
                    break;
                case R.id.menu_2:
                    Fragment twoFragment = getSupportFragmentManager().findFragmentByTag(TwoFragment.class.getSimpleName());
                    if (twoFragment == null) {
                        twoFragment = TwoFragment.newInstance();
                    }
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.layContentRoot, twoFragment, TwoFragment.class.getSimpleName())
                            .addToBackStack("TwoFragment")
                            .commit();
                    break;
            }
            drawer.closeDrawer(Gravity.LEFT);
            return false;
        });
    }

    private void initMenuHeader() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            vGlobalMenuHeader.setPadding(vGlobalMenuHeader.getPaddingLeft(),
                    vGlobalMenuHeader.getPaddingTop() + this.getResources().getDimensionPixelSize(R.dimen.status_bar_height),
                    vGlobalMenuHeader.getPaddingRight(),
                    vGlobalMenuHeader.getPaddingBottom());
        }
        ivMenuUserProfilePhoto.setRoundDraweeViewUrl("http://git.oschina.net/biezhihua/MyResource/raw/master/biezhihua.png");
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
