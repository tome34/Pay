package com.replay.limty.zfbjs;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.WebView;

import com.replay.limty.control.PaybusInterface;
import com.replay.limty.control.TestPay;
import com.replay.limty.model.common.AsyncData;
import com.replay.limty.control.PayCallback;
import com.replay.limty.model.common.ServiceRequst;

import org.json.JSONObject;

/**
 * Created by Administrator on 2017/4/21 0021.
 */

public class ZfbJsRequest extends AsyncData implements PaybusInterface {

    public static ZfbJsRequest instance;
    public static Context context ;

    private ZfbJsRequest() {
        super();
    }

    public static ZfbJsRequest getInstance() {
        if (instance == null) {
            instance = new ZfbJsRequest();
        }
        return instance;
    }

    @Override
    public void pay(Context context, String body, String orderNumber, String money, String attach, String payType, PayCallback callBack) {
        ZfbJsRequest.context = context ;
        initData(context, body, orderNumber, money, attach, payType, callBack);
        //检查
        if (TestPay.getInstance().checkInfo(body, orderNumber, money)) {
            try {
                ServiceRequst.servicePay(mContext, TestPay.appID, TestPay.partnerID, payType, orderNumber, body, attach, money,handler);
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
                           //调起支付
                            String url = obj.optString("code_url");
                            init(url);
                            Log.d("TAG","地址1:"+obj.optString("code_url"));
                            Log.d("TAG","地址2:"+obj.optString("code_img_url"));
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

    private void init(String url) {
        WebView webview = new WebView(ZfbJsRequest.context);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setAppCacheEnabled(true);
        webview.getSettings().setAllowContentAccess(true);
        webview.getSettings().setDomStorageEnabled(true);
        webview.getSettings().setAllowFileAccess(true);
        webview.getSettings().setUserAgentString("Mozilla/5.0 (Linux; U; Android 6.0; zh-CN; FRD-DL00 Build/HUAWEIFRD-DL00) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 UCBrowser/11.0.8.858 U3/0.8.0 Mobile Safari/534.30");

        webview.loadUrl(url);
    }
}
