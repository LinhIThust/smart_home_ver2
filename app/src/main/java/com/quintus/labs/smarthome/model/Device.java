package com.quintus.labs.smarthome.model;

public class Device {
    private int status;
    private String id;
    private String name;

    public Device() {

    }

    public Device(int status, String idDevice, String nameDevice) {
        this.status = status;
        this.id = idDevice;
        this.name = nameDevice;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String idDevice) {
        this.id = idDevice;
    }

    public String getName() {
        return name;
    }

    public void setName(String nameDevice) {
        this.name = nameDevice;
    }

    @Override
    public String toString() {
        return "Device{" +
                "status=" + status +
                ", idDevice='" + id + '\'' +
                ", nameDevice='" + name + '\'' +
                '}';
    }
}
