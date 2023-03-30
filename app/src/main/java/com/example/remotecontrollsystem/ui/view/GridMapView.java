package com.example.remotecontrollsystem.ui.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;



import java.nio.ByteBuffer;
import java.time.Duration;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;


public class GridMapView extends androidx.appcompat.widget.AppCompatImageView {
    private static final String TAG = GridMapView.class.getSimpleName();
    private boolean isCallable = true;

    public GridMapView(@NonNull Context context) {
        super(context);
//        init();
    }

    public GridMapView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
//        init();
    }

/*    @Override
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
//                updateMap("/map_server/map");
                updateMap("/static_map");
            }
        });
    }

    private void updateMap(String srvName) {
        Log.d(srvName, "Start to call map");
        Disposable backgroundTask = Observable.fromCallable(() -> {
            try {
                isCallable = false;

                Client<GetMap> client = SubNode.getInstance().getNode().createClient(GetMap.class, srvName);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    Log.d(srvName, "Wait for service");
                    client.waitForService(Duration.ofSeconds(3));
                }

                GetMap_Request request = new GetMap_Request();
                Future<GetMap_Response> responseFuture = client.asyncSendRequest(request);

                Log.d(srvName, "Wait for get map response");
                GetMap_Response response = responseFuture.get(3, TimeUnit.SECONDS);
                OccupancyGrid occupancyGrid = response.getMap();

                Log.d(TAG, "Start to draw map image");
                Bitmap bitmap = convertOccupancyGridToBitmap(occupancyGrid);
                MapInfo.getInstance().setMap(occupancyGrid);

                return bitmap;
            } catch (NoSuchFieldException | IllegalAccessException | ExecutionException |
                     InterruptedException | TimeoutException e) {
                e.printStackTrace();
                Log.d(srvName, "Fail to call Service(Error) -> " + srvName);

                return Bitmap.createBitmap(0, 0, null);
            }
        })
                .timeout(5, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((bitmap) -> {
                    isCallable = true;
                    if (bitmap.getWidth() > 0) {
                        Log.d(srvName, "Success to call Service(Error) -> " + srvName);
                        setImageBitmap(bitmap);
                        setClickable(false);
                    } else {
                        Log.d(srvName, "Fail to call Service(Empty Data) -> " + srvName);
                        Toast.makeText(getContext(), "맵을 불러오지 못했습니다.", Toast.LENGTH_SHORT).show();
                    }
                }, throwable -> {
                    isCallable = true;
                    Log.d(srvName, "Fail to call Service(Time Out) -> " + srvName);
                    Toast.makeText(getContext(), "맵을 불러오지 못했습니다.", Toast.LENGTH_SHORT).show();
                });
    }

    private Bitmap convertOccupancyGridToBitmap(OccupancyGrid occupancyGrid) {
        byte[] bytes = (byte[]) ArrayUtils.toPrimitive(occupancyGrid.getData().toArray(new Byte[occupancyGrid.getData().size()]));
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
    }*/

}
