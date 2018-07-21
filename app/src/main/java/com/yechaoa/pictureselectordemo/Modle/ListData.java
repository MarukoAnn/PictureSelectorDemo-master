package com.yechaoa.pictureselectordemo.Modle;

import java.util.List;

/**
 * Created by moonshine on 2018/2/8.
 */

public class ListData {
    private String id;
    private String itemcode;
    private String longitude;
    private String latitude;
    private String unitcode;
    private String itemdetail;
    private String itemmembers;
    private String itemname;

    public ListData(String id,String itemcode ,String itemname ,String itemdetail,String unitcode ,String itemmembers ,String longitude,String latitude) {
        super();
        this.id = id;
        this.itemcode =itemcode;
        this.itemname = itemname;
        this.itemdetail =itemdetail;
        this.unitcode = unitcode;
        this.itemmembers = itemmembers;
        this.longitude=longitude;
        this.latitude =latitude;
    }

    public String getItemcode() {
        return itemcode;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getUnitcode() {
        return unitcode;
    }

    public String getItemdetail() {
        return itemdetail;
    }

    public String getItemmembers() {
        return itemmembers;
    }

    public String getItemname() {
        return itemname;
    }

    public void setItemcode(String itemcode) {
        this.itemcode = itemcode;
    }

    public void setItemdetail(String itemdetail) {
        this.itemdetail = itemdetail;
    }

    public void setItemmembers(String itemmembers) {
        this.itemmembers = itemmembers;
    }

    public void setItemname(String itemname) {
        this.itemname = itemname;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public void setUnitcode(String unitcode) {
        this.unitcode = unitcode;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
