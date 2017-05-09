package com.payment.poliy.ok;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.payment.poliy.R;
import com.replay.limty.control.PayCallback;
import com.replay.limty.control.PayRequest;
import com.replay.limty.utils.Tools;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "扫码支付";
    private EditText etMoney;
    private TextView tvShow;
    public static final String APP_ID = "wx6fb989854b5583ed";
    public static final String PARTNER_ID = "tx0001";
    public static final String KEY = "b99e2e0822d133a5fe279ddfd114067f";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etMoney = (EditText) findViewById(R.id.et_money);
        tvShow = (TextView) findViewById(R.id.textView);
        PayRequest.getInstance().init(APP_ID, PARTNER_ID, KEY);
    }

    public void doPay(View view) {

        String money = etMoney.getText().toString();

        switch (view.getId()) {
            case R.id.btn_init:
//                PayRequest.getInstance().init(this,APP_ID,PARTNER_ID,"6A16823ED6305BB22EF92BC703CDD8AE");
//                TestAPI.sendRequest(this,"api测试",Tools.creatOrderNumber(),"1","abc887",TestAPI.WX_APP);
//                H5Request.h5Request(this,"102510199738","b99e2e0822d133a5fe279ddfd114067f","1");

                break;

            case R.id.btnWx_app:
                payTest(PayRequest.WX_APP, money);
                break;

            case R.id.btnWx_Qr:
                payTest(PayRequest.WX_EWM, money);
                break;

            case R.id.btnWx_gzh:
                payTest(PayRequest.WX_GZH, money);
                break;

            case R.id.btnWx_h5:
                payTest(PayRequest.WX_H5, money);
                break;

            case R.id.btnZfb_Qr:
                payTest(PayRequest.ZFB_QR, money);
                break;

            case R.id.btnZfb_h5:
                payTest(PayRequest.ZFB_WAPH5, money);
                break;

            case R.id.btnZfb_js:
                payTest(PayRequest.ZFB_JS, money);
                break;

            case R.id.btnZfb_wap:
                payTest(PayRequest.ZFB_wap, money);
                break;
        }
    }

    private void payTest(String payType, String money) {
        PayRequest.getInstance().pay(this, "购买砖石", Tools.creatOrderNumber(), money, "abc987", payType, new PayCallback() {
            @Override
            public void payResult(int returnCode, String message) {
                tvShow.setText("支付结果：" + message + "   返回码：" + returnCode);
            }
        });
    }
}
