package com.example.remotecontrollsystem.ui.view.map;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.remotecontrollsystem.mqtt.msgs.LaserScan;
import com.example.remotecontrollsystem.mqtt.msgs.MapMetaData;
import com.example.remotecontrollsystem.mqtt.msgs.Pose;
import com.example.remotecontrollsystem.mqtt.msgs.TFMessage;
import com.example.remotecontrollsystem.mqtt.msgs.TransformStamped;
import com.example.remotecontrollsystem.mqtt.utils.RosMath;

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
    }

    public void updateLaserScan(LaserScan laserScan) {
        try {
            frameId = laserScan.getHeader().getFrame_id();

            int length = laserScan.getRanges().size();
            float angleMin = laserScan.getAngle_min();
            float angleIncrement = laserScan.getAngle_increment();
            Map<String, Float> data = laserScan.getRanges();

            scanX = new float[length];
            scanY = new float[length];

            for (int i = 0; i < length; i++) {
//                float laserRadian = (float) (angleMin + angleIncrement * i - radian + tfRadian);
                    float laserRadian = (float) (angleMin + angleIncrement * i - radian + tfRadian + Math.PI);
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

    public void updateMapMetaData(MapMetaData mapMetaData) {
        resolution = mapMetaData.getResolution();
        originX = (float) mapMetaData.getOrigin().getPosition().getX();
        originY = (float) mapMetaData.getOrigin().getPosition().getY();
    }

    public void updateRobotPose(Pose pose) {
        poseX = (float) (pose.getPosition().getX() + originX) / resolution;
        poseY = (float) (pose.getPosition().getY() + originY) / resolution;
        radian = RosMath.QuaternionToRadian(pose.getOrientation());
    }

    public void updateTF(TFMessage tfMessage) {
        for (int i = 0; i < tfMessage.getTransforms().length; i++) {
            TransformStamped transForm = tfMessage.getTransforms()[i];
            if (transForm.getChild_frameId().equals(frameId)) {
                tfX = (float) transForm.getTransform().getTranslation().getX() / resolution;
                tfY = (float) transForm.getTransform().getTranslation().getY() / resolution;
                tfRadian = RosMath.QuaternionToRadian(transForm.getTransform().getRotation());
            }
        }
    }

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
