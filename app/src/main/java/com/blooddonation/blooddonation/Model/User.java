package com.blooddonation.blooddonation.Model;

public class User {
    private String typeUser;
    private String phone;
    private String name;
    private String age;
    private String gender;
    private String bloodGroup;
    private String room;
    private String block;
    private String batch;
    private String address;
    private String city;
    private String state;
    private String requested;
    private String donated;
    private String state_city_blood;
    private String image;
    private boolean admin;

    public User() {
    }

    public User(String typeUser, String phone, String name, String age, String gender, String bloodGroup, String room, String block, String batch, String requested, String donated, String state_city_blood, String image) {
        this.typeUser = typeUser;
        this.phone = phone;
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.bloodGroup = bloodGroup;
        this.room = room;
        this.block = block;
        this.batch = batch;
        this.requested = requested;
        this.donated = donated;
        this.state_city_blood = state_city_blood;
        this.image = image;
        this.admin = false;
    }

    public User(String typeUser, String phone, String name, String age, String gender, String bloodGroup, String batch, String address, String city, String state, String requested, String donated, String state_city_blood, String image) {
        this.typeUser = typeUser;
        this.phone = phone;
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.bloodGroup = bloodGroup;
        this.batch = batch;
        this.address = address;
        this.city = city;
        this.state = state;
        this.requested = requested;
        this.donated = donated;
        this.state_city_blood = state_city_blood;
        this.image = image;
        this.admin = false;
    }

    public User(String typeUser, String phone, String name, String age, String gender, String bloodGroup, String address, String city, String state, String requested, String donated, String state_city_blood, String image,boolean isAdmin) {
        this.typeUser = typeUser;
        this.phone = phone;
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.bloodGroup = bloodGroup;
        this.batch = batch;
        this.address = address;
        this.city = city;
        this.state = state;
        this.requested = requested;
        this.donated = donated;
        this.state_city_blood = state_city_blood;
        this.image = image;
        this.admin = false;
    }

    public User(String typeUser, String phone, String name, String age, String gender, String bloodGroup, String room, String block, String batch, String address, String city, String state, String requested, String donated, String state_city_blood, String image) {
        this.typeUser = typeUser;
        this.phone = phone;
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.bloodGroup = bloodGroup;
        this.room = room;
        this.block = block;
        this.batch = batch;
        this.address = address;
        this.city = city;
        this.state = state;
        this.requested = requested;
        this.donated = donated;
        this.state_city_blood = state_city_blood;
        this.image = image;
        this.admin = false;
    }



    public String getTypeUser() {
        return typeUser;
    }

    public void setTypeUser(String typeUser) {
        this.typeUser = typeUser;
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

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getBlock() {
        return block;
    }

    public void setBlock(String block) {
        this.block = block;
    }

    public String getBatch() {
        return batch;
    }

    public void setBatch(String batch) {
        this.batch = batch;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public String getRequested() {
        return requested;
    }

    public void setRequested(String requested) {
        this.requested = requested;
    }

    public String getDonated() {
        return donated;
    }

    public void setDonated(String donated) {
        this.donated = donated;
    }

    public String getState_city_blood() {
        return state_city_blood;
    }

    public void setState_city_blood(String state_city_blood) {
        this.state_city_blood = state_city_blood;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }
}
