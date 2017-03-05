package com.dgonzalez.charts.pointers;

import android.graphics.Point;

/**
 * @author david.gonzalez (gonzalez.david31@gmail.com)
 */
public class PointerObject {

    private Point start;
    private Point end;
    private Character endOfPointer;

    public PointerObject(Point start, Point end){
        this.start =  start;
        this.end = end;
    }

    public void setEndPointer(Character c){
        this.endOfPointer = c;
    }

    public Point getStart() {
        return start;
    }

    public void setStart(Point start) {
        this.start = start;
    }

    public Point getEnd() {
        return end;
    }

    public void setEnd(Point end) {
        this.end = end;
    }

    public Character getEndOfPointer() {
        return endOfPointer;
    }

    public PointerObject clone(){
        PointerObject clone = new PointerObject(new Point(start), new Point(end));
        clone.setEndPointer(getEndOfPointer());
        return clone;
    }
}
