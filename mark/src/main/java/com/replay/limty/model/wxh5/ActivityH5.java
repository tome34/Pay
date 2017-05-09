package com.replay.limty.model.wxh5;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;

/**
 * Created by Administrator on 2017/5/4 0004.
 */

public class ActivityH5 extends Activity{

    private WebView webView;
    private Activity activity;

    public ActivityH5() {
    }

    protected void onCreate(Bundle var1) {
        super.onCreate(var1);
        this.activity = this;
        String url = this.getIntent().getStringExtra("uri");
        this.webView = new WebView(this);
        this.webView.loadUrl(url);
        this.webView.setWebViewClient(new MyWebView(this));
        WebSettings settings;
        settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        settings.setDomStorageEnabled(true);
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setAllowFileAccess(true);
        settings.setGeolocationEnabled(true);
        this.setContentView(this.webView);
    }
}
