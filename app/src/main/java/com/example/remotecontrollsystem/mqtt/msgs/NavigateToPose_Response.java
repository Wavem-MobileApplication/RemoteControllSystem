package com.example.remotecontrollsystem.mqtt.msgs;

public class NavigateToPose_Response extends RosMessage {
    private String result;
    private int code;

    public NavigateToPose_Response() {
        result = "";
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
