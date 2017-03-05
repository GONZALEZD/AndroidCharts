package com.dgonzalez.charts.colorpicker;

import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.dgonzalez.charts.R;
import com.dgonzalez.charts.utils.InvalidInputException;

import java.util.ArrayList;
import java.util.List;


/**
 * @author david.gonzalez (gonzalez.david31@gmail.com)
 */
public class ColorPickerDialog extends Dialog {

    private DataWrapper dataWrapper;
    private ColorPickerView colorPickerView;
    private ColorPickerView colorHueThumb;
    private int selectedColor=-1;
    private int selectedHueColor=-1;
    private boolean displayIntensityColors = false;

    private static class DataWrapper{
        private OnColorPickedListener listener;
        private int cancelTxt = R.string.dialog_cancel_text;
        private int validateTxt = R.string.dialog_choose_text;
        private View.OnClickListener cancelClick, validateClick;
        private int titleTxt = R.string.dialog_title;
    }

    public static class Builder{
        private Context context;
        private DataWrapper wrapper;

        public Builder(Context context) {
            this.context = context;
            this.wrapper = new DataWrapper();
        }

        public Builder setOnColorPickedListener(OnColorPickedListener listener) {
            wrapper.listener = listener;
            return this;
        }

        public Builder setCancelButton(int textId, View.OnClickListener clickListener){
            wrapper.cancelTxt = textId;
            wrapper.cancelClick = clickListener;
            return this;
        }

        public Builder setValidateButton(int textId, View.OnClickListener clickListener){
            wrapper.validateTxt = textId;
            wrapper.validateClick = clickListener;
            return this;
        }

        public Builder setTitle(int stringId){
            wrapper.titleTxt = stringId;
            return this;
        }

        public ColorPickerDialog build(){
            ColorPickerDialog dialog = new ColorPickerDialog(context);
            dialog.setDataWrapper(wrapper);
            return dialog;
        }
    }

    public ColorPickerDialog(Context context) {
        super(context);
    }

    private void setDataWrapper(DataWrapper wrapper) {
        this.dataWrapper = wrapper;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_color_picker);
        getWindow().setBackgroundDrawableResource(R.drawable.dialog_background);

        colorHueThumb = (ColorPickerView) findViewById(R.id.thumb_color_picker);
        colorHueThumb.setDrawBorders(false);
        colorHueThumb.setVisibility(View.GONE);
        colorHueThumb.setDrawSelection(false);
        colorHueThumb.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                    showColorHue();
                    return true;
                }
                return false;
            }
        });

        colorPickerView = (ColorPickerView) findViewById(R.id.color_picker);
        colorPickerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int min = Math.min(colorPickerView.getMeasuredHeight(), colorPickerView.getMeasuredWidth());
                colorPickerView.getLayoutParams().width = min;
                colorPickerView.getLayoutParams().height = min;
            }
        });
        colorPickerView.setOnColorPickedListener(new OnColorPickedListener() {
            @Override
            public void onColorPicked(int color, int colorIndex) {
                findViewById(R.id.validate).setEnabled(true);
                if(displayIntensityColors==false){
                    selectedHueColor = colorIndex;
                    showColorIntensity(color);
                    selectedColor = color;
                }
                else{
                    colorPickerView.setSelectedColorIndex(colorIndex);
                    if(dataWrapper.listener != null) {
                        selectedColor = color;
                    }
                }
            }
        });

        Button cancel = (Button) findViewById(R.id.cancel);
        cancel.setText(dataWrapper.cancelTxt);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(dataWrapper.cancelClick != null){
                    dataWrapper.cancelClick.onClick(view);
                }
                dismiss();
            }
        });

        Button validate = (Button) findViewById(R.id.validate);
        validate.setEnabled(false);
        validate.setText(dataWrapper.validateTxt);
        validate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(dataWrapper.listener != null) {
                    dataWrapper.listener.onColorPicked(selectedColor, colorPickerView.getSelectedColorIndex());
                }
                if(dataWrapper.validateClick != null) {
                    dataWrapper.validateClick.onClick(view);
                }
                dismiss();
            }
        });

        setTitle(dataWrapper.titleTxt);
    }

    private void showColorHue(){
        startAnimation(new ValueAnimator.AnimatorUpdateListener() {
            private boolean isColorSet = false;
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float value = (float) valueAnimator.getAnimatedValue();
                float abs = Math.abs(value-1.f);
                if(value< 1.f && isColorSet==false) {
                    colorHueThumb.setVisibility(View.GONE);
                    displayHue();
                    colorHueThumb.setScaleX(0.f);
                    colorHueThumb.setScaleY(0.f);
                    isColorSet = true;
                }
                else if(value>= 1.f){
                    colorHueThumb.setScaleX(value-1);
                    colorHueThumb.setScaleY(value-1);
                }
                colorPickerView.setScaleX(abs);
                colorPickerView.setScaleY(abs);
            }
        });
    }

    private void displayHue(){
        colorPickerView.setDefaultColors();
        colorPickerView.setSelectedColorIndex(selectedHueColor);
        colorPickerView.invalidate();

        displayIntensityColors = false;
    }

    private void startAnimation(ValueAnimator.AnimatorUpdateListener listener){
        ValueAnimator anim = ValueAnimator.ofFloat(2.f, 1.f, 0.f);
        anim.setDuration(getContext().getResources().getInteger(R.integer.dialog_change_color_anim_duration_ms));
        anim.addUpdateListener(listener);
        anim.start();
    }

    private void showColorIntensity(final int hueColor) {
        startAnimation(new ValueAnimator.AnimatorUpdateListener() {
            private boolean isColorSet = false;
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float value = (float) valueAnimator.getAnimatedValue();
                float abs = Math.abs(value-1.f);
                if(value< 1.f && isColorSet==false) {
                    colorHueThumb.setVisibility(View.VISIBLE);
                    displaySaturationAndLightness(hueColor);
                    colorHueThumb.setScaleX(1.f);
                    colorHueThumb.setScaleY(1.f);
                    isColorSet = true;
                }
                else if(value >= 1.f){
                    colorHueThumb.setScaleX(Math.abs(value-2.f));
                    colorHueThumb.setScaleY(Math.abs(value-2.f));
                }
                colorPickerView.setScaleX(abs);
                colorPickerView.setScaleY(abs);
            }
        });
    }

    private void displaySaturationAndLightness(int hueColor){
        float[] hsv = new float[3];
        Color.colorToHSV(hueColor, hsv);
        float nbSat = 6;
        List<Integer> colors = new ArrayList<>();
        for(float i=0; i<= nbSat; i++){
            hsv[1] = i/nbSat;
            colors.add(Color.HSVToColor(hsv));
        }
        nbSat = 5;
        for(float i = nbSat-1; i>=0; i--){
            hsv[2] = i/nbSat;
            colors.add(Color.HSVToColor(hsv));
        }
        try {
            colorPickerView.setColors(colors);
        }catch (InvalidInputException e){
            throw new RuntimeException("Failed to set colors to Color picker view");
        }
        colorPickerView.setSelectedColorIndex(6);
        colorPickerView.invalidate();

        displayIntensityColors = true;
    }

    public void setOnColorPickedListener(OnColorPickedListener listener) {
        this.dataWrapper.listener = listener;
    }
}
