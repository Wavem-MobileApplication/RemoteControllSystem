package com.example.remotecontrollsystem.ui.view.map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.remotecontrollsystem.mqtt.msgs.OccupancyGrid;

import java.nio.ByteBuffer;
import java.util.stream.IntStream;


public class GridMapView extends androidx.appcompat.widget.AppCompatImageView {
    private static final String TAG = GridMapView.class.getSimpleName();

    public GridMapView(@NonNull Context context) {
        super(context);
    }

    public GridMapView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        getLayoutParams().width = bm.getWidth();
        getLayoutParams().height = bm.getHeight();
        setBackgroundColor(Color.WHITE);
    }

    public void updateMap(OccupancyGrid occupancyGrid) {
        Bitmap bitmap = convertOccupancyGridToBitmap(occupancyGrid);
        setImageBitmap(bitmap);
    }

    private Bitmap convertOccupancyGridToBitmap(OccupancyGrid occupancyGrid) {
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
}
