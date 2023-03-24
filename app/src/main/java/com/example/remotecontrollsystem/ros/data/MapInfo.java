package com.example.remotecontrollsystem.ros.data;

import com.example.remotecontrollsystem.ros.node.SubNode;

import org.ros2.rcljava.consumers.Consumer;
import org.ros2.rcljava.subscription.Subscription;

import java.util.ArrayList;
import java.util.List;

import nav_msgs.msg.OccupancyGrid;

public class MapInfo {
    private static MapInfo instance;
    private SubNode subNode;
    private Subscription<OccupancyGrid> subscription;
    private List<OnMapChangeListener> observers;

    public static MapInfo getInstance() {
        return instance;
    }

    public static MapInfo getInstance(SubNode subNode) {
        if (instance == null) {
            instance = new MapInfo(subNode);
        }

        return instance;
    }

    private MapInfo(SubNode subNode) {
        this.subNode = subNode;
        observers = new ArrayList<>();
    }

    public void subscribeMap(String topicName) {
        if (subscription != null) {
            subNode.getNode().removeSubscription(subscription);
        }

        subscription = subNode.getNode().createSubscription(OccupancyGrid.class, topicName,
                occupancyGrid -> {
                    for (OnMapChangeListener listener : observers) {
                        listener.onChange(occupancyGrid);
                    }
                });
    }

    public void setMap(OccupancyGrid occupancyGrid) {
        for (OnMapChangeListener listener : observers) {
            listener.onChange(occupancyGrid);
        }
    }

    public void observeMap(OnMapChangeListener mapChangeListener) {
        observers.add(mapChangeListener);
    }

    public void removeObserver(OnMapChangeListener listener) {
        observers.remove(listener);
    }

    public interface OnMapChangeListener {
        void onChange(OccupancyGrid occupancyGrid);
    }
}
