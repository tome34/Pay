package com.replay.limty.model.wxgzh;

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
        if (PayRequest.getInstance().checkInfo(body, orderNumber, money)) {
            sendRequest(body, orderNumber, money, attach, payType);
        }
    }

    private void sendRequest(String body, String orderNumber, String money, String attach, String payType) {
        try {
            ServiceRequst.servicePay(mContext, PayRequest.appID, PayRequest.partnerID,payType, orderNumber, body, attach, money,handler);
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
                        String tokenID = obj.optString("token_id");
                        if (!TextUtils.isEmpty(tokenID)) {
                            //TODO
                            String url = "https://pay.swiftpass.cn/pay/jspay?token_id="+tokenID+"&showwxtitle=1";
                            String url6 = "intent://dl/businessWebview/link?appid=wx6fb989854b5583ed&url=" + url + "#Intent;package=com.tencent.mm;scheme=weixin;i.translate_link_scene=1;end;";
                            Intent intent = Intent.parseUri(url6, 1);
                            intent.setAction(Intent.ACTION_VIEW);
                            intent.addCategory("android.intent.category.BROWSABLE");
                            intent.setComponent(null);
//                            Intent intent = new Intent();
//                            intent.setClass(mContext,ActivityH5.class);
//                            intent.putExtra("uri",url);
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
