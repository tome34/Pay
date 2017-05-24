package com.replay.limty.model.zfbqr;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.replay.limty.R;
import com.replay.limty.model.common.AsyncData;
import com.replay.limty.model.wxqr.Query;
import com.replay.limty.utils.Tools;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ZfbQrActivity extends AppCompatActivity {

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
            Tools.openAliPay(this);
            AsyncData.getInstance().sendPaymentState(5000, this);
        } else if (i == R.id.back) {
            Query.getInstance().payState(this, AsyncData.orderInfo.getOrderNumber(),mch_id, false);
        }
    }

    @Override
    public void onBackPressed() {
        Query.getInstance().payState(this, AsyncData.orderInfo.getOrderNumber(),mch_id, false);
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
