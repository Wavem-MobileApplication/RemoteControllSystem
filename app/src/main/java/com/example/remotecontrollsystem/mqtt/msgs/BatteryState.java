package com.example.remotecontrollsystem.mqtt.msgs;

import java.util.HashMap;

public class BatteryState extends RosMessage {
    private Header header;
    private float voltage;
    private float temperature;
    private float current;
    private float charge;
    private float capacity;
    private float design_capacity;
    private float percentage;
    private float power_supply_status;
    private float power_supply_health;
    private float power_supply_technology;
    private boolean present;
    private HashMap<String, Float> cell_voltage;
    private HashMap<String, Float> cell_temperature;
    private String location;
    private String serial_number;

    public BatteryState() {
        header = new Header();
    }

    public Header getHeader() {
        return header;
    }

    public void setHeader(Header header) {
        this.header = header;
    }

    public float getVoltage() {
        return voltage;
    }

    public void setVoltage(float voltage) {
        this.voltage = voltage;
    }

    public float getTemperature() {
        return temperature;
    }

    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }

    public float getCurrent() {
        return current;
    }

    public void setCurrent(float current) {
        this.current = current;
    }

    public float getCharge() {
        return charge;
    }

    public void setCharge(float charge) {
        this.charge = charge;
    }

    public float getCapacity() {
        return capacity;
    }

    public void setCapacity(float capacity) {
        this.capacity = capacity;
    }

    public float getDesign_capacity() {
        return design_capacity;
    }

    public void setDesign_capacity(float design_capacity) {
        this.design_capacity = design_capacity;
    }

    public float getPercentage() {
        return percentage;
    }

    public void setPercentage(float percentage) {
        this.percentage = percentage;
    }

    public float getPower_supply_status() {
        return power_supply_status;
    }

    public void setPower_supply_status(float power_supply_status) {
        this.power_supply_status = power_supply_status;
    }

    public float getPower_supply_health() {
        return power_supply_health;
    }

    public void setPower_supply_health(float power_supply_health) {
        this.power_supply_health = power_supply_health;
    }

    public float getPower_supply_technology() {
        return power_supply_technology;
    }

    public void setPower_supply_technology(float power_supply_technology) {
        this.power_supply_technology = power_supply_technology;
    }

    public boolean isPresent() {
        return present;
    }

    public void setPresent(boolean present) {
        this.present = present;
    }

    public HashMap<String, Float> getCell_voltage() {
        return cell_voltage;
    }

    public void setCell_voltage(HashMap<String, Float> cell_voltage) {
        this.cell_voltage = cell_voltage;
    }

    public HashMap<String, Float> getCell_temperature() {
        return cell_temperature;
    }

    public void setCell_temperature(HashMap<String, Float> cell_temperature) {
        this.cell_temperature = cell_temperature;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getSerial_number() {
        return serial_number;
    }

    public void setSerial_number(String serial_number) {
        this.serial_number = serial_number;
    }
}
