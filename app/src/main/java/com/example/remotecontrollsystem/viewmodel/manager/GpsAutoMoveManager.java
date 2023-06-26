package com.example.remotecontrollsystem.viewmodel.manager;

import android.content.Context;
import android.content.IntentFilter;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.example.remotecontrollsystem.model.entity.Route;
import com.example.remotecontrollsystem.model.entity.Waypoint;
import com.example.remotecontrollsystem.mqtt.data.RosObserver;
import com.example.remotecontrollsystem.mqtt.msgs.GpsMoveFeedBack;
import com.example.remotecontrollsystem.mqtt.msgs.GpsMoveRequest;
import com.example.remotecontrollsystem.mqtt.msgs.GpsMoveResponse;
import com.example.remotecontrollsystem.mqtt.msgs.NavSatFix;
import com.example.remotecontrollsystem.mqtt.msgs.Pose;
import com.example.remotecontrollsystem.mqtt.msgs.PoseStamped;
import com.example.remotecontrollsystem.mqtt.utils.WidgetType;
import com.example.remotecontrollsystem.viewmodel.MqttPubViewModel;
import com.example.remotecontrollsystem.viewmodel.MqttSubViewModel;
import com.example.remotecontrollsystem.viewmodel.StatusViewModel;

import org.oscim.core.GeoPoint;

public class GpsAutoMoveManager {
    private static final String TAG = GpsAutoMoveManager.class.getSimpleName();
    private Route route;
    private GeoPoint startGeoPoint;
    private GeoPoint currentGeoPoint;
    private GeoPoint goalGeoPoint;
    private double distance;

    private MqttPubViewModel mqttPubViewModel;
    private MqttSubViewModel mqttSubViewModel;
    private StatusViewModel statusViewModel;

    private int routeSize = 0;
    private int count = 0; // 주행이 완료된 경유지 그룹 개수
    private int index = 0; // 현재 주행중인 목적지 Index

    public GpsAutoMoveManager(AppCompatActivity activity) {
        this(activity, activity.getLifecycle(), activity);
    }

    public GpsAutoMoveManager(Fragment fragment) {
        this(fragment.requireContext(), fragment.getLifecycle(), fragment.requireActivity());
    }

    public GpsAutoMoveManager(Context context, Lifecycle lifecycle, ViewModelStoreOwner storeOwner) {
        startGeoPoint = new GeoPoint(0, 0);
        currentGeoPoint = new GeoPoint(0, 0);
        goalGeoPoint = new GeoPoint(0, 0);

        lifecycle.addObserver(new DefaultLifecycleObserver() {
            @Override
            public void onCreate(@NonNull LifecycleOwner owner) {
                DefaultLifecycleObserver.super.onCreate(owner);

                mqttPubViewModel = new ViewModelProvider(storeOwner).get(MqttPubViewModel.class);
                mqttSubViewModel = new ViewModelProvider(storeOwner).get(MqttSubViewModel.class);
                statusViewModel = new ViewModelProvider(storeOwner).get(StatusViewModel.class);
            }

            @Override
            public void onResume(@NonNull LifecycleOwner owner) {
                DefaultLifecycleObserver.super.onResume(owner);

                mqttSubViewModel.getTopicLiveData(WidgetType.NAV_SAT_FIX).observe(owner, navSatFixObserver);
                mqttSubViewModel.getTopicLiveData(WidgetType.GPS_MOVE_FEEDBACK).observe(owner, feedBackObserver);
                mqttSubViewModel.getTopicLiveData(WidgetType.GPS_MOVE_RESPONSE).observe(owner, responseObserver);
            }

            @Override
            public void onPause(@NonNull LifecycleOwner owner) {
                DefaultLifecycleObserver.super.onPause(owner);
            }


            @Override
            public void onDestroy(@NonNull LifecycleOwner owner) {
                DefaultLifecycleObserver.super.onDestroy(owner);
            }
        });
    }

    public void startManualDriving(Route route) {
        this.route = route;
        routeSize = route.getWaypointList().size();

        // Create GpsMoveRequest
        GpsMoveRequest request = createRequestMessage(route.getWaypointList().get(count));
        publishGoal(request);
    }

    public void stopManualDriving() {
        GpsMoveRequest request = createRequestMessage(new Waypoint());
        publishGoal(request);
    }

    private final RosObserver<NavSatFix> navSatFixObserver = new RosObserver<>(NavSatFix.class) {
        @Override
        public void onChange(NavSatFix navSatFix) {
            currentGeoPoint = new GeoPoint(navSatFix.getLatitude(), navSatFix.getLongitude());
        }
    };

    private final RosObserver<GpsMoveFeedBack> feedBackObserver = new RosObserver<>(GpsMoveFeedBack.class) {
        @Override
        public void onChange(GpsMoveFeedBack feedBack) {
            if (routeSize != 0) {
                double remainedDistance = currentGeoPoint.distance(goalGeoPoint);
                int progress = (int) (remainedDistance / distance * 100);
                int allProgress = (count / routeSize * 100) + progress / routeSize;

                index = feedBack.getCount();
                statusViewModel.updateManualMoveProgress(allProgress);

                Log.d("feedback", "feedback");
                Log.d("index", String.valueOf(index));
                Log.d("count", String.valueOf(count));
            }
        }
    };

    private final RosObserver<GpsMoveResponse> responseObserver = new RosObserver<>(GpsMoveResponse.class) {
        @Override
        public void onChange(GpsMoveResponse response) {
            // Add count number
            if (index >= route.getWaypointList().get(count).getPoseList().size() - 1) {
                count++;
                Log.d("count up", String.valueOf(count));
            } else {
                Log.d("response", "not count add up");
            }

            if (count >= route.getWaypointList().size()) {
                count = 0;
            } else {
                Log.d("response", "response");
                Log.d("count", String.valueOf(count));
                Log.d("index", String.valueOf(index));
                Log.d("size", String.valueOf(route.getWaypointList().get(count).getPoseList().size()));

                if (index >= route.getWaypointList().get(count).getPoseList().size() - 1) {
                    // Create GpsMoveRequest
                    GpsMoveRequest request = createRequestMessage(route.getWaypointList().get(count));
                    publishGoal(request);
                }
            }
        }
    };

    private GpsMoveRequest createRequestMessage(Waypoint waypoint) {
        // Create pose array
        PoseStamped[] poseArr = new PoseStamped[waypoint.getPoseList().size()];
        for (int i = 0; i < poseArr.length; i++) {
            Pose pose = waypoint.getPoseList().get(i);

            PoseStamped poseStamped = new PoseStamped();
            poseStamped.getHeader().setFrame_id("");
            poseStamped.getPose().getPosition().setY(4);
            poseStamped.getPose().getOrientation().setZ(1.5);
            poseStamped.getPose().getOrientation().setW(1.5);
            poseStamped.getPose().getOrientation().setX(pose.getPosition().getX());
            poseStamped.getPose().getOrientation().setY(pose.getPosition().getY());

            if (i < poseArr.length - 1) {
                poseStamped.getPose().getPosition().setX(1000);
            }

            poseArr[i] = poseStamped;
        }

        return new GpsMoveRequest(poseArr);
    }

    private void publishGoal(GpsMoveRequest request) {
        // Initialize start point
        NavSatFix startPoint = (NavSatFix) mqttSubViewModel.getTopicLiveData(WidgetType.NAV_SAT_FIX).getValue();
        startGeoPoint = new GeoPoint(startPoint.getLatitude(), startPoint.getLongitude());

        // Initialize goal point
        PoseStamped goalPoint = request.getWaypoints_list()[request.getWaypoints_list().length - 1];
        goalGeoPoint = new GeoPoint(goalPoint.getPose().getPosition().getX(), goalPoint.getPose().getPosition().getY());

        // Set distance;
        distance = startGeoPoint.distance(goalGeoPoint);

        mqttPubViewModel.publishTopic(WidgetType.GPS_MOVE_REQUEST.getType(), request);
    }
}
