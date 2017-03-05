package com.dgonzalez.charts.pointers;

import android.graphics.Paint;

/**
 * @author david.gonzalez (gonzalez.david31@gmail.com)
 */
public class PointerRenderer {
    protected int symbolColor;
    protected int backgroundColor;
    protected Paint lineColorAndStroke;
    protected float endPointSize;

    public PointerRenderer(int symbolColor, int backgroundColor, Paint lineColorAndStroke, float endPointSize) {
        this.symbolColor = symbolColor;
        this.backgroundColor = backgroundColor;
        this.lineColorAndStroke = lineColorAndStroke;
        this.endPointSize = endPointSize;
    }
}
