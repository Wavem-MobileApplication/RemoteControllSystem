package com.example.remotecontrollsystem.ui.view.map;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.remotecontrollsystem.mqtt.Mqtt;
import com.example.remotecontrollsystem.mqtt.data.MessagePublisher;
import com.example.remotecontrollsystem.mqtt.data.Observer;
import com.example.remotecontrollsystem.mqtt.msgs.GetMap_Response;
import com.example.remotecontrollsystem.mqtt.msgs.LaserScan;
import com.example.remotecontrollsystem.mqtt.msgs.Pose;
import com.example.remotecontrollsystem.mqtt.utils.WidgetType;
import com.google.gson.Gson;


public class LaserScanView extends View {
    private static final String TAG = LaserScanView.class.getSimpleName();
    private static final int STROKE = 3;

    private float[] scanX;
    private float[] scanY;
    private float tfX, tfY; // Map origin pose
    private float poseX, poseY; // Robot pose
    private float radian = 0; // Robot orientation
    private float resolution = 0.05f; // Map resolution

    private Paint paint;

    public LaserScanView(Context context) {
        super(context);
        init();
    }

    public LaserScanView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setStrokeWidth(STROKE);
        paint.setColor(Color.parseColor("#00AAFF"));
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        startObserve();
    }

    private void startObserve() {
        MessagePublisher scanPublisher = Mqtt.getInstance().getMessagePublisher(WidgetType.LASER_SCAN.getType());
        MessagePublisher mapPublisher = Mqtt.getInstance().getMessagePublisher(WidgetType.GET_MAP.getType() + Mqtt.RESPONSE);
        MessagePublisher robotPosePublisher = Mqtt.getInstance().getMessagePublisher(WidgetType.ROBOT_POSE.getType());

        scanPublisher.attach(scanObserver);
        mapPublisher.attach(mapObserver);
        robotPosePublisher.attach(robotPoseObserver);
    }

    private final Observer scanObserver = new Observer() {
        @Override
        public void update(String message) {
            try {
                LaserScan scan = LaserScan.fromJson(message);

                int length = scan.getRanges().length;
                float angleMin = scan.getAngle_min();
                float angleIncrement = scan.getAngle_increment();
                double[] data = scan.getRanges();

                scanX = new float[length];
                scanY = new float[length];

                for (int i = 0; i < length; i++) {
                    float laserRadian = (float) (angleMin + angleIncrement * i - radian + Math.PI);
                    scanX[i] = (float) (data[i] * Math.cos(laserRadian) / resolution);
                    scanY[i] = (float) (data[i] * Math.sin(laserRadian) / resolution);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            invalidate(); // Update UI
        }
    };

    private final Observer mapObserver = new Observer() {
        @Override
        public void update(String message) {
            GetMap_Response response = GetMap_Response.fromJson(message);

            resolution = response.getMap().getInfo().getResolution();
            tfX = (float) response.getMap().getInfo().getOrigin().getPosition().getX();
            tfY = (float) response.getMap().getInfo().getOrigin().getPosition().getY();
        }
    };

    private final Observer robotPoseObserver = new Observer() {
        @Override
        public void update(String message) {
            Pose pose = new Gson().fromJson(message, Pose.class);

            poseX = (float) (pose.getPosition().getX() + tfX) / resolution;
            poseY = (float) (pose.getPosition().getY() + tfY) / resolution;
        }
    };

    private final Observer tfObserver = new Observer() {
        @Override
        public void update(String message) {

        }
    };

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (scanX != null) {
            for (int i = 0; i < scanX.length; i += STROKE) {
                canvas.drawPoint(scanX[i] + poseX, scanY[i] + poseY, paint);
            }
        }
    }
}
