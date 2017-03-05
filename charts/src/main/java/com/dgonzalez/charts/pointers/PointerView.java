package com.dgonzalez.charts.pointers;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.dgonzalez.charts.utils.ColorUtils;
import com.dgonzalez.charts.utils.MissingParameterException;

import com.dgonzalez.charts.R;

/**
 * @author david.gonzalez (gonzalez.david31@gmail.com)
 */
public class PointerView extends View {

    private PointerObject pointerObj;
    private Bitmap pointerEnd;
    private PointerRenderer renderer;
    private RectF pointerBounds = new RectF();

    private Matrix transformation;

    public PointerView(Context context) {
        super(context);
    }

    public PointerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        processAttributes(context, attrs);
    }

    public PointerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        processAttributes(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public PointerView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        processAttributes(context, attrs);
    }

    private void processAttributes(Context context, AttributeSet attrs) {
        TypedArray array = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.PointerView,0,0);

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStrokeCap(Paint.Cap.ROUND);
        PointerRenderer renderer = new PointerRenderer(0,0, paint,
                context.getResources().getDimension(R.dimen.pointers_default_end_point_size));

        int primaryColor = ColorUtils.getPrimaryColor(context);
        // set default pointers renderer
        renderer.lineColorAndStroke.setColor(
                array.getColor(R.styleable.PointerView_line_color, primaryColor));
        renderer.lineColorAndStroke.setStrokeWidth(
                array.getDimension(
                        R.styleable.PointerView_stroke_width,
                        context.getResources().getDimension(R.dimen.pointers_default_stroke_width)));

        renderer.backgroundColor = array.getColor(
                R.styleable.PointerView_background_color,
                ContextCompat.getColor(context, R.color.pointers_default_background_color));
        renderer.symbolColor = array.getColor(
                R.styleable.PointerView_symbol_color, primaryColor);

        renderer.endPointSize = array.getDimension(R.styleable.PointerView_end_point_size, renderer.endPointSize);

        setRenderer(renderer);
    }

    public void setPointerObject(PointerObject pointerObj) {
        this.pointerObj = pointerObj;
        pointerEnd = createEndPointer();
    }

    public PointerObject getPointerObject() {
        return pointerObj;
    }


    protected Matrix getTransformation(){
        return transformation;
    }

    public void setRenderer(PointerRenderer renderer){
        this.renderer = renderer;
    }

    public void setEndPointerSize(float endPointerSize) {
        this.renderer.endPointSize = endPointerSize;
        pointerEnd = createEndPointer();
    }

    public void setTransformation(Matrix transformation) {
        this.transformation = transformation;
    }


    protected void checkParameters(){
        if(renderer == null){
            throw  new MissingParameterException("Missing pointer renderer ");
        }
        else if(renderer.lineColorAndStroke == null) {
            throw  new MissingParameterException("Missing paint of pointer renderer (used to draw line color and stroke)");
        }
        else if(pointerObj == null) {
            throw new MissingParameterException("Missing pointer object");
        }
        else if(pointerObj.getEnd() == null){
            throw new MissingParameterException("Missing end point of pointer object");
        }
        else if(pointerObj.getStart() == null) {
            throw new MissingParameterException("Missing start point of pointer object");
        }
        else if(this.renderer.endPointSize <= 0) {
            throw new MissingParameterException("Missing size of end pointer");
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if(changed){
            checkParameters();
        }
    }

    public PointF getMappedStart(){
        float[] point = new float[]{pointerObj.getStart().x, pointerObj.getStart().y};
        transformation.mapPoints(point);
        return new PointF(point[0], point[1]);
    }

    public PointF getMappedEnd(){
        float[] point = new float[]{pointerObj.getEnd().x, pointerObj.getEnd().y};
        transformation.mapPoints(point);
        return new PointF(point[0], point[1]);
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

        renderer.lineColorAndStroke.setStyle(Paint.Style.STROKE);
        canvas.drawLine(startX, startY, endX, endY, renderer.lineColorAndStroke);

        renderer.lineColorAndStroke.setStyle(Paint.Style.FILL);
        canvas.drawCircle(startX, startY, renderer.lineColorAndStroke.getStrokeWidth() * 2, renderer.lineColorAndStroke);

        float endHalfSize = ((float) this.renderer.endPointSize)/2.f;
        pointerBounds.set(endX - endHalfSize, endY - endHalfSize, endX + endHalfSize, endY + endHalfSize);
        canvas.drawBitmap(pointerEnd, pointerBounds.left, pointerBounds.top, renderer.lineColorAndStroke);

        canvas.save();
    }

    public Bitmap getPointerEnd(){
        return pointerEnd;
    }

    public RectF getPointerBounds()  {
        return pointerBounds;
    }

    protected Bitmap createEndPointer(){
        return PointerUtilities.createEndPointer(renderer.lineColorAndStroke, this.renderer.endPointSize, pointerObj.getEndOfPointer(), renderer.backgroundColor, renderer.symbolColor);
    }
}
