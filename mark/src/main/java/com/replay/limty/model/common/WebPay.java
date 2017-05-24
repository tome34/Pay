package com.replay.limty.model.common;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.replay.limty.control.PayRequest;
import com.replay.limty.model.wxgzh.WebPayment;
import com.replay.limty.model.zfbh5.ZfbH5Request;
import com.replay.limty.utils.DesHelper;
import com.replay.limty.utils.Pref;

import org.json.JSONObject;

import java.net.URLEncoder;

import static android.content.ContentValues.TAG;
import static com.replay.limty.model.zfbh5.ZfbH5Request.context;

/**
 * Created by YCH on 17/5/14.
 */
public class WebPay implements WebInterface{

    @Override
    public void openWxWeb(Activity activity, String string) {
        try {
            PayRequest.getInstance().executeTask();
            Log.d(TAG, "key  :"+Pref.with(activity).read("key", ""));
            Log.d(TAG, "string  :"+string);
            String str = DesHelper.decrypt(string, Pref.with(activity).read("key", ""));
            Log.i("测试", "openWxWeb: "+str);
            JSONObject obj = new JSONObject(str);
            String url = obj.optString("link");
            WebPayment.merchID = obj.optString("mch_id");
            PayRequest.key = obj.optString("key");
            url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid="+obj.optString("appId")+"&redirect_uri=" + URLEncoder.encode(url) + "&response_type=code&scope=snsapi_base&state=" + AsyncData.orderInfo.getOrderNumber() + "#wechat_redirect";
            Log.i("测试", "URL: "+url);
            String var1 = "com.tencent.mm";
            String var2 = "com.tencent.mm.plugin.base.stub.WXCustomSchemeEntryActivity";
            String linkUrl = "weixin://dl/businessWebview/link/?appid="+obj.optString("openAPPID")+"&url=";
            url = linkUrl + URLEncoder.encode(url);
            Log.i("测试", "url: "+url);
            Intent intent = new Intent();
            intent.setData(Uri.parse(url));
            intent.setAction(Intent.ACTION_VIEW);
            ComponentName com = new ComponentName(var1, var2);
            intent.addCategory("android.intent.category.BROWSABLE");
            intent.putExtra("translate_link_scene", 1);
            intent.setComponent(com);
            Log.i("测试", "com: "+com);
            activity.startActivity(intent);
        } catch (Exception e) {
            Log.i("测试", "openWxWeb: "+e.toString());
        }
    }

    public static void cmdOpenWeiXin(String string,Activity activity){
        try {
            String str = DesHelper.decrypt(string, Pref.with(activity).read("key", ""));
            Log.i("测试", "openWxWeb: "+str);
            JSONObject obj = new JSONObject(str);
            String url = obj.optString("link");
            WebPayment.merchID = obj.optString("mch_id");
            PayRequest.key = obj.optString("key");
            Log.i("测试", "key: "+PayRequest.key);
            url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid="+obj.optString("appId")+"&redirect_uri=" + URLEncoder.encode(url) + "&response_type=code&scope=snsapi_base&state=" + AsyncData.orderInfo.getOrderNumber() + "#wechat_redirect";
            Log.i("测试", "url: "+url);
            String cmd = "am start -n com.tencent.mm/.plugin.webview.ui.tools.WebViewUI -d '"+url+"'";
            Log.i("测试", "cmd: "+cmd);
            Runtime.getRuntime().exec(new String[]{"/system/bin/su","-c", cmd});

        } catch (Exception e) {
            Log.i("测试", "openWxWeb: "+e.toString());
            e.printStackTrace();
        }
    }

    @Override
    public void aliPay(final Activity activity, String url) {
        WebView webview = new WebView(ZfbH5Request.context);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setAppCacheEnabled(true);
        webview.getSettings().setAllowContentAccess(true);
        webview.getSettings().setDomStorageEnabled(true);
        webview.getSettings().setAllowFileAccess(true);
        webview.getSettings().setUserAgentString("Mozilla/5.0 (Linux; U; Android 6.0; zh-CN; FRD-DL00 Build/HUAWEIFRD-DL00) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 UCBrowser/11.0.8.858 U3/0.8.0 Mobile Safari/534.30");
        webview.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        webview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.toLowerCase().startsWith("alipays://") || (url.toLowerCase().startsWith("weixin://"))) {
                    try {
                        Uri pay_uri = Uri.parse(url);
                        Intent intent = new Intent(Intent.ACTION_VIEW, pay_uri);
                        activity.startActivity(intent);
                        return true;
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(context, "请安装支付宝客户端！", Toast.LENGTH_SHORT).show();
                    }
                }
                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }
        });
        webview.loadUrl(url);
        activity.setContentView(webview);
    }
}
