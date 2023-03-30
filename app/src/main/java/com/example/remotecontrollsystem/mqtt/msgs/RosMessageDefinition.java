package com.example.remotecontrollsystem.mqtt.msgs;

public class RosMessageDefinition {
    private String name;
    private String type;
    private String message_type;
    private String request_type;
    private String response_type;
    private String feedback_type;
    private int qos;
    boolean retained;

    public static RosMessageDefinition PUB(String name, String message_type) {
        return PUB(name, message_type, 0, false);
    }

    public static RosMessageDefinition PUB(String name, String message_type, int qos) {
        return PUB(name, message_type, qos, false);
    }

    public static RosMessageDefinition PUB(String name, String message_type, boolean retained) {
        return PUB(name, message_type,0, retained);
    }

    public static RosMessageDefinition PUB(String name, String message_type, int qos, boolean retained) {
        return new RosMessageDefinition(name, "pub", message_type, "", "", "", qos, retained);
    }

    public static RosMessageDefinition SUB(String name, String message_type) {
        return SUB(name, message_type, 0, false);
    }

    public static RosMessageDefinition SUB(String name, String message_type, int qos) {
        return SUB(name, message_type, qos, false);
    }

    public static RosMessageDefinition SUB(String name, String message_type, boolean retained) {
        return SUB(name, message_type, 0, false);
    }

    public static RosMessageDefinition SUB(String name, String message_type, int qos, boolean retained) {
        return new RosMessageDefinition(name, "sub", message_type, "", "", "", qos, retained);
    }

    public static RosMessageDefinition GOAL(String name, String message_type, String request_type, String response_type, String feedback_type, int qos, boolean retained) {
        return new RosMessageDefinition(name, "goal", message_type, request_type, response_type, feedback_type, qos, retained);
    }

    public static RosMessageDefinition CALL(String name, String message_type, String request_type, String response_type, int qos, boolean retained) {
        return new RosMessageDefinition(name, "call", message_type, request_type, response_type, "", qos, retained);
    }

    private RosMessageDefinition(String name, String type, String message_type, String request_type, String response_type, String feedback_type, int qos, boolean retained) {
        this.name = name;
        this.type = type;
        this.message_type = message_type;
        this.request_type = request_type;
        this.response_type = response_type;
        this.feedback_type = feedback_type;
        this.qos = qos;
        this.retained = retained;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getMessage_type() {
        return message_type;
    }

    public String getRequest_type() {
        return request_type;
    }

    public String getResponse_type() {
        return response_type;
    }

    public String getFeedback_type() {
        return feedback_type;
    }

    public int getQos() {
        return qos;
    }

    public boolean isRetained() {
        return retained;
    }
}
