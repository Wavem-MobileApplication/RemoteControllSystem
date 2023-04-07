package com.example.remotecontrollsystem.ui.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.remotecontrollsystem.model.viewmodel.TopicViewModel;
import com.example.remotecontrollsystem.mqtt.data.MessagePublisher;
import com.example.remotecontrollsystem.mqtt.msgs.OccupancyGrid;
import com.example.remotecontrollsystem.mqtt.msgs.RosMessageDefinition;

import org.apache.commons.lang3.ArrayUtils;

import java.nio.ByteBuffer;


public class GridMapView extends androidx.appcompat.widget.AppCompatImageView {
    private static final String TAG = GridMapView.class.getSimpleName();
    private boolean isCallable = true;
    private TopicViewModel topicViewModel;
    private RosMessageDefinition messageDefinition;
    private MessagePublisher publisher;

    public GridMapView(@NonNull Context context) {
        super(context);
        init();
    }

    public GridMapView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        setBackgroundColor(Color.WHITE);
        getLayoutParams().width = bm.getWidth();
        getLayoutParams().height = bm.getHeight();
        requestLayout();
    }

    private void init() {

        setOnClickListener(v -> {
            if (isCallable) {
                Log.d(TAG, "onClick");
                updateMap(messageDefinition);
            }
        });
    }

    private void updateMap(RosMessageDefinition definition) {

    }

    private Bitmap convertOccupancyGridToBitmap(OccupancyGrid occupancyGrid) {
        byte[] bytes = (byte[]) ArrayUtils.toPrimitive(occupancyGrid.getData());
        for (int i = 0; i < bytes.length; i++) {
            if (bytes[i] == (byte) -1) {
                bytes[i] = (byte) 100;
            } else if (bytes[i] == (byte) 100) {
                bytes[i] = (byte) 255;
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(occupancyGrid.getInfo().getWidth(), occupancyGrid.getInfo().getHeight(), Bitmap.Config.ALPHA_8);
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.rewind();
        bitmap.copyPixelsFromBuffer(buffer);

        return bitmap;
    }
}
