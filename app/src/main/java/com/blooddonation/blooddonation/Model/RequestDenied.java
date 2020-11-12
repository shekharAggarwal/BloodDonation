package com.blooddonation.blooddonation.Model;

public class RequestDenied {

    private String phoneRequested;
    private String status;

    public RequestDenied() {
    }

    public RequestDenied(String phoneRequested, String status) {
        this.phoneRequested = phoneRequested;
        this.status = status;
    }

    public String getPhoneRequested() {
        return phoneRequested;
    }

    public void setPhoneRequested(String phoneRequested) {
        this.phoneRequested = phoneRequested;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


}
