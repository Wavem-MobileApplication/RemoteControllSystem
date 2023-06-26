package com.example.remotecontrollsystem.mqtt.msgs;

public class GpsMoveResponse extends RosMessage {
    private Header header;
    private int result_code;
    private String message;

    public GpsMoveResponse() {
        header = new Header();
    }

    public Header getHeader() {
        return header;
    }

    public void setHeader(Header header) {
        this.header = header;
    }

    public int getResult_code() {
        return result_code;
    }

    public void setResult_code(int result_code) {
        this.result_code = result_code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
