package com.example.remotecontrollsystem.ui.view;

import android.content.Context;

import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import com.example.remotecontrollsystem.ui.util.GestureUtil;



public class MapFrameLayout extends FrameLayout {
    private GestureDetector gestureDetector;
    private ScaleGestureDetector scaleDetector;

    public MapFrameLayout(@NonNull Context context) {
        super(context);
        init();
    }

    public MapFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    private void init() {
        settingGestures();
        setRotationX(180);
        addView(new GridMapView(getContext()));
        addView(new NavigationView(getContext()));

/*        MapInfo.getInstance().observeMap(occupancyGrid -> {
            getLayoutParams().width = occupancyGrid.getInfo().getWidth();
            getLayoutParams().height = occupancyGrid.getInfo().getHeight();

            View parent = (View) getParent();
            float width = getLayoutParams().width;
            float height = getLayoutParams().height;
            float scale;
            if (width >= height) {
                Log.d("사이즈-부모", String.valueOf(parent.getWidth()));
                Log.d("사이즈-자식", String.valueOf(width));
                scale = parent.getWidth() / width;
            } else {
                Log.d("사이즈-부모", String.valueOf(parent.getHeight()));
                Log.d("사이즈-자식", String.valueOf(height));
                scale = parent.getHeight() / height;
            }

            Log.d("사이즈-스케일", String.valueOf(scale));

            setScaleX(scale);
            setScaleY(scale);

            setX((parent.getWidth() - width) / 2.0f);
            setY((parent.getHeight() - height) / 2.0f);
        });*/
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
