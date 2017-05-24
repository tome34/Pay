package com.payment.poliy.ok;

import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
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
    public static final String APP_ID = "40000";
    public static final String PARTNER_ID = "test0001";
    public static final String KEY = "b99e2e0822d133a5fe279ddfd114067f";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etMoney = (EditText) findViewById(R.id.et_money);
        tvShow = (TextView) findViewById(R.id.textView);
        PayRequest.getInstance().init(APP_ID, PARTNER_ID);

    }

    public void doPay(View view) {

        String money = etMoney.getText().toString();

        switch (view.getId()) {
            case R.id.btn_init:
//                PayRequest.getInstance().init(this,APP_ID,PARTNER_ID,"6A16823ED6305BB22EF92BC703CDD8AE");
//                TestAPI.sendRequest(this,"api测试",Tools.creatOrderNumber(),"1","abc887",TestAPI.WX_APP);
                //H5Request.h5Request(this,"102511038303","729f6b9679699275d66f335babd76a1e","1");

                Intent intent = new Intent(Intent.ACTION_VIEW);
                ComponentName cn = new ComponentName("com.tencent.mm", "com.tencent.mm.plugin.base.stub.WXCustomSchemeEntryActivity");
               // intent.setData(Uri.parse("weixin://dl/businessWebview/link/?appid=wxe75a2e68877315fb&url=https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx6fb989854b5583ed&redirect_uri=http%3A%2F%2Fwx.max-chip.com%2Fpages%2FWechatWebHook.aspx&response_type=code&scope=snsapi_base&state=20170523135025023GA&connect_redirect=1#wechat_redirect"));
                //intent.setData(Uri.parse("weixin://wap/pay?appid%3Dwx2421b1c4370ec43b%26noncestr%3D3e84679af4efab5f32ee9ea01b2ec290%26package%3DWAP%26prepayid%3Dwx20160504154919fdacd7bc0d0127918780%26timestamp%3D1462348159%26sign%3DC40DC4BB970049D6830BA567189B463B"));
                intent.setData(Uri.parse("weixin://wap/pay?"));
                intent.setFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
                intent.setComponent(cn);
                startActivity(intent);

                /*String var1 = "com.tencent.mm";
                String var2 = "com.tencent.mm.plugin.base.stub.WXCustomSchemeEntryActivity";
                Intent intent = new Intent();
                intent.setData(Uri.parse("weixin://dl/businessWebview/link/?appid=wxe75a2e68877315fb&url=https%3A%2F%2Fopen.weixin.qq.com%2Fconnect%2Foauth2%2Fauthorize%3Fappid%3Dwx6fb989854b5583ed%26redirect_uri%3Dhttp%253A%252F%252Fwx.max-chip.com%252Fpages%252FWechatWebHook.aspx%26response_type%3Dcode%26scope%3Dsnsapi_base%26state%3D20170523152140023DI%23wechat_redirect"));
                intent.setAction(Intent.ACTION_VIEW);
                ComponentName com = new ComponentName(var1, var2);
                intent.addCategory("android.intent.category.BROWSABLE");
                intent.putExtra("translate_link_scene", 1);
                intent.setComponent(com);
                Log.i("测试", "com: "+com);
                this.startActivity(intent);*/



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
