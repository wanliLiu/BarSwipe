package me.kaede.frescosample.subsampling;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.barswipe.R;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

public class SubsamplingActvity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_subsampling_actvity);

		SubsamplingScaleImageView subsamplingScaleImageView = (SubsamplingScaleImageView) this.findViewById(R.id.scaleimageview);
//		subsamplingScaleImageView.setImageUri(ImageApi.other.getUrlByName("longimage", ".jpg"));
		subsamplingScaleImageView.setImageUri("asset:///big_pic.jpg");
//		subsamplingScaleImageView.setImageUri("res:///" + R.drawable.big_pic_big);
	}
}
