package me.kaede.frescosample.progressivejpg;

import android.net.Uri;
import android.os.Bundle;

import com.barswipe.BaseActivity;
import com.barswipe.R;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.drawable.ProgressBarDrawable;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import me.kaede.frescosample.ImageApi;

public class ProgressiveJPGActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_simple);

		SimpleDraweeView draweeView = (SimpleDraweeView) this.findViewById(R.id.drawee_main);

		ImageRequest request = ImageRequestBuilder.newBuilderWithSource(Uri.parse(ImageApi.other.getUrlByName("lowres-big", ".jpg")))
				.setAutoRotateEnabled(true)
				.setLocalThumbnailPreviewsEnabled(true)
				.setProgressiveRenderingEnabled(true)
				.build();

		DraweeController controller = Fresco.newDraweeControllerBuilder()
				.setImageRequest(request)
				.build();
		draweeView.setController(controller);

		GenericDraweeHierarchyBuilder builder = new GenericDraweeHierarchyBuilder(getResources());
		GenericDraweeHierarchy hierarchy = builder
				.setProgressBarImage(new ProgressBarDrawable())
				.setPlaceholderImage(getResources().getDrawable(R.drawable.image_loading))
				.build();
		draweeView.setHierarchy(hierarchy);
	}
}
