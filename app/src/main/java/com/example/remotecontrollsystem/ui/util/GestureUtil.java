package com.example.remotecontrollsystem.ui.util;

import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import com.example.remotecontrollsystem.ui.view.MapFrameLayout;

public class GestureUtil {

    public class GestureListener extends GestureDetector.SimpleOnGestureListener {
        private FrameLayout layout;
        private float moveX = 0;
        private float moveY = 0;

        public GestureListener(FrameLayout layout) {
            this.layout = layout;
        }

        @Override
        public boolean onDown(@NonNull MotionEvent e) {
            moveX = layout.getX() - e.getRawX();
            moveY = layout.getY() - e.getRawY();
            return true;
        }

        @Override
        public boolean onScroll(@NonNull MotionEvent e1, @NonNull MotionEvent e2, float distanceX, float distanceY) {
            layout.setX(e2.getRawX() + moveX);
            layout.setY(e2.getRawY() + moveY);
            return false;
        }
    }

    public class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        private FrameLayout layout;
        private float mScale;

        public ScaleListener(FrameLayout layout) {
            this.layout = layout;
            mScale = 1.0f;
        }

        @Override
        public boolean onScale(@NonNull ScaleGestureDetector detector) {
            mScale = layout.getScaleX() * detector.getScaleFactor();
            if (mScale < 0.2f) {
                mScale = 0.2f;
            } else if (mScale > 20.0f) {
                mScale = 20.0f;
            }

            layout.setScaleX(mScale);
            layout.setScaleY(mScale);

            return false;
        }
    }

    public GestureListener getGestureListener(FrameLayout layout) {
        return new GestureListener(layout);
    }

    public ScaleListener getScaleListener(FrameLayout layout) {
        return new ScaleListener(layout);
    }
}
