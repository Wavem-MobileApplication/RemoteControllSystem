package com.example.remotecontrollsystem.ui.view.map;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.remotecontrollsystem.mqtt.Mqtt;
import com.example.remotecontrollsystem.mqtt.data.MessagePublisher;
import com.example.remotecontrollsystem.mqtt.data.Observer;
import com.example.remotecontrollsystem.mqtt.msgs.GetMap_Response;
import com.example.remotecontrollsystem.mqtt.msgs.LaserScan;
import com.example.remotecontrollsystem.mqtt.msgs.Pose;
import com.example.remotecontrollsystem.mqtt.msgs.TFMessage;
import com.example.remotecontrollsystem.mqtt.msgs.TransformStamped;
import com.example.remotecontrollsystem.mqtt.utils.RosMath;
import com.example.remotecontrollsystem.mqtt.utils.WidgetType;
import com.google.gson.Gson;

import java.util.Map;


public class LaserScanView extends View {
    private static final String TAG = LaserScanView.class.getSimpleName();
    private static final int STROKE = 3;

    private String frameId = "";
    private float[] scanX;
    private float[] scanY;
    private float originX, originY; // Map origin pose
    private float tfX, tfY, tfRadian;
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

        setClickable(false);
        startObserve();
    }

    private void startObserve() {
        MessagePublisher scanPublisher = Mqtt.getInstance().getMessagePublisher(WidgetType.LASER_SCAN.getType());
        MessagePublisher mapPublisher = Mqtt.getInstance().getMessagePublisher(WidgetType.GET_MAP.getType() + Mqtt.RESPONSE);
        MessagePublisher robotPosePublisher = Mqtt.getInstance().getMessagePublisher(WidgetType.ROBOT_POSE.getType());
        MessagePublisher tfPublisher = Mqtt.getInstance().getMessagePublisher(WidgetType.TF_STATIC.getType());

        scanPublisher.attach(scanObserver);
        mapPublisher.attach(mapObserver);
        robotPosePublisher.attach(robotPoseObserver);
        tfPublisher.attach(tfObserver);
    }

    private Observer scanObserver = new Observer() {
        @Override
        public void update(String message) {
            try {
                LaserScan scan = new Gson().fromJson(message, LaserScan.class);
                frameId = scan.getHeader().getFrame_id();

                int length = scan.getRanges().size();
                float angleMin = scan.getAngle_min();
                float angleIncrement = scan.getAngle_increment();
                Map<String, Float> data = scan.getRanges();

                scanX = new float[length];
                scanY = new float[length];

                for (int i = 0; i < length; i++) {
                    float laserRadian = (float) (angleMin + angleIncrement * i - radian - tfRadian + Math.PI);
                    if (data.get(String.valueOf(i)) == null) {
                        scanX[i] = Float.NaN;
                        scanY[i] = Float.NaN;
                    } else {
                        scanX[i] = (float) (data.get(String.valueOf(i)) * Math.cos(laserRadian) / resolution);
                        scanY[i] = (float) (data.get(String.valueOf(i)) * Math.sin(laserRadian) / resolution);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            invalidate(); // Update UI
        }
    };

    private Observer mapObserver = new Observer() {
        @Override
        public void update(String message) {
            GetMap_Response response = new Gson().fromJson(message, GetMap_Response.class);

            resolution = response.getMap().getInfo().getResolution();
            originX = (float) response.getMap().getInfo().getOrigin().getPosition().getX();
            originY = (float) response.getMap().getInfo().getOrigin().getPosition().getY();
        }
    };

    private Observer robotPoseObserver = new Observer() {
        @Override
        public void update(String message) {
            Pose pose = new Gson().fromJson(message, Pose.class);

            poseX = (float) (pose.getPosition().getX() + originX) / resolution;
            poseY = (float) (pose.getPosition().getY() + originY) / resolution;
            radian = RosMath.QuaternionToRadian(pose.getOrientation());
        }
    };

    private Observer tfObserver = new Observer() {
        @Override
        public void update(String message) {
            TFMessage tf = new Gson().fromJson(message, TFMessage.class);
            for (int i = 0; i < tf.getTransforms().length; i++) {
                TransformStamped transForm = tf.getTransforms()[i];
                if (transForm.getChild_frameId().equals(frameId)) {
                    tfX = (float) transForm.getTransform().getTranslation().getX();
                    tfY = (float) transForm.getTransform().getTranslation().getY();
                    tfRadian = RosMath.QuaternionToRadian(transForm.getTransform().getRotation());
                }
            }
        }
    };

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (scanX != null) {
            for (int i = 0; i < scanX.length; i += STROKE) {
                canvas.drawPoint(scanX[i] + poseX + tfX, scanY[i] + poseY + tfY, paint);
            }
        }
    }
}
