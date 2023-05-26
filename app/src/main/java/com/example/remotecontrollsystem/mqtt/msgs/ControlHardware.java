package com.example.remotecontrollsystem.mqtt.msgs;

public class ControlHardware extends RosMessage {
    private boolean horn;
    private boolean head_light;
    private boolean left_light;
    private boolean right_light;

    public ControlHardware() {
        horn = false;
        head_light = false;
        left_light = false;
        right_light = false;
    }

    public boolean isHorn() {
        return horn;
    }

    public void setHorn(boolean horn) {
        this.horn = horn;
    }

    public boolean isHead_light() {
        return head_light;
    }

    public void setHead_light(boolean head_light) {
        this.head_light = head_light;
    }

    public boolean isLeft_light() {
        return left_light;
    }

    public void setLeft_light(boolean left_light) {
        this.left_light = left_light;
    }

    public boolean isRight_light() {
        return right_light;
    }

    public void setRight_light(boolean right_light) {
        this.right_light = right_light;
    }
}
