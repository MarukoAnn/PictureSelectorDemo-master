package com.yechaoa.pictureselectordemo.Modle;

import java.util.List;

public class SelectDatadb {
    private String itemcode;
    private String itemname;
    private List<String> normal;
    private List<String>  inspector;
    private double longitude;
    private double  latitude;
    private String longitudeIP;
    private String latitudeIP;
    private String description;

    public String getItemname() {
        return itemname;
    }

    public double getLatitude() {
        return latitude;
    }

    public String getItemcode() {
        return itemcode;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getInspector() {
        return inspector;
    }

    public String getLatitudeIP() {
        return latitudeIP;
    }

    public String getLongitudeIP() {
        return longitudeIP;
    }

    public double getLongitude() {
        return longitude;
    }

    public List<String> getNormal() {
        return normal;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setItemname(String itemname) {
        this.itemname = itemname;
    }

    public void setItemcode(String itemcode) {
        this.itemcode = itemcode;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setInspector(List<String> inspector) {
        this.inspector = inspector;
    }

    public void setLatitudeIP(String latitudeIP) {
        this.latitudeIP = latitudeIP;
    }

    public void setLongitudeIP(String longitudeIP) {
        this.longitudeIP = longitudeIP;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setNormal(List<String> normal) {
        this.normal = normal;
    }
}
