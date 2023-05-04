package com.example.remotecontrollsystem.ui.view.map;

import static com.example.remotecontrollsystem.mqtt.utils.MessageType.RESPONSE;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.remotecontrollsystem.mqtt.Mqtt;
import com.example.remotecontrollsystem.mqtt.data.MessagePublisher;
import com.example.remotecontrollsystem.mqtt.data.Observer;
import com.example.remotecontrollsystem.mqtt.msgs.GetMap_Response;
import com.example.remotecontrollsystem.mqtt.msgs.OccupancyGrid;
import com.example.remotecontrollsystem.mqtt.utils.WidgetType;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.stream.IntStream;


public class GridMapView extends androidx.appcompat.widget.AppCompatImageView {
    private static final String TAG = GridMapView.class.getSimpleName();
    private MessagePublisher responsePublisher;

    public GridMapView(@NonNull Context context) {
        super(context);
        init();
    }

    public GridMapView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        responsePublisher = Mqtt.getInstance().getMessagePublisher(WidgetType.GET_MAP.getType() + RESPONSE.getType());

        setOnClickListener(view -> {
            Log.d(TAG,  "onClick");
            updateMap();
        });
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        responsePublisher.attach(mapObserver);
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        getLayoutParams().width = bm.getWidth();
        getLayoutParams().height = bm.getHeight();
        setBackgroundColor(Color.WHITE);
    }

    private void updateMap() {
        Mqtt.getInstance().sendRequestMessageCall(WidgetType.GET_MAP.getType(), "", 0, false);
    }

    private Bitmap convertOccupancyGridToBitmap(OccupancyGrid occupancyGrid) throws Exception {
        int size = occupancyGrid.getData().length;
        int width = occupancyGrid.getInfo().getWidth();
        int height = occupancyGrid.getInfo().getHeight();

        byte[] bytes = new byte[size];
        IntStream.range(0, size)
                .parallel()
                .forEach(i -> {
                    int value = occupancyGrid.getData()[i];
                    if (value == -1) {
                        bytes[i] = (byte) 100;
                    } else if (value == 100) {
                        bytes[i] = (byte) 255;
                    } else {
                        bytes[i] = (byte) value;
                    }
                });

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ALPHA_8);
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.rewind();
        bitmap.copyPixelsFromBuffer(buffer);

        return bitmap;
    }

    private final Observer<GetMap_Response> mapObserver = new Observer<GetMap_Response>() {
        @Override
        public void update(GetMap_Response message) {
            try {
                    Bitmap bitmap = convertOccupancyGridToBitmap(message.getMap());
                    Log.d("맵수신", "완료");
                    post(() -> {
                        setImageBitmap(bitmap);
                        setClickable(false);
                    });
            } catch (Exception e) {
                e.printStackTrace();
                Log.d(TAG, "Failed to draw map...");
            }
        }
    };

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        responsePublisher.detach(mapObserver);
    }
}
