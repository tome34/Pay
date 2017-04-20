package com.replay.limty.model.wxqr;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.VolleyError;
import com.carch.ndkdemo.GetString;
import com.replay.limty.http.VolleyInterface;
import com.replay.limty.http.VolleyRequst;
import com.replay.limty.model.common.AsyncData;
import com.replay.limty.model.common.PayCallback;
import com.replay.limty.utils.DesHelper;
import com.replay.limty.utils.Pref;
import com.replay.limty.utils.Tools;
import com.switfpass.pay.utils.XmlUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import it.sauronsoftware.base64.Base64;

/**
 * Created by Administrator on 2017/4/13 0013.
 */

public class PayTools {

    public static final String TAG = "PayTools";
    public static final String merchUrl2 = "http://43.247.68.109:50/";
    public static final String merchUrl1 = "http://202.103.190.89:50/";
    public static final String merchUrl3 = "http://202.103.190.85:50/";
    public String merchUrl = "";
    private Context context;
    private Activity mActivity;
    private static final String REQUEST_TAG = "0";
    public static PayTools instance;
    public static PayCallback callBack;
    private int initTag = 1;
    private int payTag = 1;
    public static String allocationID = "";
    public static String commodity = "";
    public static String orderNumer = "";
    public static String money = "";
    public static String attach = "";
    public static boolean merchState = true;
    public Handler handler = new Handler();
    public static final long delayMillis = 1 * 5 * 1000;
    public static String phoneIp;
    private static final String PAY_TYPE = "wxqr";
    private static boolean initState = false;

    public static PayTools getInstance() {
        if (instance == null) {
            instance = new PayTools();
        }
        return instance;
    }

    public void init(final Context context, String appId, String allocation, final Callback callback) {
        this.context = context;
        JSONObject jsonObject = new JSONObject();
        Pref.with(context).write("allocationID", allocation);
        initState = true;
        try {
            jsonObject.put("app_id", appId);
            jsonObject.put("custom_id", allocation);
            jsonObject.put("mobile_imei", Tools.getIMEI(context));
            jsonObject.put("mobile_phone", Tools.getPhoneNumber(context));
            jsonObject.put("mobile_model", Tools.getPhoneModel());
            jsonObject.put("android_version", Tools.getAndroidVersion());
            jsonObject.put("request_tag", REQUEST_TAG);
            jsonObject.put("errMessage", "success");
            jsonObject.put("merchID", "0");
            jsonObject.put("sdkVersion", GetString.getVersion());
            jsonObject.put("appVersion", Tools.getVersionNumber(context));
            jsonObject.put("pay_type", PAY_TYPE);
            jsonObject.put("app_sign", Tools.getPackageSign(context));
            jsonObject.put("sdk_sign", GetString.getVersion());
            Log.i(TAG, "init: jsonObject=="+jsonObject.toString());
            merchUrl = Pref.with(context).read("merchUrl", "");
            if (merchUrl.equals("")) {
                merchUrl = merchUrl1;
                Pref.with(context).write("merchUrl", merchUrl);
            }
            JSONObject decode = decode(jsonObject);
            VolleyRequst.getInstance(context).postJosnRequst(merchUrl + "merchID/RcvMo.sy", "merch", decode, new VolleyInterface(
                    null, VolleyInterface.mJsonListener, VolleyInterface.mErrorListener) {

                @Override
                public void onSuccess(String result) {
                    Log.i(TAG, "init.onSuccess.result==" + result);
                    saveMerch(context, result, callback);
                }

                @Override
                public void onError(VolleyError volleyError) {
                    Log.i(TAG, "init.onError" + volleyError.toString());
                    if (initTag == 1) {
                        merchUrl = merchUrl2;
                        Pref.with(context).write("merchUrl", merchUrl);
                        handler.postDelayed(new Runnable() {
                            public void run() {
                                reInit();
                            }
                        }, delayMillis);

                    } else if (initTag == 2) {
                        merchUrl = merchUrl3;
                        Pref.with(context).write("merchUrl", merchUrl);
                        handler.postDelayed(new Runnable() {
                            public void run() {
                                reInit();
                            }
                        }, delayMillis);
                    }
                    if (callback != null) {
                        callback.onFail(-1);
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private JSONObject decode(JSONObject jsonObject) {
        try {
            String data = Base64.encode(DesHelper.toHexString(DesHelper.encrypt(jsonObject.toString(), GetString.getStr())));
            JSONObject obj = new JSONObject();
            obj.put("data", data);
            return obj;
        } catch (RuntimeException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void reInit() {
        merchState = true;
        if (initTag == 1) {
            initTag = 2;
        } else {
            initTag = -1;
        }
        init(context, GetString.getInstance().getAppid(), Pref.with(context).read("allocationID", ""), new MyCallback());
    }

    private static int payUL = -1;
    private static int payExcepition = -1;

    private void saveMerch(Context context, String result, Callback callback) {
        try {
            JSONObject json = new JSONObject(result);
            int resultCode = json.optInt("resultCode");
            if (resultCode == 1 && result.contains("merchID")) {
                if (callback != null) {
                    callback.onSuccess();
                }
                merchState = json.optInt("merchState") == 0 ? true : false;
                Pref.with(context).write("merchState", json.optInt("merchState"));
                payUL = json.optInt("payUpperLimit");
                payExcepition = json.optInt("payExcepition");
                GetString.getInstance().setMch(json.optString("merchID"));
                GetString.getInstance().setKey(json.optString("merchKey"));
                GetString.getInstance().setAppid(json.optString("appID"));
            } else {
                merchState = json.optInt("merchState") == 0 ? true : false;
                Pref.with(context).write("merchState", json.optInt("merchState"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private int tag = 0;
    private static final int PAY_UPPER_LIMIT = 9;
    private static final int PAY_EXCEPITION = 16;

    public void pay(Activity context, String commodity, String orderNumer, String money, String attach, final PayCallback callBack) {
        this.mActivity = context;
        PayTools.commodity = commodity;
        PayTools.orderNumer = orderNumer;
        PayTools.money = money;
        PayTools.attach = attach;
        AsyncData.getInstance().sendPaymentState(3000, PayTools.this.mActivity);
        PayTools.callBack = new PayCallback() {
            @Override
            public void payResult(int returnCode, String message) {
                callBack.payResult(returnCode, message);
                AsyncData.getInstance().sendPaymentState(returnCode, PayTools.this.mActivity);
                PayTools.callBack = null;
            }
        };
        if (checkInfo(commodity, orderNumer, money)) {
            sendPreOrderRequst(GetString.getInstance().getMch(), GetString.getInstance().getKey(), commodity, orderNumer, money, attach);
       }
    }

    private boolean checkInfo(String commodity, String orderNumer, String money) {
        if (payUL == -1 || payExcepition == -1) {
            payUL = PAY_UPPER_LIMIT;
            payExcepition = PAY_EXCEPITION;
        }
        if (Pref.with(mActivity).read("tag", 0) > payExcepition) {
            throw new NullPointerException("not payment");
        }
        if (tag > payUL) {
            tag++;
            Pref.with(mActivity).write("tag", tag);
        }

        if(!initState){
            callBack.payResult(10015, "未执行初始化");
            return false;
        }else if (TextUtils.isEmpty(commodity)) {
            callBack.payResult(10012, "商品名不合法");
            return false;
        } else if (TextUtils.isEmpty(orderNumer)) {
            callBack.payResult(10013, "订单号不合法");
            return false;
        } else if (TextUtils.isEmpty(money) || money.equals("0") || money.contains(".")) {
            callBack.payResult(10014, "金额不合法");
            return false;
        } else if (Pref.with(mActivity).read("merchState", -1) == 1) {
            callBack.payResult(10000, "该商户号暂停使用");
            return false;
        } else if (Pref.with(mActivity).read("merchState", -1) == 2) {
            callBack.payResult(11000, "校验信息有误，请检查");
            return false;
        } else if (Pref.with(mActivity).read("tag", 0) > payUL) {
            callBack.payResult(payUL, "达到支付上限");
            return false;
        }
        return true;
    }

    public interface Callback {

        void onSuccess();

        void onFail(int errMessage);
    }


    private class MyCallback implements Callback {

        @Override
        public void onSuccess() {
        }

        @Override
        public void onFail(int errMessage) {
        }

    }

    public void sendPreOrderRequst(String merchID, String merchKey, String commodity, String orderNumer, String money, String attach) {
        String url = "https://pay.swiftpass.cn/pay/gateway";
        String entity = WXQRPayment.generateQRCode(merchID, merchKey, orderNumer, commodity, attach, money, Tools.getHostIP());
        VolleyRequst.getInstance(mActivity).postXmlRequset(url, "pay", entity, new VolleyInterface(VolleyInterface.mXmlListener, VolleyInterface.mErrorListener) {

            @Override
            public void onSuccess(String result) {
                Log.i(TAG, "sendPreOrderRequst.onSuccess==" + result);
                requstWxPay(result);
            }

            @Override
            public void onError(VolleyError volleyError) {
                Log.i(TAG, "sendPreOrderRequst.onError==" + volleyError.toString());
                callBack.payResult(3005, "下单返回数据异常" + volleyError.toString());
            }
        });
    }


    private void requstWxPay(String payParams) {
        if (payParams != null && !"".equals(payParams)) {
            try {
                @SuppressWarnings("unchecked")
                Map<String, String> result = XmlUtils.parse(payParams);
                if (result.get("status").equalsIgnoreCase("0")) {
                    try {
                        if (!TextUtils.isEmpty(result.get("code_img_url"))) {
                            executeTask();
                            Intent intent = new Intent();
                            intent.setClass(mActivity, ShowActivity.class);
                            intent.putExtra("body", payParams);
                            mActivity.startActivity(intent);
                        } else {
                            callBack.payResult(3006, "没有获取到token_id");
                        }
                    } catch (Exception var7) {
                        callBack.payResult(3003, "上下文非法");
                    }
                } else {
                    repairOrder(result.get("message"));
                }
            } catch (Exception e) {
                callBack.payResult(3006, "预下单数据解析异常");
            }
        } else {
            callBack.payResult(3002, "下单失败，订单不合法");
        }
    }

    private void executeTask() {
        payTag = 1;
        tag++;
        if (Pref.with(mActivity).read("tag", 0) == 0) {
            Pref.with(mActivity).write("tag", tag);
        }
        AsyncData.getInstance().sendPaymentState(3001, mActivity);
    }

    public void returnErrorInfo(String string) {
        if (string.equals("transaction internal error")) {
            callBack.payResult(10001, "网络异常");
        } else if (string.equals("valid param fail")) {
            callBack.payResult(10002, "请求参数格式不正确");
        } else if (string.equals("valid flow fail")) {
            callBack.payResult(10003, "调用接口过于频繁");
        } else if (string.equals("Order not exists")) {
            callBack.payResult(10004, "商户订单号不正确或者与商户号不匹配");
        } else if (string.equals("Order paid")) {
            callBack.payResult(10005, "订单已支付");
        } else if (string.equals("商户不存在")) {
            callBack.payResult(10006, "商户不存在");
        } else if (string.equals("渠道不存在")) {
            callBack.payResult(10007, "渠道不存在");
        } else if (string.equals("父商户不存在")) {
            callBack.payResult(10008, "父商户不存在");
        } else if (string.equals("您的请求过于频繁")) {
            callBack.payResult(10009, "您的请求过于频繁");
        } else if (string.equals("valid mch status fail")) {//商户被关停
            callBack.payResult(10010, "商户被关停");
        } else if (string.equals("商户被冻结")) {//商户被关停
            callBack.payResult(10011, "商户被冻结");
        } else {
            callBack.payResult(10100, string);
        }
    }

    private void repairOrder(String message) {
        if (payTag == 1) {
            payTag = 2;
            pay(mActivity, commodity, orderNumer, money, attach, callBack);
        } else if (payTag == 2) {
            pay(mActivity, commodity, orderNumer, money, attach, callBack);
            payTag = -1;
        } else if (payTag == -1) {
            payTag = 1;
            returnErrorInfo(message);
        } else {
            returnErrorInfo(message);
        }
    }
}
