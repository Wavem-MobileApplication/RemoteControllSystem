package com.example.remotecontrollsystem.ui.view.mapsforge;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.example.remotecontrollsystem.R;

public class GpsMapViewUtil {
    public static ImageButton createImageButton(Context context, int resId, View.OnClickListener listener) {
        ImageButton button = new ImageButton(context);
        button.setImageResource(resId);
        button.setBackgroundResource(R.drawable.ripple_click_button_new);
        button.setClickable(true);
        button.setScaleType(ImageView.ScaleType.FIT_CENTER);
        button.setPadding(5, 5, 5, 5);
        button.setOnClickListener(listener);

        return button;
    }

    public static FrameLayout.LayoutParams createLayoutParams(int maxWidth, int maxHeight, int percent) {
        int size = Math.min(maxWidth, maxHeight) * percent / 100;

        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(size, size);
        layoutParams.gravity = Gravity.RIGHT;
        layoutParams.topMargin = 20;
        layoutParams.rightMargin = 20;

        return layoutParams;
    }
}
