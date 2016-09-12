package com.barswipe;

import android.os.Bundle;
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
public class WebViewActivity extends BaseActivity {

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
                view.loadUrl(url);
                return true;
            }
        });

        String html = "<!doctype html>\n" +
                "<html>\n" +
                " <head> \n" +
                "  <meta charset=\"utf-8\"> \n" +
                "  <meta name=\"viewport\" content=\"user-scalable=0, width=device-width, initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0\"> \n" +
                "  <style>\n" +
                "        html,body,p,h1,h2,h3,h4,ul,ol,li{margin:0;padding:0;color: #333;font: 15px/1.5 sans-serif;}\n" +
                "        .p-content{word-break: break-all;}\n" +
                "        .p-content img,.p-content iframe{max-width: 100%;}\n" +
                "        .p-content iframe{width: 100%!important;}\n" +
                "    </style> \n" +
                " </head> \n" +
                " <body> \n" +
                "  <div class=\"p-content\">\n" +
                "   <p style=\"text-align: center;\">《青年晚报》签售-成都站</p> \n" +
                "   <p style=\"text-align: center;\">更多信息请持续关注Vae+</p> \n" +
                "   <p><br></p> \n" +
                "   <p>本次签售共签1500本，主办方官方天猫店【沃音乐商城】将于9月9日上午10:00开始预售签名专辑，每个淘宝账号限购5张。</p> \n" +
                "   <p>每张专辑60元！</p> \n" +
                "   <p>每张专辑60元！</p> \n" +
                "   <p>每张专辑60元！</p> \n" +
                "   <p><a href=\"https://item.taobao.com/item.htm?ut_sk=1.VgUPszukBNEDAGr3Fln7XBb5_21380790_1473345019265.TaoPassword-QQ.1&amp;id=538209446030&amp;sourceType=item&amp;price=60&amp;suid=1AD6BC92-F1A5-4F71-91D8-3B95A515245E&amp;cpp=1&amp;shareurl=true&amp;spm=a313p.22.1cj.9905922217&amp;short_name=h.0VnUCT&amp;cv=AAJrsctN&amp;sm=7cf9e0&amp;app=chrome\" target=\"_blank\"><span style=\"color: rgb(255, 0, 0);\"><strong>预售地址摸我</strong></span></a></p> \n" +
                "   <p><br></p> \n" +
                "   <p>活动QQ群：262757979</p> \n" +
                "   <p><br></p> \n" +
                "   <p><img src=\"http://img01.xusong.com/fb59824500674505a33e7cd88016a33b[800_2706_1047].jpg@!640\" title=\"1.jpg\" alt=\"\" style=\"\" width=\"\" height=\"\" onclick=\"openImage('http://img01.xusong.com/fb59824500674505a33e7cd88016a33b[800_2706_1047].jpg')\"></p>\n" +
                "  </div>  \n" +
                " </body>\n" +
                "</html>";

        myWebView.loadDataWithBaseURL(null, html, "text/html", "utf-8", null);
//        myWebView.loadUrl("http://boutique.m.milanoo.com/fr?source=app&currency=EUR");
    }
}
