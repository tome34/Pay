package com.replay.limty.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.switfpass.pay.utils.MD5;
import com.switfpass.pay.utils.SignUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Tools {

    public static String getIMEI(Context act) {
        String IMEI = ((TelephonyManager) act.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
        return IMEI;
    }

    public static String getIMSI(Context act) {
        TelephonyManager mPhoneyManager = (TelephonyManager) act.getSystemService(Context.TELEPHONY_SERVICE);
        String phoneIMSI = mPhoneyManager.getSubscriberId();
        return phoneIMSI;
    }

    public static String getICCID(Context act) {
        String cid = ((TelephonyManager) act.getSystemService(Context.TELEPHONY_SERVICE)).getSimSerialNumber();
        return cid;
    }

    public static String getMacAddress() {
        String result = "";
        String Mac = "";
        result = callCmd("busybox ifconfig", "HWaddr");
        if (result == null) {
            return "网络出错，请检查网络";
        }
        if (result.length() > 0 && result.contains("HWaddr")) {
            Mac = result.substring(result.indexOf("HWaddr") + 6, result.length() - 1);
            if (Mac.length() > 1) {
                result = Mac.toLowerCase();
            }
        }
        return result.trim();
    }

    public static String callCmd(String cmd, String filter) {
        String result = "";
        String line = "";
        try {
            Process proc = Runtime.getRuntime().exec(cmd);
            InputStreamReader is = new InputStreamReader(proc.getInputStream());
            BufferedReader br = new BufferedReader(is);
            while ((line = br.readLine()) != null && line.contains(filter) == false) {
            }
            result = line;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String getPhoneModel() {
        return Build.MODEL;
    }

    public static String getAndroidVersion() {
        return "android" + Build.VERSION.RELEASE;
    }

    public static int getVersionNumber(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            int version = info.versionCode;
            return version;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static String getCurrentTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        return dateFormat.format(new Date()).toString();
    }

    public static String getTime() {
        return String.valueOf(System.currentTimeMillis() / 1000);
    }

    public static String getAppProcessName(Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> infos = manager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo info : infos) {
            if (info.pid == pid)
                return info.processName;
        }
        return "";
    }

    public static String getPackageSign(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo packageInfo = manager.getPackageInfo(getAppProcessName(context), PackageManager.GET_SIGNATURES);
            String signValidString = getSignValidString(packageInfo.signatures[0].toByteArray());
            return signValidString.toUpperCase();
        } catch (Exception e) {
        }
        return "";
    }


    private static String getSignValidString(byte[] paramArrayOfByte) throws NoSuchAlgorithmException {
        MessageDigest localMessageDigest = MessageDigest.getInstance("MD5");
        localMessageDigest.update(paramArrayOfByte);
        return toHexString(localMessageDigest.digest());
    }

    public static String toHexString(byte[] paramArrayOfByte) {
        if (paramArrayOfByte == null) {
            return null;
        }
        StringBuilder localStringBuilder = new StringBuilder(2 * paramArrayOfByte.length);
        for (int i = 0; ; i++) {
            if (i >= paramArrayOfByte.length) {
                return localStringBuilder.toString();
            }
            String str = Integer.toString(0xFF & paramArrayOfByte[i], 16);
            if (str.length() == 1) {
                str = "0" + str;
            }
            localStringBuilder.append(str);
        }
    }

    public static String getPhoneNumber(Context act) {
        return ((TelephonyManager) act.getSystemService(Context.TELEPHONY_SERVICE)).getLine1Number();
    }

    public static String getHostIP() {
        String hostIp = null;
        try {
            Enumeration nis = NetworkInterface.getNetworkInterfaces();
            InetAddress ia = null;
            while (nis.hasMoreElements()) {
                NetworkInterface ni = (NetworkInterface) nis.nextElement();
                Enumeration<InetAddress> ias = ni.getInetAddresses();
                while (ias.hasMoreElements()) {
                    ia = ias.nextElement();
                    if (ia instanceof Inet6Address) {
                        continue;
                    }
                    String ip = ia.getHostAddress();
                    if (!"127.0.0.1".equals(ip)) {
                        hostIp = ia.getHostAddress();
                        break;
                    }
                }
            }
        } catch (SocketException e) {
            Log.i("yao", "SocketException");
            e.printStackTrace();
        }
        return hostIp;
    }

    public static String creatOrderNumber() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssddd");
        char c1 = (char) (int) (Math.random() * 26 + 97);
        char c2 = (char) (int) (Math.random() * 26 + 97);
        String paymentID = dateFormat.format(new Date()).toString() + String.valueOf(c1) + String.valueOf(c2);
        return paymentID.toUpperCase();
    }

    public static String createSign(String signKey, Map<String, String> params){
        StringBuilder buf = new StringBuilder((params.size() + 1) * 10);
        SignUtils.buildPayParams(buf, params, false);
        buf.append("&key=").append(signKey);
        String preStr = buf.toString();
        String sign = "";
        try{
            sign = MD5.md5s(preStr).toUpperCase();
        }catch (Exception e){
            sign = MD5.md5s(preStr).toUpperCase();
        }
        return sign;
    }

    public static String getNonceStr(){
        Random random = new Random();
        return MD5.getMessageDigest(String.valueOf(random.nextInt(10000)).getBytes());
    }
}

