package com.payment.poliy.test;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.payment.poliy.R;

import java.net.URISyntaxException;

import static com.fast.utils.Utils.showToast;

public class TestActivity extends Activity {

    private static final String TAG = "微信浏览器";
    private static final String var1 = "com.tencent.mm";
    private static final String var2 = "com.tencent.mm.plugin.base.stub.WXCustomSchemeEntryActivity";
    private static final String var3 = "android.intent.action.VIEW";
    private static final String var4 = "translate_link_scene";
    private static final String var5 = "#";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
    }

    public void doClick(View view) {
        switch (view.getId()) {
            case R.id.button:
                shareToWechat();
                break;
            case R.id.button2:
                openWeChat();
                break;
        }
    }

    String url = "https://pay.swiftpass.cn/pay/jspay?token_id=1c5131a32fa50526c7408fc5023b3f911&showwxtitle=1";
    String url2 = "http://wxpay.wxutil.com/mch/pay/h5.v2.php";

    String url3 = "https://open.weixin.qq.com/sns/webview?url=https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx6fb989854b5583ed&redirect_uri=http%3a%2f%2fgame.yunzmei.com&response_type=code&scope=snsapi_base&state=STATE#wechat_redirect";

    String url4 = "weixin://dl/officialaccounts";

    String url5 = "weixin://dl/businessWebview/link?url="+url3;

    String url6 = "weixin://dl/businessWebview/link?appid=wx6fb989854b5583ed&url=" + url + "#Intent;package=com.tencent.mm;scheme=weixin;i.translate_link_scene=1;end;";


    public void shareToWechat() {

        Intent intent = null;
        try {
            intent = Intent.parseUri(url6, 1);
            intent.addCategory("android.intent.category.BROWSABLE");
            intent.setComponent((ComponentName)null);
            startActivity(intent);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public void openWeChat() {
        try {
            Intent intent = getPackageManager().getLaunchIntentForPackage(var1);
            startActivity(intent);
        } catch (Exception var2) {
            showToast(this, "打开微信失败，请检查您是否安装了微信！");
        }
    }
}
