package com.replay.limty.model.wxgzh;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

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
    public static ProgressDialog progressDialog = null;

    public GzhRequest() {
        super();
    }

    public static GzhRequest getInstance() {
        if (instance == null) {
            instance = new GzhRequest();
        }
        return instance;
    }

    @Override
    public void pay(Context context, String body, String orderNumber, String money, String attach, String payType, final PayCallback callBack) {
        initData(context, body, orderNumber, money, attach, payType, callBack);
        if (PayRequest.getInstance().checkInfo(body, orderNumber, money)) {
            sendRequest(body, orderNumber, money, attach, payType);
        }
    }

    private void sendRequest(String body, String orderNumber, String money, String attach, String payType) {
        try {
            showDialog(mContext,true);
            ServiceRequst.servicePay(mContext, PayRequest.channelCode, PayRequest.partnerID, payType, orderNumber, body, attach, money, handler);
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
                Log.i(TAG, "handleMessage 1:"+body);
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
                    String string = result.optString("data");
                    Log.i(TAG, "2  :"+ string);
                    requstService(string);
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

    private void requstService(String string) {
        try {
            Intent intent = new Intent(mContext,WebPayment.class);
            intent.putExtra("body",string);
            mContext.startActivity(intent);
            Thread.sleep(200);
            if(progressDialog != null){
                progressDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showDialog(Context context,boolean isShow){
        if(isShow){
            progressDialog = new ProgressDialog(context);
            progressDialog.setProgressStyle(0);
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("正在跳转到微信支付，请稍等...");
            progressDialog.show();
        }
    }
}
