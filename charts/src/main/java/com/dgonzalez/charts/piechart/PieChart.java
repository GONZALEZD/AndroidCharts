package com.dgonzalez.charts.piechart;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import fr.dgonzalez.charts.R;

/**
 * @author david.gonzalez (gonzalez.david31@gmail.com)
 * @since 12/08/2016
 */
public class PieChart extends FrameLayout{
    private static final int ANIM_MOVE_MS = 200;

    private List<SliceOfPie> parts;

    private double total;

    private Paint bordersPainter;
    private Paint piesPainter;
    private Paint textPainter;

    private SliceOfPie sliceShown;

    private SliceOfPieView sliceView;
    private SliceOfPieView pieView;

    private PieChartAttributesHolder attributes;

    private OnSliceClickListener clickListener;

    public PieChart(Context context) {
        super(context);
        init(context, null);
    }

    public PieChart(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public PieChart(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public PieChart(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs){
        attributes = PieChartAttributesHolder.parse(context, attrs);
        parts = new ArrayList<>();
        total = 0;
        bordersPainter = new Paint();
        bordersPainter.setColor(attributes.borderColor);
        bordersPainter.setStyle(Paint.Style.STROKE);
        bordersPainter.setAntiAlias(true);
        piesPainter = new Paint();
        piesPainter.setStyle(Paint.Style.FILL);
        piesPainter.setAntiAlias(true);
        textPainter = new Paint();
        textPainter.setAntiAlias(true);
        textPainter.setColor(attributes.textColor);
        textPainter.setTextSize(attributes.textSize);

        sliceView = new SliceOfPieView(context);
        pieView = new SliceOfPieView(context);
        addView(pieView, 0);
        addView(sliceView, 1);
    }

    public void addSlice(SliceOfPie slice) throws BadValueException{
        if(slice == null){
            throw new NullPointerException("Slice of chartPie cannot be null");
        }
        if(slice.getName() == null || slice.getName().isEmpty()){
            throw new BadValueException("Incorrect name (must not be null and empty)");
        }
        if(slice.getValue()<=0){
            throw new BadValueException("Incorrect value of a slice (must be strictly higher than 0)");
        }

        parts.add(slice);
        total += slice.getValue();
    }

    @Override
    @Deprecated
    public void setOnClickListener(OnClickListener l) {
        super.setOnClickListener(l);
    }


    public void setOnSliceClickListener(OnSliceClickListener listener){
        clickListener = listener;
    }

    public interface OnSliceClickListener{
        public void onClick(SliceOfPie itemClicked);
    }

    private void createBitmaps(SliceOfPie sliceToShow, boolean invalidateViews){
        // find center point
        Rect area = new Rect();
        getDrawingRect(area);
        if(area.width() <= 0 || area.height() <= 0){
            return;
        }
        int centerX = area.centerX();
        int centerY = area.centerY();

        // compute radius
        int radius = area.width()>area.height()? area.height()/2 : area.width()/2;
        radius -= attributes.outerBorderWidth + attributes.animationDistance;

        // initialize local variables
        Bitmap sliceBitmap = null;

        Bitmap pieBitmap = Bitmap.createBitmap(area.width(), area.height(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(pieBitmap);
        Canvas sliceCanvas = null;
        double percent = 0;
        double sum = 0;
        Path path = new Path();
        path.setFillType(Path.FillType.EVEN_ODD);
        Path outerBorder = new Path();
        RectF arc = new RectF();
        arc.set(centerX-radius, centerY-radius, centerX+radius, centerY+radius);
        int x=0, y=0;
        Canvas currentCanvas;
        for(SliceOfPie part : parts){
            path.reset();

            path.moveTo(centerX, centerY);

            sum += part.getValue();
            double newPercent = sum / total;

            path.arcTo(arc, (float)percent*360,(float)(newPercent-percent)*360, true);
            path.lineTo(centerX, centerY);
            path.close();

            // paint text
            String txt;
            if(attributes.textInPercent){
                txt = String.valueOf((int)((part.getValue()*100)/total))+ "%";
            }
            else{
                txt = String.valueOf((int)part.getValue());
            }

            Rect txtBounds = new Rect();
            textPainter.getTextBounds(txt, 0, txt.length(),txtBounds);

            // compute the outer border path
            outerBorder.reset();
            outerBorder.arcTo(arc, (float)percent*360,(float)(newPercent-percent)*360, true);

            // retrieve the middle point of the path
            PathMeasure pm = new PathMeasure(outerBorder, false);
            float centerArc[] = {0f, 0f};
            pm.getPosTan(pm.getLength()*0.5f, centerArc, null);

            // draw created path
            piesPainter.setColor(part.getColor());

            if(part.equals(sliceToShow)){
                // create bitmap
                if(sliceBitmap == null) {
                    sliceBitmap = Bitmap.createBitmap(canvas.getWidth(), canvas.getHeight(), Bitmap.Config.ARGB_8888);
                    sliceCanvas = new Canvas(sliceBitmap);
                }

                currentCanvas = sliceCanvas;

                // compute x and y values used for animation
                x = (int) (attributes.animationDistance *Math.cos((percent + (newPercent-percent)/2.f)*Math.PI*2));
                y = (int) (attributes.animationDistance *Math.sin((percent + (newPercent-percent)/2.f)*Math.PI*2));
            }
            else{
                currentCanvas = canvas;
            }
            // draw
            bordersPainter.setStrokeWidth(attributes.outerBorderWidth /4);
            currentCanvas.drawPath(path, piesPainter);
            currentCanvas.drawPath(path, bordersPainter);

            bordersPainter.setStrokeWidth(attributes.outerBorderWidth);
            currentCanvas.drawPath(outerBorder, bordersPainter);

            float txtX = centerX + (centerArc[0] - centerX)*attributes.textPosition - txtBounds.width()/2;
            float txtY = centerY + (centerArc[1] - centerY)*attributes.textPosition + txtBounds.height()/2;
            currentCanvas.drawText(txt, txtX, txtY, textPainter);

            percent = newPercent;
        }

        pieView.setSlicesImage(pieBitmap);
        if(invalidateViews){
            pieView.invalidate();
        }
        // add slice animation
        if(sliceBitmap != null){
            sliceShown = sliceToShow;
            sliceView.setSliceToShow(sliceBitmap, x, y);
            sliceView.startAnimation(sliceView.getAnimation());
        }
        else {
            sliceShown = null;
        }
    }

    public void displayTextInPercent(boolean displayPercent){
        if(attributes.textInPercent != displayPercent){
            attributes.textInPercent = displayPercent;
            createBitmaps(sliceShown, true);
        }
    }

    public void setTextSize(float newSize){
        if(attributes.textSize != newSize){
            attributes.textSize = newSize;
            textPainter.setTextSize(attributes.textSize);
            createBitmaps(sliceShown, true);
        }
    }

    public void setTextPosition(float newPosition){
        if(attributes.textPosition!= newPosition){
            attributes.textPosition = newPosition;
            createBitmaps(sliceShown, true);
        }
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT);
    }

    public void showSlice(SliceOfPie slice){
        // retrieve slice to show
        createBitmaps(slice, true);

        if(clickListener != null){
            clickListener.onClick(slice);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if(changed){
            createBitmaps(null, false);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // compute angle of the point, regarding the center
        if(event.getAction() == MotionEvent.ACTION_UP){
            Rect area = new Rect();
            getDrawingRect(area);

            double dx = event.getX() - area.centerX();
            double dy = event.getY() - area.centerY();


            double angle = Math.atan2(dy,dx);
            if(angle < 0){
                angle += Math.PI*2;
            }

            double anglePercent = angle/(Math.PI*2);
            anglePercent *= total;
            double valueSum = 0;
            SliceOfPie clicked = parts.get(0);
            for(SliceOfPie slice : parts){
                valueSum += slice.getValue();
                if(valueSum > anglePercent){
                    clicked = slice;
                    break;
                }
            }
            showSlice(clicked);
        }
        return true;
    }

    private class SliceOfPieView extends View{

        private Bitmap slicesImage;

        public SliceOfPieView(Context context) {
            super(context);
        }

        public void setSlicesImage(Bitmap image){
            slicesImage = image;
        }

        public void setSliceToShow(Bitmap image, int toX, int toY){
            setSlicesImage(image);
            Animation anim = new TranslateAnimation(0, toX, 0, toY);
            anim.setDuration(ANIM_MOVE_MS);
            anim.setFillAfter(true);
            anim.setInterpolator(new LinearInterpolator());
            setAnimation(anim);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            if (slicesImage != null && slicesImage.isRecycled() == false){
                canvas.drawBitmap(slicesImage, 0, 0, null);
            }
        }
    }
}
