package com.dgonzalez.charts.pointers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * @author david.gonzalez (gonzalez.david31@gmail.com)
 */
public class PointerView extends View {

    private PointerObject pointerObj;
    private Paint paint;
    private int endPointerSize;
    private Bitmap pointerEnd;

    private Matrix transformation;

    public PointerView(Context context) {
        super(context);
    }

    public PointerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setPointerObject(PointerObject pointerObj) {
        this.pointerObj = pointerObj;
        pointerEnd = createEndPointer();
    }

    public void setPaint(Paint paint){
        this.paint = paint;
    }

    public void setEndPointerSize(int endPointerSize) {
        this.endPointerSize = endPointerSize;
        pointerEnd = createEndPointer();
    }

    public void setTransformation(Matrix transformation) {
        this.transformation = transformation;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float startX = pointerObj.getStart().x;
        float startY = pointerObj.getStart().y;
        float endX = pointerObj.getEnd().x;
        float endY = pointerObj.getEnd().y;
        if(transformation != null){
            float[] src = new float[]{pointerObj.getStart().x, pointerObj.getStart().y, pointerObj.getEnd().x, pointerObj.getEnd().y};
            float[] dst = new float[4];
            transformation.mapPoints(dst, src);
            startX = dst[0];
            startY = dst[1];
            endX = dst[2];
            endY = dst[3];
        }

        paint.setStyle(Paint.Style.STROKE);
        canvas.drawLine(startX, startY, endX, endY, paint);

        paint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(startX, startY, paint.getStrokeWidth() * 2, paint);

        float endHalfSize = ((float) endPointerSize)/2.f;
        canvas.drawBitmap(pointerEnd, endX - endHalfSize, endY - endHalfSize, paint);
        canvas.save();
    }

    public Bitmap createEndPointer(){
        return PointerUtilities.createEndPointer(paint, endPointerSize, pointerObj.getEndOfPointer());
    }
}
