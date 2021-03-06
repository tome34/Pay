package com.replay.limty.model.wxh5;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.VolleyError;
import com.replay.limty.control.PayCallback;
import com.replay.limty.control.PayRequest;
import com.replay.limty.control.PaybusInterface;
import com.replay.limty.http.VolleyInterface;
import com.replay.limty.http.VolleyRequst;
import com.replay.limty.model.common.AsyncData;
import com.replay.limty.model.common.ServiceRequst;
import com.replay.limty.utils.Tools;
import com.switfpass.pay.utils.XmlUtils;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2017/4/18 0018.
 */

public class H5Request extends AsyncData implements PaybusInterface {

    private static final String TAG = "H5Request";
    public static final String h5_url = "https://pay.swiftpass.cn/pay/gateway";

    public static H5Request instance;

    private H5Request() {
        super();
    }

    public static H5Request getInstance() {
        if (instance == null) {
            instance = new H5Request();
        }
        return instance;
    }

    @Override
    public void pay(Context context, String body, String orderNumber, String money, String attach, String payType, PayCallback callBack) {
        initData(context, body, orderNumber, money, attach, payType, callBack);
        if (PayRequest.getInstance().checkInfo(body, orderNumber, money)) {
            try {
                ServiceRequst.servicePay(mContext, PayRequest.channelCode, PayRequest.partnerID, payType, orderNumber, body, attach, money, handler);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == ServiceRequst.RE_SUCCESS_BACK) {
                Bundle bundle = msg.getData();
                String body = bundle.getString(ServiceRequst.RE_KEY);
                if (body != null) {
                    requestWxPay(body);
                }
            }
        }
    };

    private void requestWxPay(String payParams) {
        if (payParams != null && !"".equals(payParams)) {
            try {
                JSONObject result = new JSONObject(payParams);
                if (result.optString("resultCode").equals("0")) {
                    try {
                        JSONObject obj = result.optJSONObject("data");
                        if (!TextUtils.isEmpty(obj.optString("code_img_url"))) {
                            PayRequest.getInstance().executeTask();

                        } else {
                            callBack.payResult(3006, "没有获取到token_id");
                        }
                    } catch (Exception var7) {
                        callBack.payResult(3003, "上下文非法");
                    }
                } else {
                    PayRequest.getInstance().repairOrder();
                }
            } catch (Exception e) {
                callBack.payResult(3006, "预下单数据解析异常");
            }
        } else {
            callBack.payResult(3002, "下单失败，订单不合法");
        }
    }


    public static void h5Request(final Context context, String mchID, String mchKey, String money) {
        String params = getParams(mchID, mchKey, money);
        VolleyRequst.getInstance(context).postXmlRequset(h5_url, "h5", params, new VolleyInterface(VolleyInterface.mXmlListener, VolleyInterface.mErrorListener) {
            @Override
            public void onSuccess(String result) {

                HashMap map = XmlUtils.parse(result);
                Log.i(TAG, "h5Request.onSuccess: " + map.toString());
                if (map.get("status").toString().equalsIgnoreCase("0")) {
                    try {

                    }catch (Exception e){

                    }
                }
            }

            @Override
            public void onError(VolleyError volleyError) {
                Log.i(TAG, "h5Request.onError: " + volleyError.toString());
            }
        });
    }

    private static String getParams(String mchID, String mchKey, String money) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("service", "pay.weixin.jspay");
        params.put("version", "2.0");
        params.put("mch_id", mchID);
        params.put("out_trade_no", Tools.creatOrderNumber());
        params.put("body", "测试");
        params.put("is_raw", "0");
        params.put("attach", "abc665");
        params.put("sub_openid", "oqlnO1dyCni2WDcmMcg4FAqFG61g");
        params.put("total_fee", money);
        params.put("mch_create_ip", "14.20.89.73");
        params.put("notify_url", "http://202.103.190.89:50/WXPAY/RcvMo.sy");
        params.put("nonce_str", Tools.getNonceStr());
        params.put("limit_credit_pay", "0");
        params.put("sign", Tools.createSign(mchKey, params));
        return XmlUtils.toXml(params);
    }
}
