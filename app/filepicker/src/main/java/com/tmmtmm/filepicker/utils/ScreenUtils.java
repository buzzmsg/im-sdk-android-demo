package com.tmmtmm.filepicker.utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

public class ScreenUtils {

    public static int dipToPx(Context context, float dipValue) {
        float m = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * m + 0.5f);
    }


    public static float pxToDip(Context context, float px) {
        if (context == null) {
            return -1;
        }
        return px / context.getResources().getDisplayMetrics().density;
    }


    public static int spToPx(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }


    public static int getScreenWidthInPixel(Context context)
    {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        int screenW = outMetrics.widthPixels;
        wm = null;
        return screenW;
    }


    public static int getScreenHeightInPixel(Context context) {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        int screenH = outMetrics.heightPixels;
        wm = null;
        return screenH;
    }


    public static boolean isLandScape(Context context) {
        return getScreenWidthInPixel(context) > getScreenHeightInPixel(context);
    }


    public static int px2dp(Context context, float pxValue){
        return (int)(pxValue / context.getResources().getDisplayMetrics().density);
    }

}
