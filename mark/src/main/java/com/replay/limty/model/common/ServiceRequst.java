package com.replay.limty.model.common;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.android.volley.VolleyError;
import com.replay.limty.coding.Base64;
import com.replay.limty.control.PayRequest;
import com.replay.limty.http.VolleyInterface;
import com.replay.limty.http.VolleyRequst;
import com.replay.limty.utils.DesHelper;
import com.replay.limty.utils.Pref;
import com.replay.limty.utils.Tools;
import com.replay.limty.utils.keyTools;
import com.switfpass.pay.utils.MD5;
import com.switfpass.pay.utils.SignUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @internal
 * Created by Administrator on 2017/4/14 0014.
 */

public class ServiceRequst {

//    private static final String service_url = "http://43.247.68.114:8081/main/preorder";
    private static final String service_url = "http://43.247.68.109:8084/main/preorder";
    public final static String RE_KEY = "key_result";
    public static final int RE_SUCCESS_BACK = 0x08;
    private static final String notify_url = "http://202.103.190.89:50/WXPAY/RcvMo.sy";
    public static final String SDK_VERSION = "11.23.90.53";

    public static void servicePay(Context context, String channelCode, String partnerId, String payType,
                                  String orderNumber, String body, String attach, String money,
                                  final Handler handler) throws JSONException {
        JSONObject params = new JSONObject();
        String data = "";
        String up = "";
        params.put("channelCode", channelCode);
        params.put("partnerId", partnerId);
        params.put("out_trade_no", orderNumber);
        params.put("totalFee", money);
        params.put("body", body);
        params.put("sdkVersion", SDK_VERSION);
        params.put("detail", "123");
        params.put("attach", attach);
        params.put("mch_create_ip", Tools.getHostIP());
        params.put("deviceName", Tools.getPhoneModel());
        params.put("deviceCode", Tools.getAndroidVersion());
        params.put("imei", Tools.getIMEI(context));
        params.put("imsi", Tools.getIMSI(context));
        params.put("iccid", Tools.getICCID(context));
        params.put("macAddress", Tools.getMacAddress());
        params.put("version", "2.0");
        params.put("payType", payType);
        params.put("timeStamp", Tools.getCurrentTime());
        params.put("notify_url", notify_url);

        params.put("callback_url", "");
        params.put("sub_openid", "oHoG71Dt8-O8j2Z4JwY_TVCajVb4");
        params.put("device_info", "16359634586");
        params.put("goods_tag", "10000");
        params.put("sign", createSign(PayRequest.key, params.toString()));
        String time = Tools.getTime();
        String key = keyTools.getKey(time);
        Pref.with(context).write("key",key);
        try {
            data = DesHelper.toHexString(DesHelper.encrypt(params.toString(), key));
            String username = DesHelper.toHexString(DesHelper.encrypt("QHYJ_USER_20170415", key));
            String password = DesHelper.toHexString(DesHelper.encrypt("QHYJ_PwD@20170415#15:03", key));
            up = "Basic " + Base64.encode(username + ":" + password);
        } catch (Exception e) {
            e.printStackTrace();
        }

        JSONObject obj = new JSONObject();
        obj.put("data", data);
        obj.put("time", time);
        obj.put("up", up);
        VolleyRequst.getInstance(context).postJosnRequsts(service_url, "url", obj, new VolleyInterface(
                VolleyInterface.mListener, VolleyInterface.mJsonListener, VolleyInterface.mErrorListener
        ) {
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
                Log.i("测试", "postJosnRequsts.volleyError==" + volleyError.toString());
            }
        });
    }

    private static String createSign(String signKey, String jsonStr) {
        Map<String, Object> params = jsonToMap(jsonStr);
        StringBuilder buf = new StringBuilder((params.size() + 1) * 10);
        SignUtils.buildPayParams(buf, params, false);
        buf.append("&key=").append(signKey);
        String preStr = buf.toString();
        String sign = "";
        try {
            sign = MD5.md5s(preStr).toUpperCase();
        } catch (Exception e) {
            sign = MD5.md5s(preStr).toUpperCase();
        }
        return sign;
    }

    private static Map jsonToMap(String jsonStr) {
        JSONObject jsonObj = null;
        try {
            jsonObj = new JSONObject(jsonStr);
            Iterator<String> nameItr = jsonObj.keys();
            String name;
            Map<String, String> outMap = new HashMap<>();
            while (nameItr.hasNext()) {
                name = nameItr.next();
                outMap.put(name, jsonObj.getString(name));
            }
            return outMap;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
