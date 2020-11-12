package com.blooddonation.blooddonation.Model;

public class sendRequest {
    private String phoneRequested,phoneReceived;
    private String status,name,image;

    public sendRequest() {
    }

    public sendRequest(String phoneRequested, String phoneReceived, String status, String name, String image) {
        this.phoneRequested = phoneRequested+":"+status;
        this.phoneReceived = phoneReceived;
        this.status = status;
        this.name = name;
        this.image = image;
    }

    public String getPhoneRequested() {
        return phoneRequested;
    }

    public void setPhoneRequested(String phoneRequested) {
        this.phoneRequested = phoneRequested;
    }

    public String getPhoneReceived() {
        return phoneReceived;
    }

    public void setPhoneReceived(String phoneReceived) {
        this.phoneReceived = phoneReceived;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
