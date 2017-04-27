package com.payment.poliy;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.replay.limty.control.TestPay;
import com.replay.limty.control.PayCallback;
import com.replay.limty.model.wxh5.H5Request;
import com.replay.limty.utils.Tools;
import com.switfpass.pay.utils.MD5;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "扫码支付";
    private EditText etMoney;
    private TextView tvShow;
    public static final String APP_ID = "wxe5688c47b26cc5d3";
    public static final String MCH_ID = "102510199737";
    public static final String MCH_KEY = "09a7a739c1874a63b1448e42981d29f5";
    public static final String PARTNER_ID = "test0001";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etMoney = (EditText) findViewById(R.id.et_money);
        tvShow = (TextView) findViewById(R.id.textView);
        TestPay.getInstance().init(this,APP_ID,PARTNER_ID,"6A16823ED6305BB22EF92BC703CDD8AE");

        Log.i(TAG, "onCreate: "+MD5.md5s("zj0001test product163270532701556887daa4babae15ae17eee90c9e"));

    }

    public void doPay(View view) {

        String money = etMoney.getText().toString();

        switch (view.getId()) {
            case R.id.btn_init:
                TestPay.getInstance().init(this,APP_ID,PARTNER_ID,"6A16823ED6305BB22EF92BC703CDD8AE");
                break;

            case R.id.btnWx_app:
                payTest(TestPay.WX_APP,money);
                break;

            case R.id.btnWx_Qr:
                payTest(TestPay.WX_EWM,money);
                break;

            case R.id.btnWx_gzh:
                payTest(TestPay.WX_GZH,money);
                break;

            case R.id.btnWx_h5:
                //payTest(TestPay.WX_H5,money);
                H5Request.h5Request(this,"102510199752","79e2618bd809a3599a9d7d6fa178ac9a",money);
                break;

            case R.id.btnZfb_Qr:
                payTest(TestPay.ZFB_QR,money);
                break;

            case R.id.btnZfb_h5:
                payTest(TestPay.ZFB_WAPH5,money);
                break;

            case R.id.btnZfb_js:
                payTest(TestPay.ZFB_JS,money);
                break;

            case R.id.btnZfb_wap:
                payTest(TestPay.ZFB_wap,money);
                break;
        }
    }

    private void payTest(String payType,String money) {
        TestPay.getInstance().pay(this, "购买砖石", Tools.creatOrderNumber(), money, "abc987", payType, new PayCallback() {
            @Override
            public void payResult(int returnCode, String message) {
                tvShow.setText("支付结果："+message+"   返回码："+returnCode);
            }
        });
    }
}
