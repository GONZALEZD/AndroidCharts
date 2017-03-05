package com.dgonzalez.charts.utils;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.util.TypedValue;

import com.dgonzalez.charts.R;

/**
 * @author david.gonzalez (gonzalez.david31@gmail.com)
 */
public class ColorUtils {
    public static int getPrimaryColor(Context context) {
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
        int colorPrimary = typedValue.data;
        return colorPrimary;
    }

    public static boolean isLightColor(int color) {
        double darkness = 1-(0.299*Color.red(color) + 0.587*Color.green(color) + 0.114*Color.blue(color))/255;
        if(darkness<0.4){
            return true;
        }else{
            return false;
        }
    }
}
