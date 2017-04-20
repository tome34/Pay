package com.replay.limty.model.wxgzh;

import android.content.Context;
import android.util.Log;

import com.android.volley.VolleyError;
import com.replay.limty.http.VolleyInterface;
import com.replay.limty.http.VolleyRequst;
import com.replay.limty.utils.Tools;
import com.switfpass.pay.utils.XmlUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2017/4/20 0020.
 */

public class GzhRequest {

    private static final String TAG = "GzhRequest";
    public static final String h5_url = "https://pay.swiftpass.cn/pay/gateway";
    public final static String RE_KEY = "key_result";
    public static final int RE_SUCCESS_BACK = 0x08;

    public static void request(final Context context, String openID, String mchID, String key) {
        Map<String, String> map = new HashMap<>();
        map.put("service", "pay.weixin.jspay");
        map.put("is_raw", "0");
        map.put("mch_id", mchID);
        map.put("body", "公众号测试");
        map.put("out_trade_no", Tools.creatOrderNumber());
        map.put("total_fee", "1");
        map.put("mch_create_ip", Tools.getHostIP());
        map.put("notify_url", "http://202.103.190.89:50/WXPAY/RcvMo.sy");
        map.put("nonce_str", Tools.getNonceStr());
        map.put("sign", Tools.createSign(key, map));
        Log.i(TAG, "h5Request.参数: " + map.toString());
        VolleyRequst.getInstance(context).postXmlRequset(h5_url, "h5", XmlUtils.toXml(map), new VolleyInterface(VolleyInterface.mXmlListener, VolleyInterface.mErrorListener) {
            @Override
            public void onSuccess(String result) {
                Log.i(TAG, "h5Request.onSuccess: " + result);

                HashMap parse = XmlUtils.parse(result);
                String token = (String) parse.get("token_id");

                String url = "https://pay.swiftpass.cn/pay/jspay?token_id=" + token + "&showwxtitle=1";
                String appID = (String) parse.get("appid");



            }

            @Override
            public void onError(VolleyError volleyError) {
                Log.i(TAG, "h5Request.onError: " + volleyError.toString());
            }
        });
    }
}
