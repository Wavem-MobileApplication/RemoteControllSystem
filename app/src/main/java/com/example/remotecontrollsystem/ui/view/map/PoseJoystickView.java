package com.example.remotecontrollsystem.ui.view.map;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.example.remotecontrollsystem.mqtt.msgs.MapMetaData;
import com.example.remotecontrollsystem.mqtt.msgs.Pose;
import com.example.remotecontrollsystem.mqtt.utils.RosMath;

public class PoseJoystickView extends View {
    private static final String TAG = PoseJoystickView.class.getSimpleName();
    private static final int SIZE = 30;
    private static final int STROKE = 3;
    private static final float RADIUS = (SIZE - STROKE) / 2f;

    private Paint circlePaint;
    private Paint linePaint;

    private float x, y;
    private float centerX, centerY;
    private float resolution = 0.05f;

    public PoseJoystickView(Context context) {
        super(context);

        circlePaint = new Paint();
        circlePaint.setColor(Color.RED);
        circlePaint.setStrokeWidth(STROKE);
        circlePaint.setStyle(Paint.Style.STROKE);

        linePaint = new Paint();
        linePaint.setColor(Color.RED);
        linePaint.setStrokeWidth(STROKE);
        linePaint.setStyle(Paint.Style.STROKE);

        setVisibility(INVISIBLE);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(SIZE, SIZE);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        centerX = getWidth() / 2f;
        centerY = getHeight() / 2f;
        x = centerX;
        y = centerY;
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                float dx = event.getX() - (getX() + getPivotX());
                float dy = event.getY() - (getY() + getPivotY());
                float length = (float) Math.sqrt(dx * dx + dy * dy);

                x = dx * RADIUS / length;
                y = dy * RADIUS / length;

                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                // Add Pose to newWaypoint
                float poseX = getX() + getPivotY();
                float poseY = getY() + getPivotY();
                double radian = Math.atan2(y, x);
                Log.d("radian", String.valueOf(radian));
                addPose(poseX, poseY, radian);

                // Clear x, y value
                x = centerX;
                y = centerY;

                setVisibility(INVISIBLE);
                invalidate();
                break;
        }

        return true;
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
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float radius = getWidth() / 2f;
        canvas.drawCircle(centerX, centerY, RADIUS, circlePaint);
        canvas.drawLine(centerX, centerY, centerX + x, centerY + y, linePaint);
    }

    private void addPose(double x, double y, double radian) {
        double[] quaternion = RosMath.RadianToQuaternion(radian);

        Pose pose = new Pose();
        pose.getPosition().setX(x * resolution);
        pose.getPosition().setY(y * resolution);
        pose.getOrientation().setZ(quaternion[0]);
        pose.getOrientation().setW(quaternion[1]);

        Log.d("Quaternion_Z", String.valueOf(quaternion[0]));
        Log.d("Quaternion_W", String.valueOf(quaternion[1]));
    }

    public void updateMapMetaData(MapMetaData mapMetaData) {
        resolution = mapMetaData.getResolution();
    }
}
