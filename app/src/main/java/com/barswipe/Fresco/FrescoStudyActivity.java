package com.barswipe.Fresco;

import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.IdRes;

import com.barswipe.BaseActivity;
import com.barswipe.R;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.drawable.ProgressBarDrawable;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;

/**
 * Created by soli on 7/26/16.
 */
public class FrescoStudyActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fresco_study_layout);


        findViewAndLoadAnimatedImageUri(
                R.id.animated_gif,
                "http://wimg.spriteapp.cn/ugc/2016/08/06/57a5b6fd46c06.gif");
    }

    /**
     * @param viewId
     * @return
     */
    private SimpleDraweeView findAndPrepare(@IdRes int viewId) {
        SimpleDraweeView view = (SimpleDraweeView) findViewById(viewId);
        view.getHierarchy().setProgressBarImage(new ProgressBarDrawable());
        return view;
    }


    /**
     * @param viewId
     * @param uri
     * @return
     */
    private SimpleDraweeView findViewAndLoadUri(@IdRes int viewId, String uri) {
        SimpleDraweeView view = findAndPrepare(viewId);
        view.setImageURI(Uri.parse(uri));
        return view;
    }


    /**
     * @param viewId
     * @param controller
     * @return
     */
    private SimpleDraweeView findViewAndSetController(
            @IdRes int viewId,
            DraweeController controller) {
        SimpleDraweeView view = findAndPrepare(viewId);
        view.setController(controller);
        return view;
    }

    /**
     * @param viewId
     * @param uri
     * @return
     */
    private SimpleDraweeView findViewAndLoadAnimatedImageUri(@IdRes int viewId, String uri) {
        DraweeController animatedController = Fresco.newDraweeControllerBuilder()
                .setAutoPlayAnimations(true)
                .setUri(Uri.parse(uri))
                .build();
        return findViewAndSetController(viewId, animatedController);
    }
}
