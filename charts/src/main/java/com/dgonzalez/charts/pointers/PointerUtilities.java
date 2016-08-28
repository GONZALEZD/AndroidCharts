package com.dgonzalez.charts.pointers;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

/**
 * @author david.gonzalez (gonzalez.david31@gmail.com)
 */
public class PointerUtilities {

    public final static int DEFAULT_POINTER_END_SIZE = 60;

    public static Bitmap createEndPointer(Paint paint, int pointerEndSize, Character symbol){
        if(pointerEndSize<=0 || symbol == null){
            return null;
        }

        int margin = 2;

        Paint paint_background = new Paint();
        paint_background.setARGB(255, 255, 255, 255);
        paint_background.setAntiAlias(true);

        Bitmap result = Bitmap.createBitmap(pointerEndSize+margin*2, pointerEndSize+margin*2, Bitmap.Config.ARGB_8888);
        Canvas canvas= new Canvas(result);
        float endHalfSize = ((float) pointerEndSize)/2.f;
        canvas.drawCircle(endHalfSize + margin, endHalfSize + margin, endHalfSize, paint_background);

        paint.setStyle(Paint.Style.STROKE);
        canvas.drawCircle(endHalfSize + margin, endHalfSize + margin, endHalfSize, paint);

        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(pointerEndSize * 0.75f);
        Rect textBounds = new Rect();
        if(symbol != null){
            String text = ""+symbol;
            paint.getTextBounds(text, 0, 1, textBounds);
            canvas.drawText(text,
                    (pointerEndSize - textBounds.width()) / 2.f,
                    (pointerEndSize - textBounds.height()) / 2.f + textBounds.height(),
                    paint);
        }
        canvas.save();
        return result;
    }
}
