package com.replay.limty.model.wxqr;

import android.util.Log;

import com.replay.limty.utils.Tools;
import com.switfpass.pay.utils.XmlUtils;

import java.util.HashMap;
import java.util.Map;

import static com.replay.limty.utils.Tools.createSign;

/**
 * Created by Administrator on 2017/3/27 0027.
 */

public class WXQRPayment {

    public static String generateQRCode(String mchID,String mchKey,String orderNumber,String body,String attach,String money,String phoneIp){
        Map<String,String> params = new HashMap<>();
        params.put("service","pay.weixin.native");
        params.put("version","2.0");
        params.put("mch_id",mchID);
        params.put("out_trade_no",orderNumber);
        params.put("body",body);
        params.put("attach",attach);
        params.put("total_fee",money);
        params.put("mch_create_ip",phoneIp);
        params.put("notify_url","http://202.103.190.89:50/WXPAY/RcvMo.sy");
        params.put("nonce_str", Tools.getNonceStr());
        params.put("limit_credit_pay","0");
        params.put("sign",Tools.createSign(mchKey,params));
        Log.i("PayTools","generateQRCode.params=="+params.toString());
        return XmlUtils.toXml(params);
    }
}
