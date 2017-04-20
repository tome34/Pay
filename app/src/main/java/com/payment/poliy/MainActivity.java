package com.payment.poliy;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.replay.limty.control.TestPay;
import com.replay.limty.model.common.PayCallback;
import com.replay.limty.model.wxgzh.GzhRequest;
import com.replay.limty.model.wxh5.PayRequest;
import com.replay.limty.utils.Tools;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "扫码支付";
    private EditText etPhone;
    private TextView tvShow;
    public static final String APP_ID = "wx11f5f201b0b1a42e";//测试用的APP_ID
    public static final String MCH_ID = "102510199737";//测试用的APP_ID
    public static final String MCH_KEY = "09a7a739c1874a63b1448e42981d29f5";//测试用的APP_ID
    public static final String PARTNER_ID = "zj0001";//测试用的客户id
    private static final String notify_url = "http://202.103.190.89:50/WXPAY/RcvMo.sy";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etPhone = (EditText) findViewById(R.id.pay_phone);
        tvShow = (TextView) findViewById(R.id.textView);
        TestPay.getInstance().init(this,APP_ID,PARTNER_ID,"6A16823ED6305BB22EF92BC703CDD8AE");
    }


    private void gotoAlipay() {
        String url = "https://spay3.swiftpass.cn/spay/payMoney?mchId=102510199740&mchName=%E4%B8%8A%E6%B5%B7%E4%B8%87%E5%A8%B1%E6%96%87%E5%8C%96%E4%BC%A0%E6%92%AD%E6%9C%89%E9%99%90%E5%85%AC%E5%8F%B88&userId=2236170&body=%E7%A7%BB%E5%8A%A8%E6%94%AF%E4%BB%98&client=platform&sign=CF79858A6FBA65DE11363752EBFB54BE";
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        Uri content_url = Uri.parse(url);
        intent.setData(content_url);
        startActivity(intent);
    }

    private void gotoWxh5pay() {
        String url = "http://wxpay.wxutil.com/mch/pay/h5.v2.php";
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        Uri content_url = Uri.parse(url);
        intent.setData(content_url);
        startActivity(intent);
    }


    public void doPay(View view) {
        switch (view.getId()) {
            case R.id.button1:
                pay("1");
                break;
            case R.id.button3:
                //gotoWxh5pay();
                PayRequest.h5Request(this,APP_ID,MCH_ID,MCH_KEY);

                break;
            case R.id.button4:
                //pay(etPhone.getText().toString());
                //payTest();
                GzhRequest.request(this,"","7551000001","9d101c97133837e13dde2d32a5054abb");
                break;
        }
    }

    private void pay(String money) {

    }

    private void payTest() {
        TestPay.getInstance().pay(this, "手表", Tools.creatOrderNumber(), "1", "abc987", "1000", notify_url, new PayCallback() {
            @Override
            public void payResult(int returnCode, String message) {
                Log.i(TAG, "payTest.payResult: returnCode="+returnCode+"--message="+message);
            }
        });
    }
}
