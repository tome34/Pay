package com.replay.limty.model.wxqr;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.replay.limty.R;
import com.replay.limty.control.PayRequest;
import com.replay.limty.model.common.AsyncData;
import com.replay.limty.utils.ToastTools;
import com.replay.limty.utils.Tools;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ShowActivity extends AppCompatActivity {

    public static final String TAG = "Query";
    private String urlText;
    private Bitmap qrBitmap;
    private ImageView qrCode;
    private String mch_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);

        Intent intent = getIntent();
        String body = intent.getStringExtra("body");

        qrCode = (ImageView) findViewById(R.id.qrCode);
        try {
            show(body);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void doClick(View view) {
        int i = view.getId();
        if (i == R.id.btnWxPay) {
            AsyncData.getInstance().sendPaymentState(5000, this);
        } else if (i == R.id.back) {
            Query.getInstance().payState(this, AsyncData.orderInfo.getOrderNumber(), mch_id, false);
        } else if (i == R.id.saveQr) {
            saveBitmap(qrBitmap);
        } else if(i == R.id.goWX){
            AsyncData.getInstance().sendPaymentState(5000, this);
            Tools.openWeChat(this);
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


            }
        } catch (Exception e) {
            ToastTools.show(this, "保存失败" + e.toString());
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        Query.getInstance().payState(this, AsyncData.orderInfo.getOrderNumber(), mch_id, false);
    }

    private void show(String body) throws JSONException {
        JSONObject json = new JSONObject(body);
        final String code_img_url = json.optString("code_img_url");
        urlText = json.optString("code_url");
        Log.d(TAG, "code_url 扫码二维码 :"+urlText);
        mch_id = json.optString("mch_id");
        PayRequest.key = json.optString("key");
        new Thread(new Runnable() {
            @Override
            public void run() {
                qrBitmap = getBitmap(code_img_url);
                Log.d(TAG, "code_img_url 扫码二维码 :"+code_img_url);
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
