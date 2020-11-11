package com.bharatbloodbank.bharatbloodbank.Model;

public class User {
    private String phone;
    private String name;
    private String age;
    private String gender;
    private String bloodGroup;
    private String district;
    private String city;
    private String state;
    private String image;
    private String lastDonated;
    private String lastDonatedDate;

    public User() {
    }

    public User(String phone, String name, String age, String gender, String bloodGroup, String district, String city, String state, String image, String lastDonated, String lastDonatedDate) {
        this.phone = phone;
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.bloodGroup = bloodGroup;
        this.district = district;
        this.city = city;
        this.state = state;
        this.image = image;
        this.lastDonated = lastDonated;
        this.lastDonatedDate = lastDonatedDate;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getLastDonated() {
        return lastDonated;
    }

    public void setLastDonated(String lastDonated) {
        this.lastDonated = lastDonated;
    }

    public String getLastDonatedDate() {
        return lastDonatedDate;
    }

    public void setLastDonatedDate(String lastDonatedDate) {
        this.lastDonatedDate = lastDonatedDate;
    }
}
