package com.dgonzalez.charts.pointers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.TypedValue;

import com.dgonzalez.charts.utils.InvalidInputException;

import com.dgonzalez.charts.R;

/**
 * @author david.gonzalez (gonzalez.david31@gmail.com)
 */
public class PointerUtilities {

    public final static int DEFAULT_POINTER_END_SIZE = 60;

    public static Bitmap createEndPointer(PointerObject object, PointerRenderer renderer) throws InvalidInputException{
        if(object == null) {
            throw new InvalidInputException("Unable to create end pointer : pointer object is null");
        }
        else if(renderer == null) {
            throw new InvalidInputException("Unable to create end pointer : renderer is null");
        }
        return PointerUtilities.createEndPointer(renderer.lineColorAndStroke, renderer.endPointSize, object.getEndOfPointer(), renderer.backgroundColor, renderer.symbolColor);
    }

    public static Bitmap createEndPointer(Paint paint, float pointerEndSize, Character symbol, int backgroundColor, int symbolColor){
        if(pointerEndSize<=0){
            return null;
        }

        Paint paint_background = new Paint();
        paint_background.setColor(backgroundColor);
        paint_background.setAntiAlias(true);
        int imgSide = (int)(pointerEndSize);
        Bitmap result = Bitmap.createBitmap(imgSide, imgSide, Bitmap.Config.ARGB_8888);
        Canvas canvas= new Canvas(result);
        float endHalfSize = pointerEndSize/2.f;
        canvas.drawCircle(endHalfSize, endHalfSize, endHalfSize - paint.getStrokeWidth(), paint_background);

        paint.setStyle(Paint.Style.STROKE);
        canvas.drawCircle(endHalfSize, endHalfSize, endHalfSize - paint.getStrokeWidth(), paint);
        paint.setStyle(Paint.Style.FILL);

        Paint textPaint = new Paint(paint);
        textPaint.setColor(symbolColor);
        textPaint.setTextSize(pointerEndSize * 0.6f);
        textPaint.setTextAlign(Paint.Align.CENTER);
        Rect textBounds = new Rect();
        if(symbol != null){
            String text = String.valueOf(symbol);
            textPaint.getTextBounds(text, 0, 1, textBounds);
            float width = textPaint.measureText(text, 0, 1);
            float heightShift = (pointerEndSize - textBounds.height()) / 2.f;
            canvas.drawText(text,
                    (pointerEndSize - width) / 2.f + width/2.f,
                    heightShift + textBounds.height(),
                    textPaint);
        }
        canvas.save();
        return result;
    }
}
