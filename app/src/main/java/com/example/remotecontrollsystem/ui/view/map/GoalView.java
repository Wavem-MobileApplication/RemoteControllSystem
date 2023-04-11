package com.example.remotecontrollsystem.ui.view.map;

import android.content.Context;
import android.view.ViewGroup;

import com.example.remotecontrollsystem.R;

public class GoalView extends androidx.appcompat.widget.AppCompatImageView {
    private static final String TAG = GoalView.class.getSimpleName();
    private static final int SIZE = 20;

    public GoalView(Context context) {
        super(context);

        setRotationX(180);
        setImageResource(R.drawable.icon_destination_flag);
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
}
