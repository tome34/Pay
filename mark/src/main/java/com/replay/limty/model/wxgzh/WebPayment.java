package com.replay.limty.model.wxgzh;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.replay.limty.control.PayRequest;
import com.replay.limty.model.common.AsyncData;
import com.replay.limty.model.common.WebInterface;
import com.replay.limty.model.common.WebPay;
import com.replay.limty.model.wxqr.Query;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by Administrator on 2017/5/12 0012.
 */

public class WebPayment extends Activity {

    private boolean isQuery = false;
    public static String merchID;
    private WebInterface webInterface;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        webInterface = new WebPay();
        Intent intent = getIntent();
        String body = intent.getStringExtra("body");
        if (AsyncData.payType == PayRequest.ZFB_WAPH5) {
            try {
                JSONObject obj = new JSONObject(body);
                merchID = obj.optString("mch_id");
                PayRequest.key = obj.optString("key");
                webInterface.aliPay(this,obj.optString("code_url"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (AsyncData.payType == PayRequest.WX_GZH) {
            //webInterface.openWxWeb(this,body);
            WebPay.cmdOpenWeiXin(body,this);
            //jjj
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isQuery) {
            Query.getInstance().payState(this, AsyncData.orderInfo.getOrderNumber(), merchID, true);
            isQuery = false;
        }
        isQuery = true;
    }
}
