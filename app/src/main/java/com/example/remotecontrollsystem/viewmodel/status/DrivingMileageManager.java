package com.example.remotecontrollsystem.viewmodel.status;

import android.util.Log;

import com.example.remotecontrollsystem.mqtt.Mqtt;
import com.example.remotecontrollsystem.mqtt.data.MessagePublisher;
import com.example.remotecontrollsystem.mqtt.data.Observer;
import com.example.remotecontrollsystem.mqtt.msgs.Odometry;
import com.example.remotecontrollsystem.mqtt.utils.WidgetType;
import com.example.remotecontrollsystem.viewmodel.RobotStatusViewModel;

public class DrivingMileageManager {
    private double preX, preY;
    private double mileage;

    private MessagePublisher<Odometry> odomPublisher;

    public DrivingMileageManager(RobotStatusViewModel robotStatusViewModel) {
        odomPublisher = Mqtt.getInstance().getMessagePublisher(WidgetType.ODOM.getType());
    }

    public void startToAccumulateDrivingMileage() {
        odomPublisher.attach(odomObserver);
    }

    public void finishToAccumulateDrivingMileage() {
        odomPublisher.detach(odomObserver);
    }

    private Observer<Odometry> odomObserver = new Observer<Odometry>() {
        @Override
        public void update(Odometry odometry) {
            double newX = Math.abs(odometry.getPose().getPose().getPosition().getX()); // 새로 들어온 데이터
            double newY = Math.abs(odometry.getPose().getPose().getPosition().getY());

            if (preX != newX && preY != newY) { // 직전 데이터와 현재 데이터가 다를시에만 거리 합산
                double x = Math.abs(newX - preX); // 직전 데이터와 현재 데이터의 차
                double y = Math.abs(newY - preY);
                double distance = Math.sqrt(x * x + y * y); // 두 점 사이의 거리 공식으로 거리 계산

                mileage += distance; // 계산한 거리 mileage에 누적
                Log.d("Mileage", String.valueOf(mileage));
            }

            preX = newX; // 직전 데이터 현재 데이터로 업데이트
            preY = newY;
        }
    };
}
