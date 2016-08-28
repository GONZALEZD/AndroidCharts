package com.dgonzalez.charts.pointers;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import fr.dgonzalez.charts.R;

/**
 * @author david.gonzalez (gonzalez.david31@gmail.com)
 */
public class PointersContainerView extends FrameLayout {

    private static final float MIN_SCALE = 1.0f;
    private static final float MAX_SCALE = 5.0f;

    private ImageView background;
    private ViewGroup container;

    private Matrix transform;
    private Matrix previousTransform;
    private Matrix computeMatrix;

    private float[] transformTab;

    private float scaleFactor;
    private float focusX, focusY;
    private float shiftX, shiftY;
    private  float scaledShiftX, scaledShiftY;

    private ScaleGestureDetector scaleDetector;
    private GestureDetector moveDetector;

    private PointerAdapter<PointerObject> dataAdapter;

    private float minScaleFactor, maxScaleFactor;

    private int imageWidth, imageHeight;

    public PointersContainerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public PointersContainerView(Context context) {
        super(context);
        init(null);
    }

    public void setDataAdapter(PointerAdapter adapter){
        this.dataAdapter = adapter;
        createPointers();
    }

    public void setBackgroundImage(Drawable image){
        background.setImageDrawable(image);
    }

    private void createPointers(){
        if(this.dataAdapter == null){
            return;
        }
        for(int i=0; i< dataAdapter.getCount(); i++){
            PointerView child = (PointerView) dataAdapter.getView(i, null, container);
            child.setX(0);
            child.setY(0);
            container.addView(child,
                    new FrameLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT));
        }

    }

    private void init(AttributeSet attrs){
        dataAdapter = null;
        transform = new Matrix();
        scaleFactor = 1.f;
        focusX =0;
        focusY=0;
        shiftX = 0;
        shiftY = 0;
        scaledShiftX = 0;
        scaledShiftY = 0;
        transformTab = new float[9];
        transformTab[Matrix.MSCALE_X] = 1.f;
        transformTab[Matrix.MSCALE_Y] = 1.f;
        transformTab[Matrix.MTRANS_X] = 0.f;
        transformTab[Matrix.MTRANS_Y] = 0.f;
        previousTransform = new Matrix();
        computeMatrix = new Matrix();

        background = new ImageView(getContext());
        background.setScaleType(ImageView.ScaleType.MATRIX);
        container = new FrameLayout(getContext());
        container.setPivotX(0);
        container.setPivotY(0);
        addView(background,0,
                new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        addView(container, 1,
                new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        scaleDetector = new ScaleGestureDetector(getContext(), new PointerScaleListener());
        moveDetector = new GestureDetector(getContext(),new PointerMoveListener());
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return PointersContainerView.this.onTouch(v, event);
            }
        });
        if(attrs != null){
            processAttributes(attrs);
        }
    }

    private boolean onTouch(View v, MotionEvent event){
        scaleDetector.onTouchEvent(event);
        moveDetector.onTouchEvent(event);
//        transform.reset();
//        transform.postScale(scaleFactor, scaleFactor, focusX, focusY);
//        transform.postTranslate(shiftX, shiftY);

        background.setImageMatrix(transform);

        computeMatrix.setConcat(transform, previousTransform);
        computeMatrix.getValues(transformTab);

        container.setScaleX(transformTab[Matrix.MSCALE_X]);
        container.setScaleY(transformTab[Matrix.MSCALE_Y]);
        container.setTranslationX(transformTab[Matrix.MTRANS_X]);
        container.setTranslationY(transformTab[Matrix.MTRANS_Y]);

        if(event.getAction() == MotionEvent.ACTION_UP){
            container.setTranslationX(0.f);
            container.setTranslationY(0.f);
            container.setScaleX(1.f);
            container.setScaleY(1.f);
            transform.invert(previousTransform);

            for(int i=0; i<container.getChildCount(); i++){
                PointerView pointer = (PointerView) container.getChildAt(i);
                pointer.setTransformation(transform);
                pointer.invalidate();
            }
        }
        return true;
    }

    private void processAttributes(AttributeSet attrs){

        TypedArray array = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.PointersContainerView,0,0);
        // retrieve pointers container image
        int imgId = array.getResourceId(R.styleable.PointersContainerView_image, -1);
        if(imgId != -1){
            setBackgroundResource(imgId);
        }
        // retrieve minimum and maximum scale factor
        setMinimumScale(array.getFloat(R.styleable.PointersContainerView_minimum_scale, MIN_SCALE));
        setMaximumScale(array.getFloat(R.styleable.PointersContainerView_maximum_scale, MAX_SCALE));

    }

    private void updateImageSize(){
        BitmapDrawable drawable = (BitmapDrawable)background.getDrawable();
        imageWidth = drawable.getIntrinsicWidth();
        imageHeight= drawable.getIntrinsicHeight();
    }

    public void setMinimumScale(float minScale){
        minScaleFactor = minScale;
    }
    public void setMaximumScale(float maxScale){
        maxScaleFactor = maxScale;
    }

    @Override
    public void setBackgroundResource(int resid) {
        if(background != null) {
            background.setImageResource(resid);
            updateImageSize();
        }
    }

    @Override
    public void setBackground(Drawable background) {
        if(this.background != null) {
            this.background.setImageDrawable(background);
            updateImageSize();
        }
    }

    @Override
    public void setBackgroundDrawable(Drawable background) {
        if(background != null) {
            this.background.setImageDrawable(background);
            updateImageSize();
        }
    }

    private class PointerScaleListener implements ScaleGestureDetector.OnScaleGestureListener {

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            scaleFactor *= detector.getScaleFactor();
            if(scaleFactor < minScaleFactor){
                scaleFactor = minScaleFactor;
            }
            else if(scaleFactor > maxScaleFactor){
                scaleFactor = maxScaleFactor;
            }
            else{
                transform.postScale(detector.getScaleFactor(), detector.getScaleFactor(), detector.getFocusX(), detector.getFocusY());
            }

            return true;
        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            return true;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
        }
    }

    private class PointerMoveListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            shiftX -= distanceX;
            shiftY -= distanceY;
            transform.postTranslate(-distanceX, -distanceY);
            scaledShiftX -= distanceX/scaleFactor;
            scaledShiftY -= distanceY/scaleFactor;
            return true;
        }
    }
}
