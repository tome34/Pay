package com.replay.limty.model.wxh5;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by Administrator on 2017/5/4 0004.
 */

public class MyWebView extends WebViewClient {

    Activity activity;

    public MyWebView(Activity activity) {
        super();
        this.activity = activity;
    }

    public final boolean shouldOverrideUrlLoading(WebView webView, String url) {
        Intent intent;
        Log.i("测试","var2=="+url);
        if(url.contains("alipays://")) {
            try {
                intent = new Intent("android.intent.action.VIEW", Uri.parse(url));
                activity.startActivity(intent);
                return true;
            } catch (Exception var4) {
                return true;
            }
        } else {
            if(url.contains("platformapi/startapp")) {
                try {
                    intent = Intent.parseUri(url, 1).addCategory("android.intent.category.BROWSABLE");
                    intent.setComponent(null);
                    activity.startActivity(intent);
                    return true;
                } catch (Exception e) {

                }
            } else if(url.contains("weixin://")) {
                try {
                    intent = new Intent("android.intent.action.VIEW", Uri.parse(url));
                    activity.startActivity(intent);
                    activity.finish();
                    return true;
                } catch (Exception var5) {
                    return true;
                }
            }
            webView.loadUrl(url);
            return true;
        }
    }
}
