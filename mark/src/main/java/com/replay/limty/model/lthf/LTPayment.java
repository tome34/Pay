package com.replay.limty.model.lthf;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.replay.limty.model.wxapp.ServiceRequst;

import org.json.JSONException;

/**
 * Created by Administrator on 2017/4/21 0021.
 */

public class LTPayment {

    private static final String TAG = "LTPayment";

    public static void pay(Context context, String phone, String goodsName, String goodsId,
                    String orderNumber, String money){
        try {
            LiantongPay.requst(context,phone,goodsName,goodsId,orderNumber,money,handler);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private static Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == ServiceRequst.RE_SUCCESS_BACK) {
                Bundle bundle = msg.getData();
                String body = bundle.getString(ServiceRequst.RE_KEY);
                Log.i(TAG, "handleMessage: "+body);
            }
        }
    };
}
