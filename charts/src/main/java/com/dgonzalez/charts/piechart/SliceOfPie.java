package com.dgonzalez.charts.piechart;

/**
 * @author david.gonzalez (gonzalez.david31@gmail.com)
 * @since 12/08/2016
 */
public class SliceOfPie {
    private int color;
    private double value;
    private String name;

    public SliceOfPie(String name, double value, int color) {
        this.color = color;
        this.value = value;
        this.name = name;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "SliceOfPie{" +
                "name='" + name + '\'' +
                ", value=" + value +
                '}';
    }
}
