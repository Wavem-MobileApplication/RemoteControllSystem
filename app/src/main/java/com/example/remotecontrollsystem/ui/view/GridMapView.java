package com.example.remotecontrollsystem.ui.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;

import com.example.remotecontrollsystem.model.entity.Topic;
import com.example.remotecontrollsystem.model.utils.DataManager;
import com.example.remotecontrollsystem.model.viewmodel.TopicViewModel;
import com.example.remotecontrollsystem.mqtt.Mqtt;
import com.example.remotecontrollsystem.mqtt.data.MessagePublisher;
import com.example.remotecontrollsystem.mqtt.msgs.OccupancyGrid;
import com.example.remotecontrollsystem.mqtt.msgs.RosMessageDefinition;
import com.example.remotecontrollsystem.mqtt.utils.WidgetType;

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
        topicViewModel = DataManager.getInstance().getTopicViewModel();
        topicViewModel.getTopic(WidgetType.GET_MAP.getType()).observe(DataManager.getInstance().getActivity(), new Observer<Topic>() {
            @Override
            public void onChanged(Topic topic) {
                if (topic != null && topic.getMessage() != null) {
                    messageDefinition = topic.getMessage();
                    Log.d(TAG, "Set Message Definition -> " + messageDefinition.getName());
                }
            }
        });
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
        publisher = Mqtt.getInstance().getMqttMessageListener(WidgetType.MAP.getType());
        publisher.attach(new com.example.remotecontrollsystem.mqtt.data.Observer() {
            @Override
            public void update(String message) {
                Log.d("update", message);

/*                Bitmap bitmap = convertOccupancyGridToBitmap(occupancyGrid);
                setImageBitmap(bitmap);*/
            }
        });

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
