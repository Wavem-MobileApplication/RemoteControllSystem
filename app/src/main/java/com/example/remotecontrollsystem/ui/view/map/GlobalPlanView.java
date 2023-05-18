package com.example.remotecontrollsystem.ui.view.map;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.remotecontrollsystem.mqtt.msgs.MapMetaData;
import com.example.remotecontrollsystem.mqtt.msgs.Path;

public class GlobalPlanView extends View {
    private static final int STROKE = 1;
    private Paint paint;
    private Path path;
    private float resolution = 0.5f;

    public GlobalPlanView(Context context) {
        super(context);
        init();
    }

    public GlobalPlanView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.GREEN);
        paint.setStrokeWidth(STROKE);
    }

    public void updateMapMetaData(MapMetaData data) {
        resolution = data.getResolution();
    }

    public void updateGlobalPath(Path path) {
        this.path = path;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (path != null) {
            for (int i = 0; i < path.getPoses().length; i += STROKE) {
                float x = (float) (path.getPoses()[i].getPose().getPosition().getX() / resolution);
                float y = (float) (path.getPoses()[i].getPose().getPosition().getY() / resolution);
                canvas.drawPoint(x, y, paint);
            }
        }
    }
}