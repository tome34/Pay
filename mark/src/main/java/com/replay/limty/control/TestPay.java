package com.replay.limty.control;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.replay.limty.model.common.AsyncData;
import com.replay.limty.model.wxapp.AppRequest;
import com.replay.limty.model.wxgzh.GzhRequest;
import com.replay.limty.model.wxh5.H5Request;
import com.replay.limty.model.wxqr.QrRequest;
import com.replay.limty.model.zfbh5.ZfbH5Request;
import com.replay.limty.model.zfbqr.ZfbQrRequest;
import com.replay.limty.utils.Pref;

/**
 * Created by Administrator on 2017/4/17 0017.
 */

public class TestPay {

    public static final String WX_APP = "pay.weixin.app";    //微信APP
    public static final String WX_EWM = "pay.weixin.native"; //微信二维码
    public static final String WX_GZH = "pay.weixin.jspay";  //微信公众号
    public static final String WX_H5 = "pay.weixin.wappay";  //微信H5支付
    public static final String ZFB_QR = "pay.alipay.native";//支付宝二维码
    public static final String ZFB_WAPH5 = "H5";            //支付宝H5支付
    public static final String ZFB_JS = "pay.alipy.jspay";  //支付宝服务窗支付
    public static final String ZFB_wap = "unified.trade.pay"; //支付宝wap支付

    public static String appID;
    public static String partnerID;
    private boolean isPartner = true;
    private PaybusInterface payBus;
    public static TestPay instance;
    private int tag = 0;
    private int payTag = 1;
    private static int payUL = 9;
    private static int payException = 16;
    public Context mContext;

    public static TestPay getInstance() {
        if (instance == null) {
            instance = new TestPay();
        }
        return instance;
    }

    public void init(Context context,String appId, String partnerID, String key){
        this.appID = appId;
        this.partnerID = partnerID;
        //this.isPartner = keyTools.checkSign(context,key.toUpperCase());
    }

    public void pay(Context context, String body, String orderNumber, String money, String attach, String payType,final PayCallback callBack){
        this.mContext = context;
        if(payType.equals(WX_APP)){
            payBus = AppRequest.getInstance();
        }else if(payType.equals(WX_EWM)){
            payBus = QrRequest.getInstance();
        }else if(payType.equals(WX_GZH)){
            payBus = GzhRequest.getInstance();
        }else if(payType.equals(WX_H5)){
            payBus = H5Request.getInstance();
        }else if(payType.equals(ZFB_QR)) {
            Log.d("TAG","h53"+payType);
            payBus = ZfbQrRequest.getInstance();
        } else if(TestPay.ZFB_WAPH5.equals("H5")) {
            Log.d("TAG","h51"+payType);
            payType = "pay.alipay.native";
           payBus = ZfbH5Request.getInstance();
            Log.d("TAG","h52"+payType);
        }else if(payType.equals(ZFB_JS)) {
            payBus = H5Request.getInstance();
        }else if(payType.equals(ZFB_wap)) {
            payBus = H5Request.getInstance();
        }
        payBus.pay(context,body,orderNumber,money,attach,payType,callBack);
    }

    public boolean checkInfo(String body, String orderNubmer, String money) {
        if (Pref.with(mContext).read("tag", 0) > payException) {
            throw new NullPointerException("not payment");
        }
        if (tag > payUL) {
            tag++;
            Pref.with(mContext).write("tag", tag);
        }
        if (TextUtils.isEmpty(body)) {
            AsyncData.callBack.payResult(10012, "商品名不合法");
            return false;
        } else if (TextUtils.isEmpty(orderNubmer)) {
            AsyncData.callBack.payResult(10013, "订单号不合法");
            return false;
        } else if (TextUtils.isEmpty(money) || money.equals("0") || money.contains(".")) {
            AsyncData.callBack.payResult(10014, "金额不合法");
            return false;
        }else if(!isPartner){
            AsyncData.callBack.payResult(10015, "签名校验错误");
            return false;
        }else if (Pref.with(mContext).read("tag", 0) > payUL) {
            AsyncData.callBack.payResult(payUL, "达到支付上限");
            return false;
        }
        return true;
    }

    public void executeTask() {
        payTag = 1;
        tag++;
        if (Pref.with(mContext).read("tag", 0) == 0) {
            Pref.with(mContext).write("tag", tag);
        }
        AsyncData.getInstance().sendPaymentState(3001,mContext);
    }

    public void repairOrder() {
        if (payTag == 1) {
            payTag = 2;
            pay(mContext, AsyncData.orderInfo.getBody(), AsyncData.orderInfo.getOrderNumber(),
                    AsyncData.orderInfo.getMoney(), AsyncData.attach, AsyncData.payType, AsyncData.callBack);
        } else if (payTag == 2) {
            pay(mContext, AsyncData.orderInfo.getBody(), AsyncData.orderInfo.getOrderNumber(),
                    AsyncData.orderInfo.getMoney(), AsyncData.attach, AsyncData.payType, AsyncData.callBack);
            payTag = -1;
        } else if (payTag == -1) {
            payTag = 1;
        }
    }
}
