package com.dgonzalez.charts.pointers;

import android.graphics.Paint;

/**
 * @author david.gonzalez (gonzalez.david31@gmail.com)
 */
public class MutablePointerRenderer extends PointerRenderer{

    public MutablePointerRenderer(PointerRenderer renderer){
        this(renderer.symbolColor, renderer.backgroundColor, new Paint(renderer.lineColorAndStroke), renderer.endPointSize);
    }

    public MutablePointerRenderer(int symbolColor, int backgroundColor, Paint lineColorAndStroke, float endPointSize) {
        super(symbolColor, backgroundColor, lineColorAndStroke, endPointSize);
    }

    public int getSymbolColor() {
        return symbolColor;
    }

    public void setSymbolColor(int symbolColor) {
        this.symbolColor = symbolColor;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public Paint getLineColorAndStroke() {
        return lineColorAndStroke;
    }

    public void setLineColorAndStroke(Paint lineColorAndStroke) {
        this.lineColorAndStroke = lineColorAndStroke;
    }

    public float getEndPointSize() {
        return endPointSize;
    }

    public void setEndPointSize(float endPointSize) {
        this.endPointSize = endPointSize;
    }

}
