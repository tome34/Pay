package com.replay.limty.model.common;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by Administrator on 2017/5/15 0015.
 */

public class ShowQrLinearLayout extends LinearLayout{

    private LinearLayout titleLinearLayout;
    private TextView titleBarBack;
    private TextView titleName;
    private ImageView qrImage;
    private Bitmap bitmap;
    private Button btnSaveQr;
    private Button btnOpenAPP;
    private LinearLayout tvLinearLayout;
    private TextView tvOperationInstructions1;
    private TextView tvOperationInstructions2;

    public ShowQrLinearLayout(Context context) {
        super(context);
        init(context);
    }

    public ShowQrLinearLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context){

        ActionBar.LayoutParams layoutParams = new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        this.setLayoutParams(layoutParams);
        this.setBackgroundColor(Color.parseColor("#e4e4e4"));
        ActionBar.LayoutParams titleLinearLayoutParams = new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dip2px(40f));
        titleLinearLayout = new LinearLayout(context);
        titleLinearLayout.setLayoutParams(titleLinearLayoutParams);
        titleLinearLayout.setBackgroundColor(Color.parseColor("#333333"));
        titleBarBack = new TextView(context);
        titleBarBack.setText("<");
        titleBarBack.setTextColor(Color.parseColor("#ffffff"));
        titleBarBack.setLayoutParams(new ActionBar.LayoutParams(dip2px(40f),dip2px(40f)));
        titleBarBack.setTextSize(sp2px(20f));
        titleBarBack.setPadding(dip2px(10f),0,0,0);
        titleLinearLayout.addView(titleBarBack);
        titleBarBack.setGravity(Gravity.CENTER_VERTICAL);
        addView(titleLinearLayout);

        loadBitmap(context);
    }

    public void setQrBitmap(Bitmap bitmap){
        this.bitmap = bitmap;
    }

    private void loadBitmap(Context context){
        qrImage = new ImageView(context);
        qrImage.setLayoutParams(new ActionBar.LayoutParams(dip2px(240f),dip2px(240f)));
        qrImage.setImageBitmap(bitmap);
        this.addView(qrImage);
    }


    public int dip2px(float var1) {
        float var2 = this.getResources().getDisplayMetrics().density;
        return (int)(var1 * var2 + 0.5F);
    }

    public int sp2px(float var1) {
        float var2 = this.getResources().getDisplayMetrics().scaledDensity;
        return (int)(var1 * var2 + 0.5F);
    }
}
