package com.payment.poliy.test;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.fast.pay.FastParams;
import com.fast.pay.FastPay;
import com.fast.utils.Utils;
import com.payment.poliy.R;

import java.lang.reflect.Method;

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
                fanshe();
            }
        });
    }

    String string = "toqb2jhszOVkZPDWmNOOnqJw#smanBojw2s6tYKSPBC5bQYXqRnQ6AIbxMYdmysnnp%2Bw=#pBDCkVGva1irj68zPI3B4qGoObwQGWqZuqoL2HQfIt3YCZKiioGdYntDji71lleLkzFhqG5/hpGGFhFA4IXmkLnycbAMUzUxu2A%2BJxOi7odEcXHhdF2GScWh4SMLkr5X8WBeaz//k%2BNOcoPy2RuaVDkJARbioXciDP8LMe07Dc3aLtf%2BN4Fvx66NLjxYYGpPbmZJzPSyvdid09UvpwK3bTEw/1LHeUsY41AAzJTs7eP8Ef3zs5ySDfqKndOIuW3Dwm4xyNZgV1sS/RFjLfrP7IhHFpGLf2Y05vkfpkZEgfIbwI5/K1aF61YXtVSKolId37gT97B4pG2nwEehBCQE1xFWn8Zbz13y7sHG8sgxWErGobdzJdbpEFtVFnbQjazG3%2BHPx%2Bn6z/nu9f8vUPMQ8TGLM/N8UrZ3";

    private void fanshe() {
        Class<FastPay> fastPayClass = FastPay.class;
        FastPay fastPay = null;
        try {
            fastPay = fastPayClass.newInstance();
            Method method = fastPayClass.getDeclaredMethod("a", FastPay.class, String.class);
            //System.out.println("fastPayClass--" + method);
            method.setAccessible(true);
            //System.out.println("fastPayClass--" + method.getReturnType());
            //method.invoke(fastPay, fastPay, string);

            Method[] methods = fastPayClass.getMethods();
            Method[] declaredMethods = fastPayClass.getDeclaredMethods();
            for (Method m:declaredMethods) {
                System.out.println("fastPayClass--" + m.getDeclaringClass());
            }


        } catch (Exception e) {
            System.out.println("fastPayClass--Exception==" + e.toString());
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

        FastPay.getInstance().pay(this, params, handler);
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

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0://支付失败，失败详情：msg.obj
                    Utils.showToast(mContext, (String) msg.obj);
                    break;
                case 1://支付成功
                    int payType = msg.arg1;//支付方式 payType=FirePay.WEICHAT or FirePay.ALIPAY
                    Utils.showToast(mContext, "支付成功,支付方式=" + payType);
                    break;
                default:
                    break;
            }
        }
    };

}

