package com.replay.limty.model.zfbh5;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.replay.limty.control.PayCallback;
import com.replay.limty.control.PayRequest;
import com.replay.limty.control.PaybusInterface;
import com.replay.limty.model.common.AsyncData;
import com.replay.limty.model.common.ServiceRequst;

import org.json.JSONObject;

/**
 * Created by Administrator on 2017/4/21 0021.
 */

public class ZfbH5Request extends AsyncData implements PaybusInterface {

    public static ZfbH5Request instance;
    public static Context context ;

    private ZfbH5Request() {
        super();
    }

    public static ZfbH5Request getInstance() {
        if (instance == null) {
            instance = new ZfbH5Request();
        }
        return instance;
    }

    @Override
    public void pay(Context context, String body, String orderNumber, String money, String attach, String payType, PayCallback callBack) {
        ZfbH5Request.context = context ;
        initData(context, body, orderNumber, money, attach, payType, callBack);
        //检查
        if (PayRequest.getInstance().checkInfo(body, orderNumber, money)) {
            try {
                onProgress();
                ServiceRequst.servicePay(mContext, PayRequest.appID, PayRequest.partnerID, payType, orderNumber, body, attach, money,handler);
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
                    Log.d("TAG","body:"+body);
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
                            PayRequest.getInstance().executeTask(); //下单成功
                           //调起支付
                            String url = obj.optString("code_url");
                            init(url);
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

    private void init(String url) {

        WebView webview = new WebView(ZfbH5Request.context);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setAppCacheEnabled(true);
        webview.getSettings().setAllowContentAccess(true);
        webview.getSettings().setDomStorageEnabled(true);
        webview.getSettings().setAllowFileAccess(true);
        webview.getSettings().setUserAgentString("Mozilla/5.0 (Linux; U; Android 6.0; zh-CN; FRD-DL00 Build/HUAWEIFRD-DL00) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 UCBrowser/11.0.8.858 U3/0.8.0 Mobile Safari/534.30");
        webview.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        webview.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
               if ( url.toLowerCase().startsWith("alipays://") ||(url.toLowerCase().startsWith("weixin://"))) {
                    try {
                        Uri pay_uri = Uri.parse(url);
                        Intent intent = new Intent(Intent.ACTION_VIEW, pay_uri);
                        view.getContext().startActivity(intent);
                        return true;
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(context, "请安装支付宝客户端！", Toast.LENGTH_SHORT).show();
                        // 结束支付页面
                    }
                }
               return super.shouldOverrideUrlLoading(view, url);
            }


            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }
        });
        webview.loadUrl(url);
    }

    private ProgressDialog dialog;

    private void onProgress() {
        dialog =
                ProgressDialog.show(context,
                        "提示:",
                        "正在打开支付宝支付,请稍后!");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (dialog != null)
                {
                    dialog.dismiss();
                }
            }
        }, 3000);
    }
}
