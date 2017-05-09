package com.replay.limty.model.wxqr;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.replay.limty.R;
import com.replay.limty.control.PayCallback;
import com.replay.limty.control.PayRequest;
import com.replay.limty.model.common.AsyncData;
import com.replay.limty.model.common.WxTools;
import com.replay.limty.utils.ToastTools;
import com.switfpass.pay.utils.XmlUtils;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class ShowActivity extends AppCompatActivity {

    public static final String TAG = "Query";
    private String urlText;
    private Bitmap qrBitmap;
    private ImageView qrCode;
    private WxTools wxTools;
    private PayCallback callback;
    private String mch_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);

        Intent intent = getIntent();
        String body = intent.getStringExtra("body");

        qrCode = (ImageView) findViewById(R.id.qrCode);
        wxTools = new WxTools(this);
        try {
            show(body);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (AsyncData.callBack != null) {
            this.callback = AsyncData.callBack;
        }
    }

    public void doClick(View view) {
        int i = view.getId();
        if (i == R.id.btnWxPay) {
            AsyncData.getInstance().sendPaymentState(5000, this);
            wxTools.shareText(urlText);
        } else if (i == R.id.back) {
            Query.payState(this, AsyncData.orderInfo.getOrderNumber(), mch_id, handler);
        } else if (i == R.id.saveQr) {
            saveBitmap(qrBitmap);
        } else if(i == R.id.goWX){
            IWXAPI api = WXAPIFactory.createWXAPI(this, PayRequest.appID, false);
            api.registerApp(PayRequest.appID);
            api.openWXApp();
        }
    }

    public void saveBitmap(Bitmap bitmap) {
        File appDir = new File(Environment.getExternalStorageDirectory(), "微信二维码");
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = "payQR.png";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            boolean isSuccess = bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();

            if (isSuccess) {
                ToastTools.show(this, "保存成功");
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + file.getAbsolutePath())));
                findViewById(R.id.saveQr).setBackgroundResource(R.drawable.pressed);
                findViewById(R.id.saveQr).setEnabled(false);
            } else {
                ToastTools.show(this, "保存失败");
            }
        } catch (Exception e) {
            ToastTools.show(this, "保存失败" + e.toString());
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        Query.payState(this, AsyncData.orderInfo.getOrderNumber(), mch_id, handler);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == Query.RE_SUCCESS_BACK) {
                Bundle bundle = msg.getData();
                String body = bundle.getString(Query.RE_KEY);
                if (body != null) {
                    Log.i(TAG, "handleMessage: " + body);
                    Map<String, String> result = XmlUtils.parse(body);
                    handleResult(result);
                }
            }
        }
    };

    private void handleResult(Map<String, String> result) {
        if (result.get("status").equals("0")) {
            if (result.get("result_code").equals("0")) {
                String payState = result.get("trade_state").toUpperCase();
                backResult(payState);
            } else {
                this.callback.payResult(5001, result.get("err_msg"));
            }
        } else {
            callback.payResult(5002, result.get("message"));
        }
    }

    private void backResult(String payState) {
        switch (payState) {
            case "SUCCESS":
                callback.payResult(0, "支付成功");
                finish();
                break;
            case "REFUND":
                callback.payResult(-1, "已转入退款");
                finish();
                break;
            case "NOTPAY":
                ToastTools.showDialog(this);
                callback.payResult(-2, "未支付");
                break;
            case "CLOSED":
                callback.payResult(-3, "订单已关闭");
                finish();
                break;
            case "PAYERROR":
                callback.payResult(-4, "支付失败");
                finish();
                break;
        }
    }

    private void show(String body) throws JSONException {
        JSONObject json = new JSONObject(body);
        final String code_img_url = json.optString("code_img_url");
        urlText = json.optString("code_url");
        mch_id = json.optString("mch_id");

        new Thread(new Runnable() {
            @Override
            public void run() {
                qrBitmap = getBitmap(code_img_url);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        qrCode.setImageBitmap(qrBitmap);
                    }
                });
            }
        }).start();
    }

    private Bitmap getBitmap(String urlBitmap) {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(urlBitmap);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Charset", "UTF-8");
            connection.setConnectTimeout(5 * 1000);
            if (connection.getResponseCode() == 200) {
                BufferedInputStream bis = new BufferedInputStream(connection.getInputStream());
                Bitmap bitmap = BitmapFactory.decodeStream(bis);
                bis.close();
                return bitmap;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            connection.disconnect();
        }
        return null;
    }
}
