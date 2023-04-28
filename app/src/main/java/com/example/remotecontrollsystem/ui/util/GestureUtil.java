package com.example.remotecontrollsystem.ui.util;

import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import com.example.remotecontrollsystem.ui.view.map.MapFrameLayout;

public class GestureUtil {
    private static final float BOUNDARY_PERCENT = 0.2f;

    public class GestureListener extends GestureDetector.SimpleOnGestureListener {
        private MapFrameLayout layout;
        private float moveX = 0;
        private float moveY = 0;

        private float parentWidth;
        private float parentHeight;

        public GestureListener(MapFrameLayout layout) {
            this.layout = layout;
        }

        @Override
        public boolean onDown(@NonNull MotionEvent e) {
            parentWidth = ((FrameLayout) layout.getParent()).getWidth();
            parentHeight = ((FrameLayout) layout.getParent()).getHeight();

            moveX = layout.getX() - e.getRawX();
            moveY = layout.getY() - e.getRawY();

            return true;
        }

        @Override
        public void onLongPress(@NonNull MotionEvent e) {
            super.onLongPress(e);
            layout.startPoseJoystick(e);
        }

        @Override
        public boolean onScroll(@NonNull MotionEvent e1, @NonNull MotionEvent e2, float distanceX, float distanceY) {
            float newX = e2.getRawX() + moveX;
            float newY = e2.getRawY() + moveY;

            float scaleX = layout.getScaleX();
            float scaleY = layout.getScaleY();
            float width = layout.getWidth();
            float height = layout.getHeight();

            float scaledWidth = width * scaleX;
            float scaledHeight = height * scaleY;
            float originX = (scaledWidth - width) * 0.5f;
            float originY = (scaledHeight - height) * 0.5f;

            float minX = -scaledWidth * (1f - BOUNDARY_PERCENT) + originX;
            float minY = -scaledHeight * (1f - BOUNDARY_PERCENT) + originY;
            float maxX = parentWidth - scaledWidth * BOUNDARY_PERCENT + originX;
            float maxY = parentHeight - scaledHeight * BOUNDARY_PERCENT + originY;

            if (newX < minX) {
                newX = minX;
            } else if (newX > maxX) {
                newX = maxX;
            }

            if (newY < minY) {
                newY = minY;
            } else if (newY > maxY) {
                newY = maxY;
            }

            layout.setX(newX);
            layout.setY(newY);

            return false;
        }
    }

    public class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        private MapFrameLayout layout;
        private float mScale;

        public ScaleListener(MapFrameLayout layout) {
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

    public GestureListener getGestureListener(MapFrameLayout layout) {
        return new GestureListener(layout);
    }

    public ScaleListener getScaleListener(MapFrameLayout layout) {
        return new ScaleListener(layout);
    }
}
