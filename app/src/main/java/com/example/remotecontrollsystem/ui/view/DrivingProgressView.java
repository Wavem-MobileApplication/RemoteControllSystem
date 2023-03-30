package com.example.remotecontrollsystem.ui.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;

import com.example.remotecontrollsystem.R;
import com.example.remotecontrollsystem.mqtt.msgs.Pose;

import java.util.ArrayList;
import java.util.List;

public class DrivingProgressView extends androidx.appcompat.widget.AppCompatImageView {
    private static final int WIDTH_STRIDE = 200;
    private static final int DEFAULT_HEIGHT = 20;
    private static final int ROUND = 10;
    private static final int POINT_RADIUS = 10;
    private static final int BOTTOM_TEXT_SIZE = 20;
    private static final int TOP_TEXT_SIZE = 30;
    private static final int TOP_TEXT_MARGIN = 10;
    private static final float CAR_SIZE = 100;
    private static final float HEIGHT = DEFAULT_HEIGHT + BOTTOM_TEXT_SIZE + TOP_TEXT_SIZE + TOP_TEXT_MARGIN;

    private List<Pose> destinationList;
    private Paint progressBarPaint;
    private Paint destinationPointPaint;
    private Paint bottomTextPaint;
    private Paint topTextPaint;

    private Bitmap carBitmap;

    public DrivingProgressView(@NonNull Context context) {
        super(context);
        settingCarBitmap();
    }

    public DrivingProgressView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        settingCarBitmap();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        init();
    }

    private void init() {
        destinationList = new ArrayList<>();

        progressBarPaint = new Paint();
        progressBarPaint.setColor(Color.parseColor("#D3D3D3"));

        destinationPointPaint = new Paint();
        destinationPointPaint.setColor(Color.WHITE);

        bottomTextPaint = new Paint();
        bottomTextPaint.setColor(Color.WHITE);
        bottomTextPaint.setTextAlign(Paint.Align.CENTER);
        bottomTextPaint.setTextSize(BOTTOM_TEXT_SIZE);

        topTextPaint = new Paint();
        topTextPaint.setColor(Color.YELLOW);
        topTextPaint.setTextSize(TOP_TEXT_SIZE);
        topTextPaint.setTextAlign(Paint.Align.CENTER);

        List<Pose> dummyList = dummyDestinationListData();
        updateDestinationList(dummyList);
    }

    private void settingCarBitmap() {
        Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.icon_car_crop, null);
        carBitmap = ((BitmapDrawable)drawable).getBitmap();

        Matrix matrix = new Matrix();
        float scale = CAR_SIZE / carBitmap.getWidth();
        matrix.setScale(scale, scale);

        carBitmap = Bitmap.createBitmap(carBitmap, 0, 0,
                carBitmap.getWidth(), carBitmap.getHeight(), matrix, true);
    }

    private List<Pose> dummyDestinationListData() {
        List<Pose> dummyDestinationList = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            Pose dummy = new Pose();
            dummy.getPosition().setX(i);
            dummy.getPosition().setY(i);
            dummy.getOrientation().setZ(0.43);
            dummy.getOrientation().setW(0.52);

            dummyDestinationList.add(dummy);
        }

        return dummyDestinationList;
    }

    private void updateDestinationList(List<Pose> destinations) {
        this.destinationList.clear();
        this.destinationList.addAll(destinations);

        updateUI();
    }

    private void updateUI() {
        getLayoutParams().width = WIDTH_STRIDE * destinationList.size() + POINT_RADIUS * 4;
        getLayoutParams().height = (int) HEIGHT + carBitmap.getHeight();

        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int barRight = destinationList.size() * WIDTH_STRIDE;
        int barBottom = getBottom() - BOTTOM_TEXT_SIZE;
        int barTop = barBottom - DEFAULT_HEIGHT;

        canvas.drawRoundRect(0, barTop, barRight, barBottom, ROUND, ROUND, progressBarPaint);

        for (int i = 0; i < destinationList.size(); i++) {
            int x = WIDTH_STRIDE * (i + 1) - POINT_RADIUS;
            int y = getBottom();
            int circleY = y - BOTTOM_TEXT_SIZE - POINT_RADIUS;
            canvas.drawText("목적지" + i, x, y, bottomTextPaint);
            canvas.drawCircle(x, circleY, POINT_RADIUS, destinationPointPaint);
        }

        int carLeft = 100 - carBitmap.getWidth() / 2;
        int carTop = getBottom() - BOTTOM_TEXT_SIZE - DEFAULT_HEIGHT - carBitmap.getHeight();
        canvas.drawBitmap(carBitmap, carLeft, carTop, new Paint());

        int speedX = carLeft + carBitmap.getWidth() / 2;
        int speedY = carTop - TOP_TEXT_MARGIN;
        canvas.drawText("3.2 m/s", speedX, speedY, topTextPaint);
    }
}
