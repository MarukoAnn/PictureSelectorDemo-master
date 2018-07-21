package com.yechaoa.pictureselectordemo.Modle;

/**
 * Created by moonshine on 2018/2/8.
 */

public class PostlistData {
    private ListData values;
    private String message;
    private String status;

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }


    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public ListData getValues() {
        return values;
    }

    public void setValues(ListData values) {
        this.values = values;
    }
}
