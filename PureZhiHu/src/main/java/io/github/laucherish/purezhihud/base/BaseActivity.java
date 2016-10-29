package io.github.laucherish.purezhihud.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.View;

import butterknife.ButterKnife;
import io.github.laucherish.purezhihud.utils.PrefUtil;
import io.github.laucherish.purezhihud.utils.swipeback.SwipeBackActivity;
import io.github.laucherish.purezhihud.utils.swipeback.SwipeBackLayout;

/**
 * Created by laucherish on 16/3/15.
 */
public abstract class BaseActivity extends SwipeBackActivity {

    protected SwipeBackLayout mSwipeBackLayout;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            scrollToFinishActivity();
        }
        return true;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(PrefUtil.getThemeRes());
        if (!PrefUtil.isNight())
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        mSwipeBackLayout = getSwipeBackLayout();
        mSwipeBackLayout.setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT);
        ButterKnife.bind(this);
        afterCreate(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    protected abstract int getLayoutId();

    protected abstract void afterCreate(Bundle savedInstanceState);
}
