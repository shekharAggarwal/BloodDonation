package com.bharatbloodbank.bharatbloodbank.Model;

public class Contact {
    private String Name, Phone, Rating, Review;

    public Contact() {
    }

    public Contact(String name, String phone, String rating, String review) {
        Name = name;
        Phone = phone;
        Rating = rating;
        Review = review;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getRating() {
        return Rating;
    }

    public void setRating(String rating) {
        Rating = rating;
    }

    public String getReview() {
        return Review;
    }

    public void setReview(String review) {
        Review = review;
    }
}
