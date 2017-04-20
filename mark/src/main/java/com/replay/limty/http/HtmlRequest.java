package com.replay.limty.http;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;

import java.io.UnsupportedEncodingException;

/**
 * Created by Administrator on 2017/4/10 0010.
 */

public class HtmlRequest extends JsonRequest<String> {

    public HtmlRequest(String url, String requestBody, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(url, requestBody, listener, errorListener);
    }

    public HtmlRequest(int method, String url, String requestBody, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(method, url, requestBody, listener, errorListener);
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {

        try {
            String htmlString = new String(response.data,HttpHeaderParser.parseCharset(response.headers, PROTOCOL_CHARSET));
            return Response.success(htmlString,HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (Exception je) {
            return Response.error(new ParseError(je));
        }
    }
}
