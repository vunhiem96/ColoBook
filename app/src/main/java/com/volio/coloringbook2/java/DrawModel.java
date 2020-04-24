package com.volio.coloringbook2.java;

import android.graphics.Point;

public class DrawModel {
    private Point point;
    private int sourceColor;
    private int targetColor;


    public DrawModel() {

    }

    public DrawModel(Point point, int sourceColor, int targetColor) {
        this.point = point;
        this.sourceColor = sourceColor;
        this.targetColor = targetColor;
    }

    public Point getPoint() {
        return point;
    }

    public void setPoint(Point point) {
        this.point = point;
    }

    public int getSourceColor() {
        return sourceColor;
    }

    public void setSourceColor(int sourceColor) {
        this.sourceColor = sourceColor;
    }

    public int getTargetColor() {
        return targetColor;
    }

    public void setTargetColor(int targetColor) {
        this.targetColor = targetColor;
    }
}
