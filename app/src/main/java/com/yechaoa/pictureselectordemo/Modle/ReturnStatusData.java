package com.yechaoa.pictureselectordemo.Modle;

/**
 * Created by moonshine on 2018/1/27.
 */

public class ReturnStatusData {
    private String status;
    private String  sid;
    private String SurplusNum;
    private String  systemInfo;

    public String getStatus() {
        return status;
    }

    public String setStatus(String status) {
        this.status = status;
        return status;
    }

    public String getSid() {
        return sid;
    }

    public String setSid(String sid) {
        this.sid = sid;
        return sid;
    }

    public String getSurplusNum() {
        return SurplusNum;
    }

    public void setSurplusNum(String surplusNum) {
        SurplusNum = surplusNum;
    }

    public String getSystemInfo() {
        return systemInfo;
    }

    public void setSystemInfo(String systemInfo) {
        this.systemInfo = systemInfo;
    }
}
