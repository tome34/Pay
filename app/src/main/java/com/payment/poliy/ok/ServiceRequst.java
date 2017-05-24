//package com.payment.poliy;
//
//import android.content.Context;
//import android.util.Log;
//
//import com.android.volley.VolleyError;
//import com.carch.ndkdemo.GetString;
//import com.google.gson.Gson;
//import com.google.gson.reflect.TypeToken;
//import com.replay.limty.http.VolleyInterface;
//import com.replay.limty.utils.Tools;
//import com.switfpass.pay.utils.MD5;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.util.HashMap;
//import java.util.Map;
//
//import it.sauronsoftware.base64.Base64;
//
///**
// * Created by Administrator on 2017/4/14 0014.
// */
//
//public class ServiceRequst {
//
//    private static final String service_url = "http://43.247.68.114:8084/main/preorder";
//    private static final String notify_url = "http://202.103.190.89:50/WXPAY/RcvMo.sy";
//
//    public static void servicePay(Context context, String channelCode, String partnerId, String payType,
//                                String orderNumber, String body, String attach, String money) throws JSONException {
//        JSONObject params = new JSONObject();
//        String data = "";
//        String up = "";
//        params.put("appId",channelCode);
//        params.put("partnerId",partnerId);
//        params.put("out_trade_no",orderNumber);
//        params.put("totalFee",money);
//        params.put("body",body);
//        params.put("sdkVersion", GetString.getVersion());
//        params.put("detail","123");
//        params.put("attach",attach);
//        params.put("mch_create_ip",Tools.getHostIP());
//        params.put("deviceName", Tools.getPhoneModel());
//        params.put("deviceCode",Tools.getAndroidVersion());
//        params.put("imei",Tools.getIMEI(context));
//        params.put("imsi",Tools.getIMSI(context));
//        params.put("iccid",Tools.getICCID(context));
//        params.put("payType",payType);
//        params.put("callback_url",notify_url);
//        params.put("sub_openid","wx1596235472356");
//        params.put("device_info","16359634586");
//        params.put("mch_app_name","16359634586");
//        params.put("mch_app_id","16359634586");
//        params.put("buyer_logon_id","16359634586");
//        params.put("buyer_id","16359634586");
//
//        String sign = createSign("79e2618bd809a3599a9d7d6fa178ac9a",params.toString());
//        params.put("sign",sign);
//        Log.i("测试","service_url=="+service_url+"ServiceRequst.params=="+params.toString());
//        String userp = "";
//        try {
//            String username = Base64.encode("QHYJ_USER_20170415");
//            String password = Base64.encode("QHYJ_PwD@20170415#15:03");
//            up = "Basic "+ Base64.encode(username +":"+ password);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        JSONObject obj = new JSONObject();
//        obj.put("data",Base64.encode(params.toString()));
//
//        Log.i("测试","上传参数=="+obj.toString());
//        VolleyRequst.getInstance(context).postJosnRequsts(service_url,"url",up ,obj, new VolleyInterface(
//                VolleyInterface.mListener,VolleyInterface.mJsonListener,VolleyInterface.mErrorListener
//        ) {
//            @Override
//            public void onSuccess(String result) {
//               Log.i("测试","postJosnRequsts.onSuccess=="+result);
//            }
//
//            @Override
//            public void onError(VolleyError volleyError) {
//                Log.i("测试","postJosnRequsts.volleyError=="+volleyError.toString());
//            }
//        });
//    }
//
//    private static String createSign(String signKey, String jsonStr){
//        Gson gson = new Gson();
//        Map<String,Object> params =  gson.fromJson(jsonStr, new TypeToken<HashMap<String,Object>>(){}.getType());
//        String partnerId = (String) params.get("partnerId");
//        String body = (String) params.get("body");
//        String totalFee = (String) params.get("totalFee");
//        String out_trade_n = (String) params.get("out_trade_no");
//        StringBuilder buf = new StringBuilder();
//        buf.append(partnerId+body+totalFee+out_trade_n+signKey);
//        String preStr = buf.toString();
//        Log.i("测试", "MD5前createSign: "+preStr);
//        String sign = "";
//        try{
//            sign = MD5.md5s(preStr).toUpperCase();
//        }catch (Exception e){
//            sign = MD5.md5s(preStr).toUpperCase();
//        }
//        Log.i("测试", "MD5后createSign: "+sign);
//        return sign;
//    }
//}
