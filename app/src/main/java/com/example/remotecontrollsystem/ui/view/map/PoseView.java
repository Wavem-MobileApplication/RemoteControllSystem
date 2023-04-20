package com.example.remotecontrollsystem.ui.view.map;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;

import com.example.remotecontrollsystem.R;

public class PoseView extends AppCompatImageView {
    private static final int SIZE = 10;

    public PoseView(@NonNull Context context) {
        super(context);
        setImageResource(R.drawable.icon_circle_arrow);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(SIZE, SIZE);
    }

    @Override
    public void setX(float x) {
        super.setX(x - SIZE / 2f);
    }

    @Override
    public void setY(float y) {
        super.setY(y - SIZE / 2f);
    }

    @Override
    public void setRotation(float rotation) {
        super.setRotation(rotation - 180); // Add 180 degree, Because of ROS rotation
        Log.d("Rotation", String.valueOf(rotation));
    }
}
