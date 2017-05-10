package com.payment.poliy.test;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.fast.pay.FastParams;
import com.fast.pay.FastPay;
import com.fast.utils.Des3CbcUtil;
import com.fast.utils.Des3CbcUtilNet;
import com.fast.utils.JsonParser;
import com.fast.utils.MyHttpPost;
import com.fast.utils.Utils;
import com.payment.poliy.R;

import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.TreeMap;

import static com.fast.pay.FastPay.ALIPAY;
import static com.fast.pay.FastPay.ALIPAY_SCAN;
import static com.fast.pay.FastPay.WEICHAT;
import static com.fast.pay.FastPay.WEICHAT_SCAN;

/**
 * Created by Administrator on 2017/5/4 0004.
 */

public class KoActivity extends Activity {

    private Button mButton1;
    private Button mButton2;
    private Context mContext;
    private EditText mEditText;
    private int money;
    private String mOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ko);
        mContext = this;

        mEditText = (EditText) findViewById(R.id.editText);

        mButton1 = (Button) findViewById(R.id.testButton1);
        mButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPay();
            }
        });
        mButton2 = (Button) findViewById(R.id.testButton2);
        mButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //requestPay(FastPay.WEICHAT);
                //fanshe();
            }
        });
    }

    String string = "toqb2jhszOVkZPDWmNOOnqJw#smanBojw2s6tYKSPBC5bQYXqRnQ6AIbxMYdmysnnp%2Bw=#pBDCkVGva1irj68zPI3B4qGoObwQGWqZuqoL2HQfIt3YCZKiioGdYntDji71lleLkzFhqG5/hpGGFhFA4IXmkLnycbAMUzUxu2A%2BJxOi7odEcXHhdF2GScWh4SMLkr5X8WBeaz//k%2BNOcoPy2RuaVDkJARbioXciDP8LMe07Dc3aLtf%2BN4Fvx66NLjxYYGpPbmZJzPSyvdid09UvpwK3bTEw/1LHeUsY41AAzJTs7eP8Ef3zs5ySDfqKndOIuW3Dwm4xyNZgV1sS/RFjLfrP7IhHFpGLf2Y05vkfpkZEgfIbwI5/K1aF61YXtVSKolId37gT97B4pG2nwEehBCQE1xFWn8Zbz13y7sHG8sgxWErGobdzJdbpEFtVFnbQjazG3%2BHPx%2Bn6z/nu9f8vUPMQ8TGLM/N8UrZ3";

    private static void fanshe(FastPay f,String string) {
        Class<FastPay> fastPayClass = FastPay.class;
        FastPay fastPay = null;
        try {
            fastPay = fastPayClass.newInstance();
            Method method = fastPayClass.getDeclaredMethod("a", FastPay.class, String.class);
            System.out.println("H5Request--" + method);
            method.setAccessible(true);
            System.out.println("H5Request--" + method.getReturnType());
            method.invoke(fastPay, f, string);
        } catch (Exception e) {
            System.out.println("H5Request--Exception==" + e.toString());
            e.printStackTrace();
        }
    }

    /*
     * 支付接口1
     */
    public void requestPay() {
        String ret = mEditText.getText().toString();
        if (Utils.isNullOrEmpty(ret)) {
            Utils.showToast(mContext, "默认1分");
            ret = "1";
        }
        money = Integer.parseInt(ret);
        mOrder = String.valueOf(System.currentTimeMillis());

        FastParams params = new FastParams();
        params.setAppKey("0a3952b9858148558b6efed6e4756a51");//app key
        params.setBusKey("100540016001");//商户号
        params.setOrderno(mOrder);// 订单号
        params.setFee(money);// 支付金额 - 单位为分
        params.setBody("测试道具");// 商品名称
        params.setAttach("测试道具");// 商品描述
        params.setCallbackUrl("http://www.baidu.com");// 支付成功后进入页面
        params.setNotifyUrl("http://www.baidu.com");// 数据同步地址

        pay(this, params, handler,FastPay.WEICHAT);
    }

    public void pay(Activity var1x, FastParams fastParams, Handler handler, int var4x) {
        if (!Utils.isNetworkConnected(var1x)) {
            Utils.showToast(var1x, "网络异常");
        } else {
            String var7 = "jft.weixin.mppay";
            if (WEICHAT == var4x) {
                var7 = "jft.weixin.mppay";
            } else if (ALIPAY == var4x) {
                var7 = "jft.ali.wappay";
            } else if (WEICHAT_SCAN == var4x) {
                var7 = "jft.weixin.native";
            } else if (ALIPAY_SCAN == var4x) {
                var7 = "jft.ali.native";
            }
            TreeMap var8 = new TreeMap();
            var8.put("service", var7);
            var8.put("version", "3.0");
            var8.put("charset", "UTF-8");
            var8.put("sign_type", "MD5");
            var8.put("mch_id", fastParams.getBusKey());
            var8.put("out_trade_no", fastParams.getOrderno().replaceAll("\"", ""));
            var8.put("body", fastParams.getBody().replaceAll("\"", ""));
            var8.put("attach", fastParams.getAttach().replaceAll("\"", ""));
            var8.put("total_fee", String.valueOf(fastParams.getFee()).replaceAll("\"", ""));
            var8.put("notify_url", fastParams.getNotifyUrl().replaceAll("\"", ""));
            var8.put("callback_url", fastParams.getCallbackUrl().replaceAll("\"", ""));
            var8.put("nonce_str", fastParams.getOrderno().replaceAll("\"", ""));
            String var9 = Utils.getStringMD5((Utils.getMapSrc(var8) + fastParams.getAppKey()).trim()).toLowerCase();
            var8.put("sign", var9);
            var8.put("channel", "FastPay_APK");
            var9 = Utils.mapToJsonStr(var8);
            var9 = "{\"data\": \"" + var9 + "\"}";
            MyHttpPost.post(Des3CbcUtil.des3DecodeCBCAPI("0Cwn63omnB4fEl4ZF9194g/D/qJTFCg9NRxx7goNc3SFym/cIuNW7Q==").trim(), var9, handler);
        }
    }

    /*
     * 支付接口2
     */
    public void requestPay(int payType) {
        String ret = mEditText.getText().toString();
        if (Utils.isNullOrEmpty(ret)) {
            Utils.showToast(mContext, "默认1分");
            ret = "1";
        }
        money = Integer.parseInt(ret);
        mOrder = String.valueOf(System.currentTimeMillis());
        FastParams params = new FastParams();
        params.setAppKey("0a3952b9858148558b6efed6e4756a51");//app key
        params.setBusKey("100540016001");//商户号
        params.setOrderno(mOrder);// 订单号
        params.setFee(money);// 支付金额 - 单位为分
        params.setBody("测试道具");// 商品名称
        params.setAttach("测试道具");// 商品描述
        params.setCallbackUrl("http://www.baidu.com");// 支付成功后进入页面
        params.setNotifyUrl("http://www.qq.com");// 数据同步地址
        FastPay.getInstance().pay(this, params, handler, payType);
    }

    /*
     * 支付回调
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        FastPay.getInstance().callResult(requestCode, resultCode, data);
    }

    public static String var2 = "vvl2PtlBMIVL99vFWzv566AqJ%2BtcsCJ93ZEbUO5ilYlF4%2BAMWpWUkIDFMD7i8EStCGV4vBs8oprqkcuVe15BGA==";
    public static String var3 = "kmhDS/XE7DUcA%2B9U48fD8JSYxNEJHA4Ie5vx9tb7uF8=";

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0://支付失败，失败详情：msg.obj
                    Utils.showToast(mContext, (String) msg.obj);
                    break;
                case 1://支付成功
                    int payType = msg.arg1;//支付方式 payType=FirePay.WEICHAT or FirePay.ALIPAY
                    Utils.showToast(mContext, "支付成功,支付方式=" + payType);
                    JSONObject var5 = JsonParser.asJSONObject((String)msg.obj);
                    Log.i("H5Request","var5=="+var5.toString());

                    String var4 = JsonParser.getRawString("pay_info", var5);
                    String key = "100540016001";
                    try{
                        String s = Des3CbcUtilNet.des3DecodeCBCAPI(var4,mOrder);
                        Log.i("H5Request", "handleMessage: var4=="+var4);
                        Log.i("H5Request","pay_info=="+s);

                        fanshe(FastPay.getInstance(),var4);
                    }catch (Exception e){
                        Log.i("H5Request","Exception=="+e.toString());
                    }
                    break;
                default:
                    break;
            }
        }
    };

}

