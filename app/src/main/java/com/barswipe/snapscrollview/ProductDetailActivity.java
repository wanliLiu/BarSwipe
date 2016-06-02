package com.barswipe.snapscrollview;

import android.os.Bundle;

import com.barswipe.BaseActivity;
import com.barswipe.R;

public class ProductDetailActivity extends BaseActivity {

    private McoySnapPageLayout mcoySnapPageLayout = null;

    private McoyProductContentPage bottomPage = null;
    private McoyProductDetailInfoPage topPage = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_detail);

        mcoySnapPageLayout = (McoySnapPageLayout) findViewById(R.id.flipLayout);

        topPage = new McoyProductDetailInfoPage(ProductDetailActivity.this, getLayoutInflater().inflate(R.layout.mcoy_produt_detail_layout, null));
        bottomPage = new McoyProductContentPage(ProductDetailActivity.this, getLayoutInflater().inflate(R.layout.mcoy_product_content_page, null));

        mcoySnapPageLayout.setSnapPages(topPage, bottomPage);
    }

}
