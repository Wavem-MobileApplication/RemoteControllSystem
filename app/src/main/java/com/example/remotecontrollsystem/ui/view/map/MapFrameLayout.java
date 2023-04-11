package com.example.remotecontrollsystem.ui.view.map;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.remotecontrollsystem.ui.util.GestureUtil;


public class MapFrameLayout extends FrameLayout {
    private GestureDetector gestureDetector;
    private ScaleGestureDetector scaleDetector;

    public MapFrameLayout(@NonNull Context context, AppCompatActivity activity) {
        super(context);
        init(activity);
    }

    private void init(AppCompatActivity activity) {
        settingGestures();
        setRotationX(180);
        setClickable(false);

        GridMapView gridMapView = new GridMapView(getContext());
        addView(gridMapView);
        addView(new NavigationView(getContext()));
        addView(new GoalFrameView(getContext(), activity));
        addView(new LaserScanView(getContext()));

        gridMapView.addOnLayoutChangeListener((view, i, i1, i2, i3, i4, i5, i6, i7) -> {
            float width = view.getWidth();
            float height = view.getHeight();

            View parent = (View) getParent();
            getLayoutParams().width = LayoutParams.WRAP_CONTENT;
            getLayoutParams().height = LayoutParams.WRAP_CONTENT;

            float x = (parent.getWidth() - width) / 2.0f;
            float y = (parent.getHeight() - height) / 2.0f;

            setX(0);
            setY(0);

            float scale;
            if (width >= height) {
                scale = parent.getWidth() / width;
            } else {
                scale = parent.getHeight() / height;
            }

            setScaleX(scale);
            setScaleY(scale);
        });
    }

    private void settingGestures() {
        GestureUtil util = new GestureUtil();
        gestureDetector = new GestureDetector(getContext(), util.getGestureListener(this));
        scaleDetector = new ScaleGestureDetector(getContext(), util.getScaleListener(this));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        scaleDetector.onTouchEvent(event);
        return true;
    }
}
