package com.blooddonation.blooddonation.Model;

public class Banner {

    private String userPhone;
    private String bannerImage;
    private String bannerName;
    private String bannerLastDate;
    private String status;
    private String BannerDetail;

    public Banner() {
    }

    public Banner(String userPhone, String bannerImage, String bannerName, String bannerLastDate, String status, String bannerDetail) {
        this.userPhone = userPhone;
        this.bannerImage = bannerImage;
        this.bannerName = bannerName;
        this.bannerLastDate = bannerLastDate;
        this.status = status;
        BannerDetail = bannerDetail;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getBannerImage() {
        return bannerImage;
    }

    public void setBannerImage(String bannerImage) {
        this.bannerImage = bannerImage;
    }

    public String getBannerName() {
        return bannerName;
    }

    public void setBannerName(String bannerName) {
        this.bannerName = bannerName;
    }

    public String getBannerLastDate() {
        return bannerLastDate;
    }

    public void setBannerLastDate(String bannerLastDate) {
        this.bannerLastDate = bannerLastDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getBannerDetail() {
        return BannerDetail;
    }

    public void setBannerDetail(String bannerDetail) {
        BannerDetail = bannerDetail;
    }
}
