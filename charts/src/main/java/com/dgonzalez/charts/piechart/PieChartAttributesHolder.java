package com.dgonzalez.charts.piechart;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;

import fr.dgonzalez.charts.R;

/**
 * @author david.gonzalez (gonzalez.david31@gmail.com)
 * @since 21/08/2016
 */
class PieChartAttributesHolder {

    private static final int DEFAULT_BORDER_COLOR = Color.WHITE;
    private static final int DEFAULT_ANIM_DISTANCE = 20;

    private static final float DEFAULT_BORDER_STROKE_WIDTH = 8.f;
    private static final float DEFAULT_TEXT_SIZE = 26;
    private static final int DEFAULT_TEXT_COLOR = Color.WHITE;
    private static final boolean DEFAULT_TEXT_FORMAT_IN_PERCENT = false;
    private static final float DEFAULT_TEXT_POSITION = 0.70f;

    int borderColor;
    float animationDistance;
    float outerBorderWidth;
    float textSize;
    int textColor;
    boolean textInPercent;
    float textPosition;

    private PieChartAttributesHolder(){
    }

    public static PieChartAttributesHolder parse(Context context, AttributeSet attrs){
        PieChartAttributesHolder holder = new PieChartAttributesHolder();
        if(attrs == null || context == null){
            holder.borderColor = DEFAULT_BORDER_COLOR;
            holder.animationDistance = DEFAULT_ANIM_DISTANCE;
            holder.outerBorderWidth = DEFAULT_BORDER_STROKE_WIDTH;
            holder.textSize = DEFAULT_TEXT_SIZE;
            holder.textColor = DEFAULT_TEXT_COLOR;
            holder.textInPercent = DEFAULT_TEXT_FORMAT_IN_PERCENT;
            holder.textPosition = DEFAULT_TEXT_POSITION;
        }
        else {
            TypedArray array = context.getTheme().obtainStyledAttributes(attrs, R.styleable.PieChart, 0, 0);
            // retrieve pointers container image
            holder.borderColor = array.getColor(R.styleable.PieChart_border_color, DEFAULT_BORDER_COLOR);

            // retrieve animation distance
            holder.animationDistance = array.getDimension(R.styleable.PieChart_animation_distance, DEFAULT_ANIM_DISTANCE);

            // retrieve outer border width
            holder.outerBorderWidth = array.getDimension(R.styleable.PieChart_outer_border_width, DEFAULT_BORDER_STROKE_WIDTH);

            // retrieve text size
            holder.textSize = array.getDimension(R.styleable.PieChart_text_size, DEFAULT_TEXT_SIZE);

            // retrieve text color
            holder.textColor = array.getColor(R.styleable.PieChart_text_color, DEFAULT_TEXT_COLOR);

            // retrieve whether text displayed in percent
            holder.textInPercent = array.getBoolean(R.styleable.PieChart_text_in_percent, DEFAULT_TEXT_FORMAT_IN_PERCENT);

            // retrieve text position
            holder.textPosition = array.getFraction(R.styleable.PieChart_text_position, 1, 100, DEFAULT_TEXT_POSITION);

        }
        return holder;
    }
}
