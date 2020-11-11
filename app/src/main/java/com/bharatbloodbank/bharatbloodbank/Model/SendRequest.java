package com.bharatbloodbank.bharatbloodbank.Model;

public class SendRequest {
    private String userPhone;
    private String RequestPhone;
    private String status;
    private boolean state;

    public SendRequest() {
    }

    public SendRequest(String userPhone, String requestPhone, String status, boolean state) {
        this.userPhone = userPhone;
        RequestPhone = requestPhone;
        this.status = status;
        this.state = state;
    }

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getRequestPhone() {
        return RequestPhone;
    }

    public void setRequestPhone(String requestPhone) {
        RequestPhone = requestPhone;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
