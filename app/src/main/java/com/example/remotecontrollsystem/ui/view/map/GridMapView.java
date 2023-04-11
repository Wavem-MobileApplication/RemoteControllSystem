package com.example.remotecontrollsystem.ui.view.map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
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

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;


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
        responsePublisher = Mqtt.getInstance().getMessagePublisher(WidgetType.GET_MAP.getType() + Mqtt.RESPONSE);
        responsePublisher.attach(responseObserver);

        setClickable(true);
        setOnClickListener(v -> {
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

    private Bitmap convertOccupancyGridToBitmap(OccupancyGrid occupancyGrid) {
        int[] data = occupancyGrid.getData();
        byte[] bytes = new byte[data.length];
        for (int i = 0; i < bytes.length; i++) {
            if (data[i] == (byte) -1) {
                bytes[i] = (byte) 100;
            } else if (data[i] == (byte) 100) {
                bytes[i] = (byte) 255;
            } else {
                bytes[i] = (byte) data[i];
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(occupancyGrid.getInfo().getWidth(), occupancyGrid.getInfo().getHeight(), Bitmap.Config.ALPHA_8);
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.rewind();
        bitmap.copyPixelsFromBuffer(buffer);

        return bitmap;
    }

    private final Observer responseObserver = new Observer() {
        @Override
        public void update(String message) {
            Disposable backgroundTask = Observable.fromCallable(() -> {
                        GetMap_Response response = GetMap_Response.fromJson(message);

                        Log.d("MAP", String.valueOf(response.getMap().getData().length));
                        Log.d("MAP_Width", String.valueOf(response.getMap().getInfo().getWidth()));
                        Log.d("MAP_Height", String.valueOf(response.getMap().getInfo().getHeight()));

                        Bitmap bitmap = convertOccupancyGridToBitmap(response.getMap());
                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.post(() -> setImageBitmap(bitmap));
                        setClickable(false);

                        return true;
            })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe((result) -> {

                    });
        }
    };
}
