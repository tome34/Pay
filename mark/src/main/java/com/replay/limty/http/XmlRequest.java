package com.replay.limty.http;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;

import java.io.UnsupportedEncodingException;

public class XmlRequest extends XmlStringRequest<String>{

	public XmlRequest(String url, Listener<String> listener, ErrorListener errorListener) {
        super(Request.Method.GET, url, null, listener, errorListener);
    }
	
	public XmlRequest(int method, String url, Listener<String> listener, ErrorListener errorListener) {
        super(method, url, null, listener, errorListener);
    }
	
	public XmlRequest(int method, String url, String jsonRequest,Listener<String> listener, ErrorListener errorListener) {
        super(method, url, (jsonRequest == null) ? null : jsonRequest.toString(), listener,errorListener);
    }

	public XmlRequest(String url, String jsonRequest, Listener<String> listener,
            ErrorListener errorListener) {
        this(jsonRequest == null ? Request.Method.GET : Request.Method.POST, url, jsonRequest,listener, errorListener);
    }

	@Override
	protected Response<String> parseNetworkResponse(NetworkResponse response) {
		try {  			
			String xmlString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
			
			return Response.success(xmlString, HttpHeaderParser.parseCacheHeaders(response));  
		} catch (UnsupportedEncodingException e) {  
			return Response.error(new ParseError(e));  
		} catch (Exception e) {  
			return Response.error(new ParseError(e));
		}  
	}	
}
