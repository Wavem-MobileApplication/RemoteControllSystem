package com.example.remotecontrollsystem.model.utils;

import com.example.remotecontrollsystem.model.dao.UgvAttributeDao;
import com.example.remotecontrollsystem.model.entity.UgvAttribute;

public class InitializerUgvDatabase {
    private UgvAttributeDao dao;

    public InitializerUgvDatabase(UgvAttributeDao dao) {
        this.dao = dao;
    }

    public void initializeDatabase() {
        UgvAttribute attr = new UgvAttribute();
        attr.setName("UGV 실외 자율주행 차량");
        attr.setMqttAddress("tcp://10.223.188.11:1883");
        attr.setFrontRtspAddress("rtsp://admin:q1w2e3r4t5@10.223.188.36:554/profile4/media.smp");
        attr.setRearRtspAddress("rtsp://admin:q1w2e3r4t5@10.223.188.37:554/profile4/media.smp");
    }
}
