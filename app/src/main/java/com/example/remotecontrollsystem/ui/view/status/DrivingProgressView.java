package com.example.remotecontrollsystem.ui.view.status;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;

import com.example.remotecontrollsystem.R;
import com.example.remotecontrollsystem.model.entity.Route;
import com.example.remotecontrollsystem.model.entity.Waypoint;
import com.example.remotecontrollsystem.mqtt.msgs.Odometry;
import com.example.remotecontrollsystem.mqtt.msgs.Pose;
import com.example.remotecontrollsystem.viewmodel.manager.AutoDrivingProgression;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class DrivingProgressView extends androidx.appcompat.widget.AppCompatImageView {
    private static final String TAG = DrivingProgressView.class.getSimpleName();
    private static final int WIDTH_STRIDE = 200;
    private static final int DEFAULT_HEIGHT = 20;
    private static final int ROUND = 10;
    private static final int POINT_RADIUS = 10;
    private static final int BOTTOM_TEXT_SIZE = 20;
    private static final int TOP_TEXT_SIZE = 30;
    private static final int TOP_TEXT_MARGIN = 10;
    private static final float CAR_SIZE = 100;
    private static final float HEIGHT = DEFAULT_HEIGHT + BOTTOM_TEXT_SIZE + TOP_TEXT_SIZE + TOP_TEXT_MARGIN;

    private Route route;
    private Paint progressBarPaint;
    private Paint destinationPointPaint;
    private Paint bottomTextPaint;
    private Paint topTextPaint;

    private Bitmap carBitmap;

    private int progression = 1;
    private Odometry odometry;
    private DecimalFormat decimalFormat;

    public DrivingProgressView(@NonNull Context context) {
        super(context);
        init();
        settingCarBitmap();
    }

    public DrivingProgressView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
        settingCarBitmap();
    }

    private void init() {
        route = new Route();
        odometry = new Odometry();
        decimalFormat = new DecimalFormat("#.#");

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

    public void updateRoute(Route route) {
        this.route = route;
        Log.d(TAG, "updateRoute");
        Log.d(TAG, "updateRoute -> size : " + route.getWaypointList().size());
        updateUI();
    }

    public void updateAutoDrivingProgression(int progress) {
        if (progress == 0) {
            this.progression = 1;
        } else {
            this.progression = progress;
        }
        invalidate();
    }

    public void updateOdometry(Odometry odometry) {
        this.odometry = odometry;
    }

    private void updateUI() {
        getLayoutParams().width = WIDTH_STRIDE * route.getWaypointList().size() + POINT_RADIUS * 4;
        getLayoutParams().height = (int) HEIGHT + carBitmap.getHeight();
        Log.d(TAG, "updateUI");
        if (route != null) {
            invalidate();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.d(TAG, "onDraw");
        int barRight = route.getWaypointList().size() * WIDTH_STRIDE;
        int barBottom = getBottom() - BOTTOM_TEXT_SIZE;
        int barTop = barBottom - DEFAULT_HEIGHT;

        canvas.drawRoundRect(0, barTop, barRight, barBottom, ROUND, ROUND, progressBarPaint);
        Log.d(TAG, "onDraw -> size : " + route.getWaypointList().size());
        Log.d(TAG, "onDraw -> width : " + barRight);
        for (int i = 0; i < route.getWaypointList().size(); i++) {
            int x = WIDTH_STRIDE * (i + 1) - POINT_RADIUS;
            int y = getBottom();
            int circleY = y - BOTTOM_TEXT_SIZE - POINT_RADIUS;
            canvas.drawText(route.getWaypointList().get(i).getName(), x, y, bottomTextPaint);
            canvas.drawCircle(x, circleY, POINT_RADIUS, destinationPointPaint);
            Log.d(TAG, route.getWaypointList().get(i).getName());
        }

        float carLeft = (float) (barRight / progression / 100 - carBitmap.getWidth() / 2);
        float carTop = getBottom() - BOTTOM_TEXT_SIZE - DEFAULT_HEIGHT - carBitmap.getHeight();
        canvas.drawBitmap(carBitmap, carLeft, carTop, new Paint());

        float speedX = carLeft + carBitmap.getWidth() / 2f;
        float speedY = carTop - TOP_TEXT_MARGIN;
        String speed = decimalFormat.format(odometry.getTwist().getTwist().getLinear().getX());
        Log.d("스피드", speed);
        canvas.drawText(speed + " m/s", speedX, speedY, topTextPaint);
    }
}
