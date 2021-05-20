package com.barswipe.fram;

import android.os.Bundle;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.view.ViewCompat;
import androidx.appcompat.app.AppCompatActivity;

import com.barswipe.R;
import com.jakewharton.rxbinding2.view.RxView;

/**
 * http://blog.csdn.net/max2005/article/details/51914246
 * http://ms.csdn.net/geek/88224?from=timeline&isappinstalled=1
 * 上面已看
 * <p>
 * http://www.cnblogs.com/zqlxtt/p/5206466.html
 * http://www.07net01.com/2015/12/1036890.html
 * http://blog.csdn.net/zhangke3016/article/details/52268136
 * http://blog.csdn.net/hunanqi/article/details/52597675
 * http://www.tuicool.com/articles/ju2muuU
 * http://blog.csdn.net/u011277123/article/details/52238062
 * http://www.jianshu.com/p/a506ee4afecb
 * http://www.jianshu.com/p/d372d37e8640
 * http://www.jianshu.com/p/72d45d1f7d55
 * http://www.jianshu.com/p/26439595ffef
 * http://blog.csdn.net/tiankong1206/article/details/48394393
 * https://segmentfault.com/a/1190000006657044
 * http://androidwing.net/index.php/70
 */
public class CustomBehaviorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_behavior);

        RxView.clicks(findViewById(R.id.depentent))
                .subscribe(avoid -> ViewCompat.offsetTopAndBottom(findViewById(R.id.depentent), 15));

        RxView.clicks(findViewById(R.id.follow))
                .subscribe(avoid -> Snackbar.make(findViewById(R.id.follow), "Snackbar 和 FloatingActionButton的联系", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show());
    }
}
