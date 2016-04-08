package com.barswipe;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

/**
 * Created by SoLi on 2016/2/24.
 */
public class WebViewActivity extends Activity {

    private WebView myWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview);

        myWebView = (WebView) findViewById(R.id.webView);
        // 设置支持javascript
        myWebView.getSettings().setJavaScriptEnabled(true);
        myWebView.getSettings().setUseWideViewPort(true);
        myWebView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        myWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);

        myWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        //清除缓存
        CookieSyncManager cookieSyncMngr = CookieSyncManager.createInstance(WebViewActivity.this);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookie();
        myWebView.clearCache(true);
        myWebView.clearHistory();

        myWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, final int newProgress) {
                super.onProgressChanged(view, newProgress);
            }
        });

        // 设置加载提示条在加载完成前显示，完成后不显示
        myWebView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Toast.makeText(WebViewActivity.this,url,Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        myWebView.loadUrl("http://boutique.m.milanoo.com/fr?source=app&currency=EUR");
    }
}
