package com.example.remotecontrollsystem.mqtt.utils;

public enum WidgetType {
    MAP("지도"),
    ROBOT_POSE("차량 위치"),
    LASER_SCAN("라이다 센서"),
    TF("동적 기준 좌표"),
    TF_STATIC("정적 기준 좌표"),
    CMD_VEL_PUB("수동 제어"),
    CMD_VEL_SUB("차량 속도"),
    ODOM("위치/이동 정보"),
    GLOBAL_PLAN("예상 주행 경로"),
    LOCAL_PLAN("목표 주행 경로"),
    INITIAL_POSE("차량 위치 갱신");

    private final String type;

    WidgetType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public static int size() {
        return values().length;
    }
}
