package com.replay.limty.http;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by YCH on 16/10/13.
 */
public class VolleyRequst {

    public static VolleyRequst instance;
    public static RequestQueue queue;

    private VolleyRequst(Context context) {
        queue = Volley.newRequestQueue(context);
    }

    public static VolleyRequst getInstance(Context context) {
        if (instance == null) {
            instance = new VolleyRequst(context);
        }
        return instance;
    }

    public void postXmlRequset(String url, String tag, String xmlString, VolleyInterface vif) {
        queue.cancelAll(tag);
        XmlRequest xmlRequest = new XmlRequest(Request.Method.POST, url, xmlString, vif.getXmlListener(), vif.getErrorListener());
        xmlRequest.setTag(tag);
        xmlRequest.setRetryPolicy(new DefaultRetryPolicy(
                40 * 1000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        queue.add(xmlRequest);
    }

    public void postJosnRequst(String url, String tag, JSONObject parsa, VolleyInterface vif) {
        VolleyRequst.queue.cancelAll(tag);
        JsonObjectRequest jsonRequest = new JsonObjectRequest(url, parsa, vif.getmJsonListener(), vif.getErrorListener());
        jsonRequest.setTag(tag);
        jsonRequest.setRetryPolicy(new DefaultRetryPolicy(
                40 * 1000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        VolleyRequst.queue.add(jsonRequest);
    }

    public void postJosnRequsts(String url, String tag, JSONObject parsa, VolleyInterface vif) {
        VolleyRequst.queue.cancelAll(tag);
        final String time = (String) parsa.remove("time");
        final String up = (String) parsa.remove("up");

        JsonObjectRequest jsonRequest = new JsonObjectRequest(url, parsa, vif.getmJsonListener(), vif.getErrorListener()) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("timestamp", time);
                headers.put("Authorization", up);
                return headers;
            }
        };
        jsonRequest.setTag(tag);
        jsonRequest.setRetryPolicy(new DefaultRetryPolicy(
                40 * 1000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        jsonRequest.setShouldCache(true);
        VolleyRequst.queue.add(jsonRequest);
    }
}
