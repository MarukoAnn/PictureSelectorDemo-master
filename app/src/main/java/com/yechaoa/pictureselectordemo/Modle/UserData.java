package com.yechaoa.pictureselectordemo.Modle;

/**
 * Created by moonshine on 2018/3/13.
 */

public class UserData {
    private String id;
    private String User;
    private String name;
    private String brithday;
    private String idnum;
    private String phone;

    public UserData(String id, String User, String name, String idnum, String brithday, String phone) {
        super();
        this.id = id;
        this.User = User;
        this.name = name;
        this.idnum = idnum;
        this.phone = phone;
        this.brithday = brithday;
    }
    public String getUser() {
        return User;
    }

    public void setUser(String user) {
        User = user;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhone() {
        return phone;
    }

    public String getBrithday() {
        return brithday;
    }

    public String getIdnum() {
        return idnum;
    }

    public String getName() {
        return name;
    }

    public void setBrithday(String brithday) {
        this.brithday = brithday;
    }

    public void setIdnum(String idnum) {
        this.idnum = idnum;
    }

    public void setName(String name) {
        this.name = name;
    }
}
