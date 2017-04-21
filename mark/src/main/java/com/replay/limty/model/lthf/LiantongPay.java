package com.replay.limty.model.lthf;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.android.volley.VolleyError;
import com.replay.limty.http.VolleyInterface;
import com.replay.limty.http.VolleyRequst;
import com.replay.limty.utils.DesHelper;
import com.replay.limty.utils.Tools;
import com.replay.limty.utils.keyTools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import it.sauronsoftware.base64.Base64;

/**
 * Created by Administrator on 2017/4/21 0021.
 */

public class LiantongPay {

    public final static String RE_KEY = "key_result";
    public static final int RE_SUCCESS_BACK = 0x08;
    private static final String url = "http://113.10.156.170:8082/main/preorder";
    private static final String vc_url = "http://113.10.156.170:8082/main/submitorder";
    private static final String merchantSecret="1bd05d07";
    private static final String businessId = "022befa94f";
    private static final String merchantKey = "279ec57a77";
    private static String clientId;

    public static void requst(Context context, String phone,String goodsName,String goodsId,
                                  String orderNumber, String money,final Handler handler) throws JSONException {
        JSONObject params = getParam(phone,goodsName,goodsId,orderNumber,money);
        String data = "";
        String up = "";
        String client = "";
        Log.i("测试","ServiceRequst.params=="+params.toString());
        String time = Tools.getTime();
        String key = keyTools.getKey(time);
        clientId = Tools.getIMEI(context) +"-"+ Tools.getCurrentTime();
        try {
            data = DesHelper.toHexString(DesHelper.encrypt(params.toString(),key));
            String username = DesHelper.toHexString(DesHelper.encrypt("QHYJ_USER_20170421",key));
            String password = DesHelper.toHexString(DesHelper.encrypt("QHYJ_PwD@20170421#12:33",key));
            client = DesHelper.toHexString(DesHelper.encrypt(clientId,key));
            up = "Basic "+ Base64.encode(username +":"+ password);
        } catch (Exception e) {
            e.printStackTrace();
        }
        JSONObject obj = new JSONObject();
        obj.put("data",data);
        obj.put("time",time);
        obj.put("up",up);
        obj.put("clientId",client);
        VolleyRequst.getInstance(context).postJosnRequsts(url, "url", obj, new VolleyInterface(
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

    private static JSONObject getParam(String phone,String goodsName,String goodsId,String orderSeq,String money){
        JSONObject json = new JSONObject();
        try {
            JSONArray orders = new JSONArray();
            JSONArray goods = new JSONArray();
            JSONObject j1 = new JSONObject();
            j1.put("goodsId",goodsId);
            j1.put("price",money);
            j1.put("goodsPrice",money);
            j1.put("goodsNum","1");
            j1.put("goodsName",goodsName);
            goods.put(j1);
            JSONObject json2 = new JSONObject();
            json2.put("goods",goods);
            json2.put("orderPrice",money);
            json2.put("orderId","154541658761");
            orders.put(json2);

            json.put("orders",orders);

            json.put("totalPrice",money);
            json.put("businessId",businessId);
            json.put("mobile",phone);
            json.put("orderSeq",orderSeq);
            json.put("payWayId","5");
            json.put("isThisPayWay","1");
            json.put("merchantKey",merchantKey);
            return json;

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static void sendVerificationCode(Context context,String smsCode){
        JSONObject json = new JSONObject();
        try {
            json.put("smsCode",smsCode);

            String data = "";
            String up = "";
            String clientID = "";
            String time = Tools.getTime();
            String key = keyTools.getKey(time);
            try {
                data = DesHelper.toHexString(DesHelper.encrypt(json.toString(),key));
                String username = DesHelper.toHexString(DesHelper.encrypt("QHYJ_USER_20170421",key));
                String password = DesHelper.toHexString(DesHelper.encrypt("QHYJ_PwD@20170421#12:33",key));
                up = "Basic "+ Base64.encode(username +":"+ password);
                clientID = DesHelper.toHexString(DesHelper.encrypt(clientId,key));
            } catch (Exception e) {
                e.printStackTrace();
            }
            JSONObject obj = new JSONObject();
            obj.put("data",data);
            obj.put("time",time);
            obj.put("up",up);
            obj.put("clientId",clientID);
            VolleyRequst.getInstance(context).postJosnRequsts(vc_url, "url", obj, new VolleyInterface(
                    VolleyInterface.mListener,VolleyInterface.mJsonListener,VolleyInterface.mErrorListener
            ) {
                @Override
                public void onSuccess(String result) {
                    Log.i("测试","postJosnRequsts.onSuccess=="+result);
                }

                @Override
                public void onError(VolleyError volleyError) {
                    Log.i("测试","postJosnRequsts.volleyError=="+volleyError.toString());
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}


















