package com.replay.limty.http;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;

import org.json.JSONObject;

/**
 * Created by YCH on 16/9/25.
 */
public abstract class VolleyInterface {

    public static Listener<String> mListener;
    public static Listener<JSONObject> mJsonListener;
    public static ErrorListener mErrorListener;
    public static Listener<String> mXmlListener;

    public VolleyInterface(Listener<String> listener, Listener<JSONObject> jsonListener,ErrorListener errorListener) {
        VolleyInterface.mListener = listener;
        VolleyInterface.mErrorListener = errorListener;
        VolleyInterface.mJsonListener = jsonListener;
    }

    public VolleyInterface(Listener<String> listener,ErrorListener errorListener){
        VolleyInterface.mXmlListener = listener;
        VolleyInterface.mErrorListener = errorListener;
    }

    public Listener<String> getListener() {
        mListener = new Listener<String>() {
            @Override
            public void onResponse(String s) {
                onSuccess(s);
            }
        };
        return mListener;
    }

    public Listener<JSONObject> getmJsonListener(){
       try{
           mJsonListener = new Listener<JSONObject>() {
               @Override
               public void onResponse(JSONObject response) {
                   onSuccess(response.toString());
               }
           };
       }catch (Exception e){
       }
        return mJsonListener;
    }

    public Listener<String> getXmlListener(){
        mXmlListener = new Listener<String>() {

            @Override
            public void onResponse(String response) {
                onSuccess(response.toString());
            }
        };
        return mXmlListener;
    }

    public ErrorListener getErrorListener() {

        mErrorListener = new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                onError(volleyError);
            }
        };
        return mErrorListener;
    }

    public abstract void onSuccess(String result);

    public abstract void onError(VolleyError volleyError);
}
