package com.replay.limty.model.wxgzh;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.replay.limty.control.PaybusInterface;
import com.replay.limty.control.TestPay;
import com.replay.limty.model.common.AsyncData;
import com.replay.limty.control.PayCallback;
import com.replay.limty.model.common.ServiceRequst;

import org.json.JSONObject;

/**
 * Created by Administrator on 2017/4/15 0015.
 */

public class GzhRequest extends AsyncData implements PaybusInterface {

    public static GzhRequest instance;

    private GzhRequest() {
        super();
    }

    public static GzhRequest getInstance() {
        if (instance == null) {
            instance = new GzhRequest();
        }
        return instance;
    }

    @Override
    public void pay(Context context, String body, String orderNumber, String money, String attach, String payType,final PayCallback callBack) {
        initData(context, body, orderNumber, money, attach, payType, callBack);
        if (TestPay.getInstance().checkInfo(body, orderNumber, money)) {
            sendRequest(body, orderNumber, money, attach, payType);
        }
    }

    private void sendRequest(String body, String orderNumber, String money, String attach, String payType) {
        try {
            //GzhRequest.request(mContext,"","7551000001","9d101c97133837e13dde2d32a5054abb");
            ServiceRequst.servicePay(mContext,TestPay.appID, TestPay.partnerID,payType, orderNumber, body, attach, money,handler);
        } catch (Exception e) {
            e.printStackTrace();
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
                if (result.optString("resultCode").equalsIgnoreCase("0")) {
                    try {
                        JSONObject obj = result.optJSONObject("data");
                        if (!TextUtils.isEmpty(obj.optString("token_id"))) {
                            //TODO

                        } else {
                            callBack.payResult(3006, "没有获取到token_id");
                        }
                    } catch (Exception var7) {
                        callBack.payResult(3003, "上下文非法");
                    }
                } else {
                    TestPay.getInstance().repairOrder();
                }
            } catch (Exception e) {
                callBack.payResult(3006, "预下单数据解析异常");
            }
        } else {
            callBack.payResult(3002, "下单失败，订单不合法");
        }
    }
}
