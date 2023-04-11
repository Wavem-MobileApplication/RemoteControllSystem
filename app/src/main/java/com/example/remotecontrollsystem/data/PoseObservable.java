package com.example.remotecontrollsystem.data;

import com.example.remotecontrollsystem.mqtt.msgs.Pose;

import java.util.ArrayList;
import java.util.List;

public class PoseObservable implements ISubject<List<Pose>> {
    private final List<IObserver> observerList;
    private static PoseObservable instance;
    private List<Pose> poseList;

    public PoseObservable() {
        observerList = new ArrayList<>();
        poseList = new ArrayList<>();
    }

    public static PoseObservable getInstance() {
        if (instance == null) {
            instance = new PoseObservable();
        }
        return instance;
    }

    @Override
    public void attach(IObserver<List<Pose>> observer) {
        observerList.add(observer);
    }

    @Override
    public void detach(IObserver<List<Pose>> observer) {
        observerList.remove(observer);
    }

    @Override
    public void postValue(List<Pose> poseList) {
        poseList.clear();
        poseList.addAll(poseList);

        for (IObserver observer : observerList) {
            observer.update(poseList);
        }
    }

    public void addPose(Pose pose) {
        poseList.add(pose);
        postValue(poseList);
    }

    public void removePose(Pose pose) {
        poseList.remove(pose);
        postValue(poseList);
    }
}
