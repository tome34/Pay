package com.carch.ndkdemo;

public class GetString {

	private String key;
	private String mch;
	private String appid;
	private static GetString instance;

	private GetString(){
	}

	public static GetString getInstance(){
		if(instance == null){
			instance = new GetString();
		}
		return instance;
	}

	public void setKey(String key){
		this.key = key;
	}

	public String getKey(){
		return key;
	}

	public void setMch(String mch){
		this.mch = mch;
	}

	public String getMch(){
		return mch;
	}

	public void setAppid(String appId){
		this.appid = appId;
	}

	public String getAppid(){
		return appid;
	}

	public static native String getVersion();

	public static native String getStr();

	static{
		System.loadLibrary("Profile");
	}
}
