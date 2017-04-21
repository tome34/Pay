package com.payment.poliy;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.replay.limty.control.TestPay;
import com.replay.limty.model.common.PayCallback;
import com.replay.limty.model.lthf.LTPayment;
import com.replay.limty.model.lthf.LiantongPay;
import com.replay.limty.utils.Tools;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "扫码支付";
    private EditText etPhone,etMoney,etCode;
    private TextView tvShow;
    public static final String APP_ID = "wx11f5f201b0b1a42e";//测试用的APP_ID
    public static final String MCH_ID = "102510199737";//测试用的APP_ID
    public static final String MCH_KEY = "09a7a739c1874a63b1448e42981d29f5";//测试用的APP_ID
    public static final String PARTNER_ID = "zj0001";//测试用的客户id

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etPhone = (EditText) findViewById(R.id.pay_phone);
        etMoney = (EditText) findViewById(R.id.pay_phone_2);
        etCode = (EditText) findViewById(R.id.pay_phone_3);
        tvShow = (TextView) findViewById(R.id.textView);
        TestPay.getInstance().init(this,APP_ID,PARTNER_ID,"6A16823ED6305BB22EF92BC703CDD8AE");
    }


    private void gotoLPpay() {
        LTPayment.pay(this,"13048991673","phone6","15622999",Tools.creatOrderNumber(),"0.01");
    }


    public void doPay(View view) {
        switch (view.getId()) {
            case R.id.button1:
                pay("1");
                break;
            case R.id.button3:
                //gotoWxh5pay();
                //PayRequest.h5Request(this,APP_ID,MCH_ID,MCH_KEY);

                break;
            case R.id.button4:
                //pay(etPhone.getText().toString());
                payTest();
                //GzhRequest.request(this,"","7551000001","9d101c97133837e13dde2d32a5054abb");
                break;
            case R.id.button5:
                gotoLPpay();
                break;

            case R.id.button6:
                String code = etCode.getText().toString().trim();
                LiantongPay.sendVerificationCode(this,code);
                break;
        }
    }

    private void pay(String money) {

    }

    private void payTest() {
        TestPay.getInstance().pay(this, "手表", Tools.creatOrderNumber(), "1", "abc987", "2000", new PayCallback() {
            @Override
            public void payResult(int returnCode, String message) {
                Log.i(TAG, "payTest.payResult: returnCode="+returnCode+"--message="+message);
            }
        });
    }
}
