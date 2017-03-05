package com.dgonzalez.charts.pointers;

import android.support.annotation.Nullable;
import android.view.MotionEvent;
import android.view.View;

/**
 * @author david.gonzalez (gonzalez.david31@gmail.com)
 */
public interface OnUserTouchListener {
    public void onTouchImage(MotionEvent event, int imageX, int imageY, @Nullable PointerView pointerView);
    public void onTouchOutsideImage(MotionEvent event);
    public void onPointerClicked(MotionEvent event, PointerView pointer, PointerObject object);
}

