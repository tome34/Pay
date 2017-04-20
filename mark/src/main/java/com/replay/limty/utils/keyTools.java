package com.replay.limty.utils;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/4/15 0015.
 */

public class keyTools {

    public static boolean checkSign(Context context,String packageSign){
        return packageSign.equals(Tools.getPackageSign(context));
    }

    public static String getKey(String time){
        String key = "";
        int i1 = (getInt(time.charAt(0)) * getInt(time.charAt(time.length()-1)))%10;
        int i2 = (getInt(time.charAt(1)) + getInt(time.charAt(time.length()-1)))%10;
        int i3 = (getInt(time.charAt(2)) * getInt(time.charAt(time.length()-2)))%10;
        int i4 = (getInt(time.charAt(3)) + getInt(time.charAt(time.length()-2)))%10;
        int i5 = (getInt(time.charAt(4)) * getInt(time.charAt(time.length()-3)))%10;
        int i6 = (getInt(time.charAt(5)) + getInt(time.charAt(time.length()-3)))%10;
        key = "0"+creatkey(i1, i2)+creatkey(i2, i3)+creatkey(i3, i4)+
                creatkey(i4, i5)+creatkey(i5, i6)+creatkey(i6, i1)+"0";
        return key;
    }

    private static int getInt(char c){
        String s = String.valueOf(c);
        int i = Integer.valueOf(s);
        return i;
    }

    private static String creatkey(int x,int y){
        List<String> arr = new ArrayList<>();
        arr.add("~!@#$%^&*(");
        arr.add("`123456789");
        arr.add("qwertyuiop");
        arr.add("asdfghjkl;");
        arr.add("zxcvbnm,./");
        arr.add("ZXCVBNM<>?");
        arr.add("qazwsxedcr");
        arr.add("<>?:[{}|-=");
        arr.add("987654321o");
        arr.add("PLMOKNIJBU");
        return arr.get(x).substring(y, y+1);
    }
}
