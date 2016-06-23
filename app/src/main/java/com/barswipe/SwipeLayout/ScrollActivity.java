package com.barswipe.SwipeLayout;

import android.os.Bundle;
import android.webkit.WebView;

import com.barswipe.BaseActivity;
import com.barswipe.R;

/**
 * Created by SoLi on 2016/5/16.
 */
public class ScrollActivity extends BaseActivity {

    WebView myWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.swipeitem);
        setContentView(R.layout.activity_scroll);

//        myWebView = (WebView)findViewById(R.id.webview);
//
//        myWebView.getSettings().setJavaScriptEnabled(true);
//        myWebView.getSettings().setUseWideViewPort(true);
//        myWebView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
//        myWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
//
//        myWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
//
//        myWebView.setWebViewClient(new WebViewClient() {
//            @Override
//            public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                return super.shouldOverrideUrlLoading(view, url);
//            }
//        });
//
//        myWebView.setWebChromeClient(new WebChromeClient() {
//            @Override
//            public void onProgressChanged(WebView view, final int newProgress) {
//                super.onProgressChanged(view, newProgress);
//            }
//        });
//
//        myWebView.loadUrl("http://m.milanoo.com/");
    }

}
