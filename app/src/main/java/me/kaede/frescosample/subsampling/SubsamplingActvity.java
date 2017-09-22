package me.kaede.frescosample.subsampling;

import android.os.Bundle;

import com.barswipe.BaseActivity;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

public class SubsamplingActvity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		setContentView(R.layout.activity_subsampling_actvity);

//		SubsamplingScaleImageView subsamplingScaleImageView = (SubsamplingScaleImageView) this.findViewById(R.id.scaleimageview);
		SubsamplingScaleImageView subsamplingScaleImageView = new SubsamplingScaleImageView(this);
//		subsamplingScaleImageView.setImageUri(ImageApi.other.getUrlByName("longimage", ".jpg"));
//		subsamplingScaleImageView.setImageUri("asset:///big_pic.jpg");
//		subsamplingScaleImageView.setDebug(true);
//		subsamplingScaleImageView.setImageUri("res:///" + R.drawable.big_pic_big);
//		subsamplingScaleImageView.setImageUri("http://img01.starfans.com/100016_699e814fe7a18ae66b48bc72f3e59ede[600_8986_561].jpg");
		subsamplingScaleImageView.tesst();
		setContentView(subsamplingScaleImageView);
	}
}
