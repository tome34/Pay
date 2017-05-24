package com.replay.limty.model.wxqr;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

import com.android.volley.VolleyError;
import com.replay.limty.control.PayCallback;
import com.replay.limty.control.PayRequest;
import com.replay.limty.http.VolleyInterface;
import com.replay.limty.http.VolleyRequst;
import com.replay.limty.model.common.AsyncData;
import com.replay.limty.utils.ToastTools;
import com.replay.limty.utils.Tools;
import com.switfpass.pay.utils.XmlUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2017/4/14 0014.
 */

public class Query {

    public static final String query_url = "https://pay.swiftpass.cn/pay/gateway";
    public static ProgressDialog progressDialog = null;
    private PayCallback callback;
    private Activity mActivity;
    private static Query instance;

    private Query(){}

    public static Query getInstance(){
        if(instance == null){
            instance = new Query();
        }
        return instance;
    }

    public void payState(Activity activity, String orderNumber, String mch_id, boolean isShowDialog){
        initData(activity);
        showDialog(activity,isShowDialog);
        Map<String,String> params = new HashMap<>();
        params.put("service","unified.trade.query");
        params.put("version","2.0");
        params.put("charset","UTF-8");
        params.put("sign_type","MD5");
        params.put("mch_id", mch_id);
        params.put("out_trade_no",orderNumber);
        params.put("nonce_str", Tools.getNonceStr());
        params.put("sign",Tools.createSign(PayRequest.key,params));
        Log.i("测试", "onSuccess: "+params.toString());
        VolleyRequst.getInstance(activity).postXmlRequset(query_url, "query", XmlUtils.toXml(params),
                new VolleyInterface(VolleyInterface.mXmlListener,VolleyInterface.mErrorListener) {
                    @Override
                        public void onSuccess(String result) {
                        if (result != null) {
                            Map<String, String> results = XmlUtils.parse(result);
                            handleResult(results);
                        }
                    }

                    @Override
                    public void onError(VolleyError volleyError) {
                    }
                });
    }

    private void initData(Activity activity){
        if (AsyncData.callBack != null) {
            this.callback = AsyncData.callBack;
        }
        this.mActivity = activity;
    }

    private void handleResult(Map<String, String> result) {
        if (result.get("status").equals("0")) {
            if (result.get("result_code").equals("0")) {
                String payState = result.get("trade_state").toUpperCase();
                backResult(payState);
            } else {
                this.callback.payResult(5001, result.get("err_msg"));
                close();
            }
        } else {
            callback.payResult(5002, result.get("message"));
            close();
        }
    }

    private void backResult(String payState) {
        switch (payState) {
            case "SUCCESS":
                callback.payResult(0, "支付成功");
                mActivity.finish();
                break;
            case "REFUND":
                callback.payResult(-1, "已转入退款");
                mActivity.finish();
                break;
            case "NOTPAY":
                callback.payResult(-2, "取消支付");
                if(AsyncData.payType == PayRequest.WX_EWM){
                    ToastTools.showDialog(mActivity);
                }else{
                    mActivity.finish();
                }
                break;
            case "CLOSED":
                callback.payResult(-3, "订单已关闭");
                mActivity.finish();
                break;
            case "PAYERROR":
                callback.payResult(-4, "支付失败");
                mActivity.finish();
                break;
        }
        close();
    }

    private void close(){
        if(progressDialog != null){
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            progressDialog.dismiss();
        }
        if(mActivity != null){
            mActivity.finish();
        }
    }

    private static void showDialog(Context context,boolean isShow){
        if(isShow){
            progressDialog = new ProgressDialog(context);
            progressDialog.setProgressStyle(0);
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("正在查询支付结果...");
            progressDialog.show();
        }
    }
}
