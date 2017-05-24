//package com.payment.poliy;
//
//import android.content.Context;
//
//import com.replay.limty.control.PayRequest;
//
///**
// * Created by Administrator on 2017/4/28 0028.
// */
//
//public class TestAPI {
//
//    public static final String WX_APP = "pay.weixin.app";    //微信APP
//    public static final String WX_EWM = "pay.weixin.native"; //微信二维码
//    public static final String WX_GZH = "pay.weixin.jspay";  //微信公众号
//    public static final String WX_H5 = "pay.weixin.wappay";  //微信H5支付
//    public static final String ZFB_QR = "pay.alipay.native";//支付宝二维码
//    public static final String ZFB_WAPH5 = "pay.alipay.native";//支付宝H5支付
//    public static final String ZFB_JS = "pay.alipay.jspay";  //支付宝服务窗支付
//    public static final String ZFB_wap = "unified.trade.pay"; //支付宝wap支付
//
//
//    public static void sendRequest(Context context,String body, String orderNumber, String money, String attach, String payType) {
//        try {
//            ServiceRequst.servicePay(context, PayRequest.channelCode, PayRequest.partnerID, payType, orderNumber, body, attach, money);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//}
