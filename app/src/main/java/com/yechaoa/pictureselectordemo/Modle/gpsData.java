package com.yechaoa.pictureselectordemo.Modle;

public class gpsData {
    private  static  String longitude;
    private  static  String  latitude;
    private static String id;
    private static String itemcode;
    private static String unitcode;
    private static String itemdetail;
    private static String itemmembers;
    private static String itemname;

    public static void setLongitude(String longitude) {
        gpsData.longitude = longitude;
    }

    public static void setLatitude(String latitude) {
        gpsData.latitude = latitude;
    }

    public static String getLatitude() {
        return latitude;
    }

    public static String getLongitude() {
        return longitude;
    }

    public static String getId() {
        return id;
    }

    public static void setId(String id) {
        gpsData.id = id;
    }

    public static String getItemcode() {
        return itemcode;
    }

    public static void setItemcode(String itemcode) {
        gpsData.itemcode = itemcode;
    }

    public static String getUnitcode() {
        return unitcode;
    }

    public static void setUnitcode(String unitcode) {
        gpsData.unitcode = unitcode;
    }

    public static String getItemdetail() {
        return itemdetail;
    }

    public static void setItemdetail(String itemdetail) {
        gpsData.itemdetail = itemdetail;
    }

    public static String getItemmembers() {
        return itemmembers;
    }

    public static void setItemmembers(String itemmembers) {
        gpsData.itemmembers = itemmembers;
    }

    public static String getItemname() {
        return itemname;
    }

    public static void setItemname(String itemname) {
        gpsData.itemname = itemname;
    }
}
