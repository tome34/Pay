package com.replay.limty.model.zfbqr;

import android.content.Context;
import android.content.Intent;
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
 * Created by Administrator on 2017/4/21 0021.
 */

public class ZfbQrRequest extends AsyncData implements PaybusInterface {

    public static ZfbQrRequest instance;

    private ZfbQrRequest() {
        super();
    }

    public static ZfbQrRequest getInstance() {
        if (instance == null) {
            instance = new ZfbQrRequest();
        }
        return instance;
    }

    @Override
    public void pay(Context context, String body, String orderNumber, String money, String attach, String payType, PayCallback callBack) {
        initData(context, body, orderNumber, money, attach, payType, callBack);
        //检查
        if (TestPay.getInstance().checkInfo(body, orderNumber, money)) {
            try {
                ServiceRequst.servicePay(mContext, TestPay.appID, TestPay.partnerID, payType, orderNumber, body, attach, money,handler);
               // ServiceRequst.servicePay(mContext, TestPay.appID, TestPay.partnerID, payType, orderNumber, body, attach, money, Tools.getHostIP(), handler);
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
                    requestzfbPay(body);
                }
            }
        }
    };

    private void requestzfbPay(String payParams) {
        if (payParams != null && !"".equals(payParams)) {
            try {
                JSONObject result = new JSONObject(payParams);
                if (result.optString("resultCode").equals("0")) {
                    try {
                        JSONObject obj = result.optJSONObject("data");
                        if (!TextUtils.isEmpty(obj.optString("code_img_url"))) {
                            TestPay.getInstance().executeTask(); //下单成功
                            Intent intent = new Intent();
                            intent.setClass(mContext, ZfbQrActivity.class);
                            intent.putExtra("body", obj.toString());
                            mContext.startActivity(intent);
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
