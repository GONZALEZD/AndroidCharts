package com.dgonzalez.charts.pointers;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.dgonzalez.charts.utils.ColorUtils;
import com.dgonzalez.charts.utils.NotPermittedException;

import com.dgonzalez.charts.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @author david.gonzalez (gonzalez.david31@gmail.com)
 */
public class PointersContainerView extends FrameLayout {

    private static final float MIN_SCALE = 1.0f;
    private static final float MAX_SCALE = 5.0f;

    private ImageView background;
    private ViewGroup container;

    private Matrix transform;
    private Matrix invertTransform;
    private Matrix previousTransform;
    private Matrix computeMatrix;

    private float[] transformTab;

    private float scaleFactor;

    private ScaleGestureDetector scaleDetector;
    private PointerScaleListener scaleListener;
    private GestureDetector moveDetector;
    private PointerMoveListener moveListener;
    private OnUserTouchListener userListener;

    private PointerAdapter<PointerObject> dataAdapter;
    private PointerRenderer defaultRenderer;
    private PointerView pointerTouchedDown;
    private List<PointerView> pointerViewsFromAdapter;

    private float minScaleFactor, maxScaleFactor;

    private int imageWidth, imageHeight;

    private boolean lockTouch = false;

    public PointersContainerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public PointersContainerView(Context context) {
        super(context);
        init(context, null);
    }

    @Override
    public void addView(View child) throws NotPermittedException{
        throw new NotPermittedException("Only "+PointerView.class.getName()+
                " instances are allowed to be added into "+
                PointersContainerView.class.getSimpleName());
    }

    public void addCustomPointerView(PointerView child) {
        container.addView(child, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
    }

    public void setDataAdapter(PointerAdapter adapter){
        this.dataAdapter = adapter;
        if(dataAdapter.getDefaultRenderer() == null) {
            dataAdapter.setDefaultRenderer(this.defaultRenderer);
        }
        dataAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                for(View v : pointerViewsFromAdapter){
                    container.removeView(v);
                }
                createPointers();
                for(int i=0; i<container.getChildCount(); i++){
                    PointerView pointer = (PointerView) container.getChildAt(i);
                    pointer.setTransformation(transform);
                    pointer.invalidate();
                }
            }

            @Override
            public void onInvalidated() {
                for(int i=0; i<container.getChildCount(); i++){
                    container.getChildAt(i).invalidate();
                }
            }
        });
        createPointers();
    }

    public final PointerRenderer getDefaultRenderer() {
        return defaultRenderer;
    }

    public void setOnTouchListener(View.OnTouchListener l){
        throw new NotPermittedException("Method used for "+getClass().getSimpleName()+" internal purpose");
    }

    public void setOnUserTouchListener(OnUserTouchListener listener){
        userListener = listener;
    }

    public void setBackgroundImage(Drawable image){
        background.setImageDrawable(image);
        updateImageSize();
    }

    public void setOnDoubleTapListener(GestureDetector.OnDoubleTapListener listener) {
        this.moveDetector.setOnDoubleTapListener(listener);
    }

    private void createPointers(){
        pointerViewsFromAdapter = new ArrayList<>();
        if(this.dataAdapter == null){
            return;
        }
        for(int i=dataAdapter.getCount()-1; i>=0 ; i--){
            PointerView child = (PointerView) dataAdapter.getView(i, null, container);
            pointerViewsFromAdapter.add(child);
            child.setX(0);
            child.setY(0);
            child.setTag(dataAdapter.getItem(i));
            container.addView(child, 0,
                    new FrameLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT));
        }

    }

    private void init(Context context, AttributeSet attrs){
        dataAdapter = null;
        pointerViewsFromAdapter = new ArrayList<>();
        Paint lineAndStroke = new Paint();
        int colorPrimary = ColorUtils.getPrimaryColor(context);
        lineAndStroke.setColor(colorPrimary);
        lineAndStroke.setAntiAlias(true);
        lineAndStroke.setStrokeCap(Paint.Cap.ROUND);
        lineAndStroke.setStrokeWidth(getContext().getResources().getDimension(R.dimen.pointers_default_stroke_width));
        defaultRenderer = new PointerRenderer(
                colorPrimary,
                ContextCompat.getColor(getContext(), R.color.pointers_default_background_color),
                lineAndStroke,
                getResources().getDimension(R.dimen.pointers_default_end_point_size));
        transform = new Matrix();
        invertTransform = new Matrix();
        scaleFactor = 1.f;
        transformTab = new float[9];
        transformTab[Matrix.MSCALE_X] = 1.f;
        transformTab[Matrix.MSCALE_Y] = 1.f;
        transformTab[Matrix.MTRANS_X] = 0.f;
        transformTab[Matrix.MTRANS_Y] = 0.f;
        previousTransform = new Matrix();
        computeMatrix = new Matrix();

        background = new ImageView(getContext());
        background.setScaleType(ImageView.ScaleType.MATRIX);

        moveListener = new PointerMoveListener();
        scaleListener = new PointerScaleListener();
        if(attrs != null){
            processAttributes(context, attrs, colorPrimary);
        }

        container = new FrameLayout(getContext());
        container.setPivotX(0);
        container.setPivotY(0);
        addView(background,0,
                new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        addView(container, 1,
                new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        scaleDetector = new ScaleGestureDetector(getContext(), scaleListener);
        moveDetector = new GestureDetector(getContext(),moveListener);

        super.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return PointersContainerView.this.onTouch(v, event);
            }
        });
    }


    public void fitImageToView(){
        if(background != null && background.getDrawable() != null){
            post(new Runnable() {
                @Override
                public void run() {
                    fitImage();
                }
            });
        }
    }

    private void fitImage(){
        if(background != null && background.getDrawable() != null){
            // retrieve image bounds
            //retrieve View bounds
            float viewW = getMeasuredWidth();
            float viewH = getMeasuredHeight();

            float scaleW = viewW/((float)imageWidth);
            float scaleH = viewH/((float)imageHeight);

            float transX, transY;
            float firstScale = 0;
            if(scaleH > scaleW) {
                firstScale = scaleW;
                transX=0;
                transY= (viewH-(imageHeight*firstScale))/2.f;
            }
            else{
                firstScale = scaleH;
                transY=0;
                transX = (viewW- (imageWidth*firstScale))/2.f;
            }

            transform.setScale(firstScale, firstScale);
            transform.postTranslate(transX, transY);
            transform.invert(previousTransform);

            // apply scale
            background.setImageMatrix(transform);

            resetContainerTransformation();
        }
    }

    private void resetContainerTransformation(){
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

    public PointerView findPointerView(PointerObject from) {
        if(from == null){
            return null;
        }
        for(int i=0; i<container.getChildCount(); i++) {
            PointerView p = (PointerView) container.getChildAt(i);
            if(from.equals(p.getTag())){
                return p;
            }
        }
        return null;
    }

    private void handleOnUserTouchListener(MotionEvent touchEvent) {
        if(userListener != null) {
            float[] point = new float[]{touchEvent.getX(), touchEvent.getY()};
            transform.invert(invertTransform);
            invertTransform.mapPoints(point);
            // fire onTouchImage event if image coordinates is correct
            int x = (int) point[0];
            int y = (int) point[1];

            // find if a pointer is clicked
            PointerView pointerFound = null;
            for(int i=container.getChildCount()-1; i>=0; i--) {
                PointerView pointer = (PointerView) container.getChildAt(i);
                if (pointer.getVisibility() == View.VISIBLE && pointer.getPointerBounds().contains(touchEvent.getX(), touchEvent.getY())) {
                    pointerFound = pointer;
                    break;
                }
            }
            if(touchEvent.getAction() == MotionEvent.ACTION_DOWN) {
                pointerTouchedDown = pointerFound;
            }
            if(0 <= x && x <=imageWidth && 0 <= y && y <= imageHeight) {
                userListener.onTouchImage(touchEvent, x, y, pointerFound);
                if(touchEvent.getAction() == MotionEvent.ACTION_UP && pointerFound!=null && pointerFound.equals(pointerTouchedDown)) {
                    userListener.onPointerClicked(touchEvent, pointerFound, pointerFound.getPointerObject());
                }

            }
            else {
                userListener.onTouchOutsideImage(touchEvent);
            }
        }
    }

    private boolean onTouch(View v, MotionEvent event){
        handleOnUserTouchListener(event);

        if(lockTouch) {
            return true;
        }
        scaleDetector.onTouchEvent(event);
        moveDetector.onTouchEvent(event);

        background.setImageMatrix(transform);

        computeMatrix.setConcat(transform, previousTransform);
        computeMatrix.getValues(transformTab);

        container.setScaleX(transformTab[Matrix.MSCALE_X]);
        container.setScaleY(transformTab[Matrix.MSCALE_Y]);
        container.setTranslationX(transformTab[Matrix.MTRANS_X]);
        container.setTranslationY(transformTab[Matrix.MTRANS_Y]);

        if(event.getAction() == MotionEvent.ACTION_UP){
            resetContainerTransformation();
        }
        return true;
    }

    private void processAttributes(Context context, AttributeSet attrs, int colorPrimary){

        TypedArray array = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.PointersContainerView,0,0);
        // retrieve pointers container image
        int imgId = array.getResourceId(R.styleable.PointersContainerView_image, -1);
        if(imgId != -1){
            setBackgroundResource(imgId);
        }
        // retrieve minimum and maximum scale factor
        setMinimumScale(array.getFloat(R.styleable.PointersContainerView_minimum_scale, MIN_SCALE));
        setMaximumScale(array.getFloat(R.styleable.PointersContainerView_maximum_scale, MAX_SCALE));

        // set default pointers renderer
        defaultRenderer.lineColorAndStroke.setColor(
                array.getColor(R.styleable.PointersContainerView_pointers_default_color, colorPrimary));
        defaultRenderer.lineColorAndStroke.setStrokeWidth(
                array.getDimension(
                        R.styleable.PointersContainerView_pointers_default_stroke_width,
                        context.getResources().getDimension(R.dimen.pointers_default_stroke_width)));

        defaultRenderer.backgroundColor = array.getColor(
                R.styleable.PointersContainerView_pointers_default_background_color,
                ContextCompat.getColor(context, R.color.pointers_default_background_color));
        defaultRenderer.symbolColor = array.getColor(
                R.styleable.PointersContainerView_pointers_default_symbol_color, colorPrimary);
    }

    private void updateImageSize(){
        BitmapDrawable drawable = (BitmapDrawable)background.getDrawable();
        imageWidth = drawable.getIntrinsicWidth();
        imageHeight= drawable.getIntrinsicHeight();
        moveListener.setSrcBounds(imageWidth, imageHeight);
    }

    public void setMinimumScale(float minScale){
        minScaleFactor = minScale;
    }
    public void setMaximumScale(float maxScale){
        maxScaleFactor = maxScale;
    }
    public float getMinimumScale() { return minScaleFactor; }
    public float getMaximumScale() { return maxScaleFactor; }


    public void zoom(float zoomFactor, float pivotX, float pivotY){
        // scale view
        scaleListener.onScale(zoomFactor/scaleFactor, pivotX, pivotY);

        // center view
        moveListener.onScroll(null, null, 0, 0);

        background.setImageMatrix(transform);

        // update pointers view
        resetContainerTransformation();
    }

    public void zoom(float zoomFactor){
        zoom(zoomFactor, getMeasuredWidth()/2, getMeasuredHeight()/2);
    }

    public float getZoom() {
        return scaleFactor;
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
            return onScale(detector.getScaleFactor(), detector.getFocusX(), detector.getFocusY());
        }

        public boolean onScale(float newScale, float focusX, float focusY){
            float currentSF = newScale;
            float newScaleFactor = scaleFactor*currentSF;
            if(newScaleFactor < minScaleFactor){
                currentSF = minScaleFactor/scaleFactor;
                newScaleFactor = minScaleFactor;
            }
            else if(newScaleFactor > maxScaleFactor){
                currentSF = maxScaleFactor/scaleFactor;
                newScaleFactor = maxScaleFactor;
            }
            transform.postScale(currentSF, currentSF, focusX, focusY);
            scaleFactor = newScaleFactor;
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

        private RectF imgBounds, srcBounds;

        public PointerMoveListener() {
            imgBounds = new RectF();
            srcBounds = new RectF(0,0,imageWidth, imageHeight);
        }

        protected void setSrcBounds(int imageWidth, int imageHeight) {
            srcBounds = new RectF(0, 0, imageWidth, imageHeight);
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            float dX = 0, dY = 0;

            transform.mapRect(imgBounds, srcBounds);

            if(imgBounds.width() < getMeasuredWidth()) {
                // center image
                dX = -imgBounds.left + (getMeasuredWidth() - imgBounds.width()) * 0.5f;
            }
            else if(((imgBounds.left-distanceX) <=0) &&
                    ((imgBounds.right-distanceX) >= getMeasuredWidth())){
                dX = -distanceX;
            }
            else{
                if(imgBounds.left-distanceX > 0){
                    dX = -imgBounds.left;
                }
                else {
                    dX = getMeasuredWidth()- imgBounds.right;
                }
            }

            if(imgBounds.height() < getMeasuredHeight()) {
                // center image
                dY = -imgBounds.top + (getMeasuredHeight() - imgBounds.height()) * 0.5f;
            }
            else if(((imgBounds.top-distanceY) <=0) &&
                    ((imgBounds.bottom-distanceY) >= getMeasuredHeight())){
                dY = -distanceY;
            }
            else{
                if(imgBounds.top - distanceY > 0){
                    dY = -imgBounds.top;
                }
                else {
                    dY = getMeasuredHeight()- imgBounds.bottom;
                }
            }
            transform.postTranslate(dX, dY);
            return true;
        }
    }

    public void lockZoomAndTranslation(){
        lockTouch = true;
    }

    public void unlockZoomAndTranslation(){
        lockTouch = false;
    }

    public boolean isZoomAndTranslationLocked()  {
        return lockTouch;
    }
}
