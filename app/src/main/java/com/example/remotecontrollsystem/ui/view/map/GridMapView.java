package com.example.remotecontrollsystem.ui.view.map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.JsonReader;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.remotecontrollsystem.mqtt.Mqtt;
import com.example.remotecontrollsystem.mqtt.data.MessagePublisher;
import com.example.remotecontrollsystem.mqtt.data.Observer;
import com.example.remotecontrollsystem.mqtt.msgs.GetMap_Response;
import com.example.remotecontrollsystem.mqtt.msgs.OccupancyGrid;
import com.example.remotecontrollsystem.mqtt.utils.WidgetType;
import com.google.gson.Gson;
import com.google.gson.internal.GsonBuildConfig;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;


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

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    private void init() {
        responsePublisher = Mqtt.getInstance().getMessagePublisher(WidgetType.GET_MAP.getType() + Mqtt.RESPONSE);
        responsePublisher.attach(responseObserver);

        post(() -> setClickable(false));

        setOnClickListener(view -> {
            Log.d(TAG,  "onClick");
            updateMap();
        });
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
        int size = occupancyGrid.getData().size();
        int width = occupancyGrid.getInfo().getWidth();
        int height = occupancyGrid.getInfo().getHeight();

        byte[] bytes = new byte[size];
        for (int i = 0; i < size; i++) {
            if (occupancyGrid.getData().get(String.valueOf(i)) == -1) {
                bytes[i] = (byte) 100;
            } else if (occupancyGrid.getData().get(String.valueOf(i)) == 100) {
                bytes[i] = (byte) 255;
            } else {
                bytes[i] = (byte) occupancyGrid.getData().get(String.valueOf(i)).intValue();
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ALPHA_8);
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.rewind();
        bitmap.copyPixelsFromBuffer(buffer);

        return bitmap;
    }

    private Observer responseObserver = message -> {
/*
        GetMap_Response response = new Gson().fromJson(message, GetMap_Response.class);
        try {
            Bitmap bitmap = convertOccupancyGridToBitmap(response.getMap());
            Log.d("맵수신", "완료");
            post(() -> {
                setImageBitmap(bitmap);
                setClickable(false);
            });
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "Failed to draw map...");
        }
*/

        Log.d("맵", message);
    };
}
