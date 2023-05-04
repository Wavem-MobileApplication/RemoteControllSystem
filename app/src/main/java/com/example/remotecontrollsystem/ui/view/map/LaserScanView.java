package com.example.remotecontrollsystem.ui.view.map;

import static com.example.remotecontrollsystem.mqtt.utils.MessageType.RESPONSE;

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
import com.example.remotecontrollsystem.mqtt.msgs.TFMessage;
import com.example.remotecontrollsystem.mqtt.msgs.TransformStamped;
import com.example.remotecontrollsystem.mqtt.utils.RosMath;
import com.example.remotecontrollsystem.mqtt.utils.WidgetType;

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
    private Paint testPaint;

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

        testPaint = new Paint();
        testPaint.setStrokeWidth(STROKE * 10);
        testPaint.setColor(Color.RED);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        setClickable(false);
        startObserve();
    }

    private void startObserve() {
        MessagePublisher<LaserScan> scanPublisher = Mqtt.getInstance().getMessagePublisher(WidgetType.LASER_SCAN.getType());
        MessagePublisher<GetMap_Response> mapPublisher = Mqtt.getInstance().getMessagePublisher(WidgetType.GET_MAP.getType() + RESPONSE.getType());
        MessagePublisher<Pose> robotPosePublisher = Mqtt.getInstance().getMessagePublisher(WidgetType.ROBOT_POSE.getType());
        MessagePublisher<TFMessage> tfPublisher = Mqtt.getInstance().getMessagePublisher(WidgetType.TF_STATIC.getType());

        scanPublisher.attach(scanObserver);
        mapPublisher.attach(mapObserver);
        robotPosePublisher.attach(robotPoseObserver);
        tfPublisher.attach(tfObserver);
    }

    private final Observer<LaserScan> scanObserver = new Observer<LaserScan>() {
        @Override
        public void update(LaserScan message) {
            try {
                frameId = message.getHeader().getFrame_id();

                int length = message.getRanges().size();
                float angleMin = message.getAngle_min();
                float angleIncrement = message.getAngle_increment();
                Map<String, Float> data = message.getRanges();

                scanX = new float[length];
                scanY = new float[length];

                for (int i = 0; i < length; i++) {
                    float laserRadian = (float) (angleMin + angleIncrement * i - radian + tfRadian);
//                    float laserRadian = (float) (angleMin + angleIncrement * i - radian + tfRadian + Math.PI);
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

    private final Observer<GetMap_Response> mapObserver = new Observer<GetMap_Response>() {
        @Override
        public void update(GetMap_Response message) {
            if (message != null) {
                resolution = message.getMap().getInfo().getResolution();
                originX = (float) message.getMap().getInfo().getOrigin().getPosition().getX();
                originY = (float) message.getMap().getInfo().getOrigin().getPosition().getY();
            }
        }
    };

    private final Observer<Pose> robotPoseObserver = new Observer<Pose>() {
        @Override
        public void update(Pose message) {
            poseX = (float) (message.getPosition().getX() + originX) / resolution;
            poseY = (float) (message.getPosition().getY() + originY) / resolution;
            radian = RosMath.QuaternionToRadian(message.getOrientation());
        }
    };

    private final Observer<TFMessage> tfObserver = new Observer<TFMessage>() {
        @Override
        public void update(TFMessage message) {
            for (int i = 0; i < message.getTransforms().length; i++) {
                TransformStamped transForm = message.getTransforms()[i];
                if (transForm.getChild_frameId().equals(frameId)) {
                    tfX = (float) transForm.getTransform().getTranslation().getX() / resolution;
                    tfY = (float) transForm.getTransform().getTranslation().getY() / resolution;
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
                canvas.drawPoint(scanX[i] + poseX - tfX, scanY[i] + poseY - tfY, paint);
            }
        }
    }
}
