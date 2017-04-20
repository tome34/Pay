package com.replay.limty.model.wxh5;

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
 * Created by Administrator on 2017/4/18 0018.
 */

public class PayRequest {

    private static final String TAG = "GzhRequest";
    public static final String h5_url = "https://api.mch.weixin.qq.com/pay/unifiedorder";
    public final static String RE_KEY = "key_result";
    public static final int RE_SUCCESS_BACK = 0x08;

    public static void h5Request(Context context,String appID, String mchID, String key){
        Map<String,String> map = new HashMap<>();
        map.put("appid",appID);
        map.put("mch_id",mchID);
        map.put("nonce_str", Tools.getNonceStr());
        map.put("body","二维码测试");
        map.put("out_trade_no",Tools.creatOrderNumber());
        map.put("total_fee","1");
        map.put("spbill_create_ip",Tools.getHostIP());
        map.put("notify_url","http://202.103.190.89:50/WXPAY/RcvMo.sy");
        map.put("trade_type","MWEB");
        map.put("product_id","146925368523");//商品ID，商户自行定义。
        map.put("sign",Tools.createSign(key,map));
        Log.i(TAG, "h5Request.参数: "+map.toString());
        VolleyRequst.getInstance(context).postXmlRequset(h5_url, "h5", XmlUtils.toXml(map), new VolleyInterface(VolleyInterface.mXmlListener,VolleyInterface.mErrorListener) {
            @Override
            public void onSuccess(String result) {
                Log.i(TAG, "h5Request.onSuccess: "+result);
            }

            @Override
            public void onError(VolleyError volleyError) {
                Log.i(TAG, "h5Request.onError: "+volleyError.toString());
            }
        });
    }
}
