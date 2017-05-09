package com.replay.limty.model.common;

import android.content.Context;
import android.util.Log;

import com.android.volley.VolleyError;
import com.carch.ndkdemo.GetString;
import com.replay.limty.control.PayCallback;
import com.replay.limty.control.PayRequest;
import com.replay.limty.http.VolleyInterface;
import com.replay.limty.http.VolleyRequst;
import com.replay.limty.utils.Pref;
import com.replay.limty.utils.Tools;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2017/4/17 0017.
 */

public class AsyncData {

    public static final String TAG = "同步数据";
    public static AsyncData instance;
    public static OrderInfo orderInfo;
    public static String attach = "";
    public static String payType = "";
    public Context mContext;
    public static PayCallback callBack;

    protected AsyncData(){

    }

    public static AsyncData getInstance(){
        if(instance == null){
            instance = new AsyncData();
        }
        return instance;
    }

    public void initData(final Context context,String body, String orderNumber, String money, String attach,String payType, final PayCallback callBack){
        this.mContext = context;
        orderInfo = OrderInfo.getInstance();
        orderInfo.setBody(body);
        orderInfo.setOrderNumber(orderNumber);
        orderInfo.setMoney(money);
        this.attach = attach;
        this.payType = payType;
        sendPaymentState(3000, context);
        this.callBack = new PayCallback() {
            @Override
            public void payResult(int returnCode, String message) {
                callBack.payResult(returnCode, message);
                sendPaymentState(returnCode, context);
                AsyncData.this.callBack = null;
            }
        };
    }

    public void sendPaymentState(int payStateCode,Context context){
        JSONObject object = new JSONObject();
        String defulUrl = "http://202.103.190.89:50/";
        String url = Pref.with(context).read("merchUrl", defulUrl)+"APPpayStateCode/RcvMo.sy";
        try {
            object.put("payStateCode", payStateCode);
            object.put("appId", PayRequest.appID);
            object.put("allocationID", PayRequest.partnerID);
            object.put("merchID", GetString.getInstance().getMch());
            object.put("commodity", orderInfo.getBody());
            object.put("orderNumer", orderInfo.getOrderNumber());
            object.put("money", orderInfo.getMoney());
            object.put("sdkVersion", GetString.getVersion());
            object.put("appVersion", Tools.getVersionNumber(context));
            object.put("mobile_model", Tools.getPhoneModel());
            object.put("android_version", Tools.getAndroidVersion());
            object.put("mobile_imei", Tools.getIMEI(context));
            object.put("mobile_phone", Tools.getPhoneNumber(context));
            object.put("mobile_imsi", Tools.getIMSI(context));
            object.put("mobile_iccid", Tools.getICCID(context));
            object.put("client_ip", Tools.getHostIP());
            object.put("payType",payType);
            Log.i(TAG, "url"+url+"参数=="+object.toString());
            VolleyRequst.getInstance(context).postJosnRequst(url, "PaymentState", object, new VolleyInterface(
                    null,VolleyInterface.mJsonListener,VolleyInterface.mErrorListener) {

                @Override
                public void onSuccess(String result) {
                    Log.i(TAG, result);
                }

                @Override
                public void onError(VolleyError volleyError) {
                    Log.i(TAG, volleyError.toString());
                }
            });
        } catch (JSONException e) {
            Log.i(TAG, e.toString());
            e.printStackTrace();
        }
    }
}
