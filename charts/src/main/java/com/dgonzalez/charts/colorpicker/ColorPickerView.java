package com.dgonzalez.charts.colorpicker;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.dgonzalez.charts.utils.ColorUtils;
import com.dgonzalez.charts.utils.InvalidInputException;

import java.util.ArrayList;
import java.util.List;

/**
 * @author david.gonzalez (gonzalez.david31@gmail.com)
 */
public class ColorPickerView extends View {

    private static final float STROKE_WIDTH = 6.f;

    private List<Integer> colors;
    private Paint painter;
    private Paint borderPainter;
    private Rect area = new Rect();
    private RectF arc = new RectF();
    private RectF arcSelected = new RectF();
    private Bitmap colorsBitmap;

    private int selectedColorIndex = -1;

    private boolean drawBorders = true;
    private boolean drawSelection = true;

    private OnColorPickedListener listener;

    public ColorPickerView(Context context) {
        super(context);
        init(context, null);
    }

    public ColorPickerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ColorPickerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ColorPickerView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        painter = new Paint();
        painter.setStrokeCap(Paint.Cap.ROUND);
        painter.setAntiAlias(true);
        painter.setStyle(Paint.Style.FILL);
        painter.setStrokeWidth(STROKE_WIDTH);

        borderPainter = new Paint();
        borderPainter.setStrokeCap(Paint.Cap.ROUND);
        borderPainter.setAntiAlias(true);
        borderPainter.setStyle(Paint.Style.STROKE);
        borderPainter.setColor(Color.WHITE);
        borderPainter.setStrokeWidth(STROKE_WIDTH);

        colors = getDefaultHueColors();
    }

    public void setColors(List<Integer> colorsList) throws InvalidInputException{
        if(colorsList == null ||colorsList.size()<2) {
            throw new InvalidInputException("Color list must contains at least 2 colors!");
        }
        this.colors = colorsList;
        this.colorsBitmap = drawColorsOnBitmap();
    }

    public List<Integer> getColors() {
        return colors;
    }

    public void setOnColorPickedListener(OnColorPickedListener listener) {
        this.listener = listener;
    }

    public void setDrawBorders(boolean drawBorders) {
        this.drawBorders = drawBorders;
    }

    private List<Integer> getDefaultHueColors() {
        List<Integer> rez = new ArrayList<>();
        float[] colorHSV =  new float[]{0,1,1};
        float nbColors = 12.f;
        float ratio = 360.f/nbColors;
        for(float i=0; i<nbColors; i++) {
            colorHSV[0] = i*ratio;
            rez.add(Color.HSVToColor(colorHSV));
        }
        return rez;
    }

    public void setDefaultColors(){
        colors = getDefaultHueColors();
        colorsBitmap = drawColorsOnBitmap();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if(changed) {
            getDrawingRect(area);
            if(area.width() <= 0 || area.height() <= 0){
                return;
            }

            float width3 = borderPainter.getStrokeWidth()*3;
            float width = borderPainter.getStrokeWidth();
            arc.set(area.left + width3,
                    area.top + width3,
                    area.right - width3,
                    area.bottom - width3);

            arcSelected.set(
                    area.left + width,
                    area.top + width,
                    area.right - width,
                    area.bottom - width);
            colorsBitmap = drawColorsOnBitmap();
        }

    }

    private Bitmap drawColorsOnBitmap(){
        Bitmap rez = Bitmap.createBitmap((int)area.width(), (int) area.height(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(rez);
        int centerX = area.centerX();
        int centerY = area.centerY();

        float fraction = 360.f/((float)colors.size());
        float startDegree = -90 - (colors.size()%2==0?0:fraction/2.f);

        Path path = new Path();
        path.setFillType(Path.FillType.EVEN_ODD);

        for(int i=0; i< colors.size(); i++) {
            path.reset();
            path.moveTo(centerX, centerY);
            path.arcTo(arc, startDegree, fraction, true);
            path.lineTo(centerX, centerY);
            path.close();

            startDegree += fraction;

            painter.setColor(colors.get(i));

            c.drawPath(path, painter);
            if(drawBorders) {
                c.drawPath(path, borderPainter);
            }
        }
        c.save();
        return rez;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int centerX = area.centerX();
        int centerY = area.centerY();

        float fraction = 360.f/((float)colors.size());
        float startDegree = -90 - (colors.size()%2==0?0:fraction/2.f);

        Path path = new Path();
        path.setFillType(Path.FillType.EVEN_ODD);

        if(drawSelection && selectedColorIndex >=0 && selectedColorIndex < colors.size()) {
            float start = startDegree + selectedColorIndex*fraction;
            path.reset();
            path.moveTo(centerX, centerY);
            path.arcTo(arcSelected, start, fraction, true);
            path.lineTo(centerX, centerY);
            path.close();
            painter.setShader(null);
            painter.setColor(ColorUtils.getPrimaryColor(getContext()));
            canvas.drawPath(path, painter);

        }
        canvas.drawBitmap(colorsBitmap,0, 0, painter);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // compute angle of the point, regarding the center
        Rect area = new Rect();
        getDrawingRect(area);

        double dx = event.getX() - area.centerX();
        double dy = event.getY() - area.centerY();

        float fraction = (float) Math.PI*2.f/((float)colors.size());
        double angle = Math.atan2(dy,dx);
        angle += (Math.PI + (colors.size()%2==0?0:fraction))/2.f;
        if(angle < 0){
            angle += Math.PI*2;
        }
        int colorIndex = (int)(angle/fraction);
        int color = colors.get(colorIndex);

        if(drawSelection){
            setSelectedColorIndex(colorIndex);
        }

        if(event.getAction() == MotionEvent.ACTION_UP && listener!=null){
            listener.onColorPicked(color, colorIndex);
        }
        return true;
    }
    public void setSelectedColorIndex(int colorIndex){
        selectedColorIndex = colorIndex;
        if(drawSelection) {
            invalidate();
        }
    }

    public int getSelectedColorIndex() {
        return selectedColorIndex;
    }

    public void setDrawSelection(boolean drawSelection) {
        this.drawSelection = drawSelection;
    }
}
