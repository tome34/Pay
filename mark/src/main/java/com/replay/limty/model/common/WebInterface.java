package com.replay.limty.model.common;

import android.app.Activity;

/**
 * Created by YCH on 17/5/14.
 */
public interface WebInterface {

    void openWxWeb(Activity activity,String string);

    void aliPay(Activity activity,String url);
}
