package com.replay.limty.model.wxgzh;

import android.content.Context;
import android.content.Intent;

/**
 * Created by Administrator on 2017/5/10 0010.
 */

public class GzhTools {

    public static final String TAG = "公众号";

    public static void getCode(Context context) {
        String url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx6fb989854b5583ed&redirect_uri=http%3a%2f%2fgame.yunzmei.com&response_type=code&scope=snsapi_base&state=STATE#wechat_redirect";

        String url6 = "weixin://dl/businessWebview/link/?appid=wxc295afc02b80fa45&url=" + url;

        String url7 = "weixin://dl/moments";

        String var1 = "com.tencent.mm";
        String var2 = "com.tencent.mm.plugin.webview.ui.tools.WebViewUI";

        String var10 = "intent://dl/businessWebview/link/?appid=wx6fb989854b5583ed&url=" + url + "#Intent;package=com.tencent.mm;scheme=weixin;i.translate_link_scene=1;end";

        try {
            Intent var11 = Intent.parseUri(url7, 1);
            var11.addCategory("android.intent.category.BROWSABLE");
            var11.setComponent(null);
            var11.putExtra("key_package_signature","9e9bf2d907b640d2926384837d5e9092");
            context.startActivity(var11);
        } catch (Exception var12) {

            return;
        }
    }
}
