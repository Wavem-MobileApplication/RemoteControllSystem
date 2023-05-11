package com.example.remotecontrollsystem.ui.util;

import android.graphics.Path;
import android.util.Log;

public class ArrowPath extends Path {
    private float cx, cy;
    private float x1, y1, x2, y2, x3, y3;
    private float tx, ty; // 밑변 중심 좌표
    private float len;

    private void centerPoint(float cx, float cy) {
        this.cx = cx;
        this.cy = cy;
    }

    private void startPoint(float x, float y) {
        this.x1 = x;
        this.y1 = y;
    }

    private void length(float length) {
        this.len = length;
        Log.d("len", String.valueOf(length));
    }

    private void calculate() {
        double m = Math.sqrt((cx - x1) * (cx - x1) + (cy - y1) * (cy - y1)) - len; // cx cy와 x1 y1을 이은 선분의 길이
        tx = (float) ((len * x1 + m * cx) / (len + m));
//        ty = cy + k * (y1 - cy);
        ty = (float) ((m * y1 + len * cy) / (len + m));

        float hypo = (float) (len * Math.sqrt(2f));
        double cos = hypo * Math.cos(Math.toRadians(45));
        double sin = hypo * Math.sin(Math.toRadians(45));

        x2 = (float) (tx + 10);
        y2 = (float) (ty);
        x3 = (float) (tx - 10);
        y3 = (float) (ty);

/*        float height = 100;
        x1= (float) (100 + Math.cos(Math.toRadians(90))*height);
        y1= (float) (100 + Math.sin(Math.toRadians(90))*height);

        x2= (float) (100 + Math.cos(Math.toRadians(90 + 90))*height);
        y2= (float) (100 + Math.sin(Math.toRadians(90 + 90))*height);

        x3= (float) (100 + Math.cos(Math.toRadians(90+270))*height);
        y3= (float) (100 + Math.sin(Math.toRadians(90+270))*height);*/

        moveTo(x1, y1);
        lineTo(x2, y2);
        lineTo(x3, y3);
        close();

        Log.d(String.valueOf(tx), String.valueOf(ty));
        Log.d(String.valueOf(x1), String.valueOf(y1));
        Log.d(String.valueOf(x2), String.valueOf(y2));
        Log.d(String.valueOf(x3), String.valueOf(y3));
    }

    public static class ArrowPathBuilder {
        private ArrowPath arrowPath;

        public ArrowPathBuilder() {
            arrowPath = new ArrowPath();
        }

        public ArrowPathBuilder centerPoint(float cx, float cy) {
            arrowPath.centerPoint(cx, cy);
            return this;
        }

        public ArrowPathBuilder startPoint(float x, float y) {
            arrowPath.startPoint(x, y);
            return this;
        }

        public ArrowPathBuilder length(float length) {
            arrowPath.length(length);
            return this;
        }

        public ArrowPath build() {
            arrowPath.calculate();
            return arrowPath;
        }
    }
}
