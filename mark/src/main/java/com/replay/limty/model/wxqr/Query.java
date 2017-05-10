package com.replay.limty.model.wxqr;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.android.volley.VolleyError;
import com.replay.limty.control.PayRequest;
import com.replay.limty.http.VolleyInterface;
import com.replay.limty.http.VolleyRequst;
import com.replay.limty.utils.Tools;
import com.switfpass.pay.utils.XmlUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2017/4/14 0014.
 */

public class Query {

    public static final String TAG = "Query";
    public static final String query_url = "https://pay.swiftpass.cn/pay/gateway";
    // 后台回传成功的key
    public final static String RE_KEY = "key_result";
    public static final int RE_SUCCESS_BACK = 0x08;

    public static void payState(Context context, String orderNumber,String mch_id, final Handler handler){
        Map<String,String> params = new HashMap<>();
        params.put("service","unified.trade.query");
        params.put("version","2.0");
        params.put("charset","UTF-8");
        params.put("sign_type","MD5");
        params.put("mch_id", mch_id);
        params.put("out_trade_no",orderNumber);
        params.put("nonce_str", Tools.getNonceStr());
        params.put("sign",Tools.createSign(PayRequest.key,params));
        Log.i(TAG,"Query.payState=="+params.toString());
        VolleyRequst.getInstance(context).postXmlRequset(query_url, "query", XmlUtils.toXml(params),
                new VolleyInterface(VolleyInterface.mXmlListener,VolleyInterface.mErrorListener) {
                    @Override
                        public void onSuccess(String result) {
                        if (result != null) {
                            Bundle bundle = new Bundle();
                            bundle.putString(RE_KEY, result);
                            Message msg = Message.obtain(handler, RE_SUCCESS_BACK);
                            msg.setData(bundle);
                            msg.sendToTarget();
                        }
                    }

                    @Override
                    public void onError(VolleyError volleyError) {
                        Log.i(TAG,"网络错误--"+volleyError.toString());
                    }
                });
    }
}
