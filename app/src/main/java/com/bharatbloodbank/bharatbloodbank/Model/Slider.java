package com.bharatbloodbank.bharatbloodbank.Model;

public class Slider {
    private String Image,Heading,Data;

    public Slider() {
    }

    public Slider(String image, String heading, String data) {
        Image = image;
        Heading = heading;
        Data = data;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getHeading() {
        return Heading;
    }

    public void setHeading(String heading) {
        Heading = heading;
    }

    public String getData() {
        return Data;
    }

    public void setData(String data) {
        Data = data;
    }
}
