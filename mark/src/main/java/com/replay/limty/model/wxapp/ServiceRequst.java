package com.replay.limty.model.wxapp;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.android.volley.VolleyError;
import com.carch.ndkdemo.GetString;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.replay.limty.http.VolleyInterface;
import com.replay.limty.http.VolleyRequst;
import com.replay.limty.utils.DesHelper;
import com.replay.limty.utils.Tools;
import com.replay.limty.utils.keyTools;
import com.switfpass.pay.utils.MD5;
import com.switfpass.pay.utils.SignUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import it.sauronsoftware.base64.Base64;

/**
 * Created by Administrator on 2017/4/14 0014.
 */

public class ServiceRequst {

    private static final String service_url = "http://43.247.68.114:8081/main/preorder";
    public final static String RE_KEY = "key_result";
    public static final int RE_SUCCESS_BACK = 0x08;
    private static final String notify_url = "http://202.103.190.89:50/WXPAY/RcvMo.sy";

    public static void servicePay(Context context, String appID, String partnerId, String payType,
                                String orderNumber, String body, String attach, String money, String phoneIp, final Handler handler) throws JSONException {
        JSONObject params = new JSONObject();
        String data = "";
        String up = "";
        params.put("appId",appID);
        params.put("partnerId",partnerId);
        params.put("out_trade_no",orderNumber);
        params.put("totalFee",money);
        params.put("body",body);
        params.put("sdkVersion", GetString.getVersion());
        params.put("detail","123");
        params.put("attach",attach);
        params.put("mch_create_ip",phoneIp);
        params.put("deviceName", Tools.getPhoneModel());
        params.put("deviceCode",Tools.getAndroidVersion());
        params.put("imei",Tools.getIMEI(context));
        params.put("imsi",Tools.getIMSI(context));
        params.put("iccid",Tools.getICCID(context));
        params.put("macAddress",Tools.getMacAddress());
        params.put("version","2.0");
        params.put("payType",payType);
        params.put("timeStamp",Tools.getCurrentTime());
        params.put("notify_url",notify_url);
        params.put("sign",createSign(GetString.getInstance().getKey(),params.toString()));
        Log.i("测试","ServiceRequst.params=="+params.toString());
        String time = Tools.getTime();
        String key = keyTools.getKey(time);
        try {
            data = DesHelper.toHexString(DesHelper.encrypt(params.toString(),key));
            String username = DesHelper.toHexString(DesHelper.encrypt("QHYJ_USER_20170415",key));
            String password = DesHelper.toHexString(DesHelper.encrypt("QHYJ_PwD@20170415#15:03",key));
            up = "Basic "+ Base64.encode(username +":"+ password);
        } catch (Exception e) {
            e.printStackTrace();
        }

        JSONObject obj = new JSONObject();
        obj.put("data",data);
        obj.put("time",time);
        obj.put("up",up);
        VolleyRequst.getInstance(context).postJosnRequsts(service_url,"url", obj, new VolleyInterface(
                VolleyInterface.mListener,VolleyInterface.mJsonListener,VolleyInterface.mErrorListener
        ) {
            @Override
            public void onSuccess(String result) {
               Log.i("测试","postJosnRequsts.onSuccess=="+result);
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
                Log.i("测试","postJosnRequsts.volleyError=="+volleyError.toString());
            }
        });
    }

    private static String createSign(String signKey, String jsonStr){
        Gson gson = new Gson();
        Map<String,Object> params =  gson.fromJson(jsonStr, new TypeToken<HashMap<String,Object>>(){}.getType());
        StringBuilder buf = new StringBuilder((params.size() + 1) * 10);
        SignUtils.buildPayParams(buf, params, false);
        buf.append("&key=").append(signKey);
        String preStr = buf.toString();
        String sign = "";
        try{
            sign = MD5.md5s(preStr).toUpperCase();
        }catch (Exception e){
            sign = MD5.md5s(preStr).toUpperCase();
        }
        return sign;
    }
}
