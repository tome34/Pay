package com.replay.limty.control;

import android.content.Context;

import com.replay.limty.model.common.PayCallback;

/**
 * Created by Administrator on 2017/4/17 0017.
 */

public interface PaybusInterface {

    void pay(Context context, String body, String orderNumber, String money, String attach, String payType, final PayCallback callBack);
}
