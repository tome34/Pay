package com.replay.limty.model.wxqr;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.replay.limty.control.PayCallback;
import com.replay.limty.control.PayRequest;
import com.replay.limty.control.PaybusInterface;
import com.replay.limty.model.common.AsyncData;
import com.replay.limty.model.common.ServiceRequst;
import com.replay.limty.utils.DesHelper;
import com.replay.limty.utils.Pref;

import org.json.JSONObject;

/**
 * Created by Administrator on 2017/4/21 0021.
 */

public class QrRequest extends AsyncData implements PaybusInterface {

    public static QrRequest instance;

    private QrRequest() {
        super();
    }

    public static QrRequest getInstance() {
        if (instance == null) {
            instance = new QrRequest();
        }
        return instance;
    }

    @Override
    public void pay(Context context, String body, String orderNumber, String money, String attach, String payType, PayCallback callBack) {
        initData(context, body, orderNumber, money, attach, payType, callBack);
        if (PayRequest.getInstance().checkInfo(body, orderNumber, money)) {
            try {
                ServiceRequst.servicePay(mContext, PayRequest.channelCode, PayRequest.partnerID, payType, orderNumber, body, attach, money,handler);
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
                JSONObject obj = new JSONObject(payParams);
                if (obj.optString("resultCode").equals("0")) {
                    try {
                        String string = DesHelper.decrypt(obj.optString("data"), Pref.with(mContext).read("key",""));
                        JSONObject result = new JSONObject(string);
                        if (!TextUtils.isEmpty(result.optString("code_img_url"))) {
                            PayRequest.getInstance().executeTask();
                            Intent intent = new Intent();
                            intent.setClass(mContext, ShowActivity.class);
                            intent.putExtra("body", result.toString());
                            mContext.startActivity(intent);
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
}
