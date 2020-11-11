package com.bharatbloodbank.bharatbloodbank.Model;

import java.util.ArrayList;
import java.util.List;

public class States {
        private ArrayList<String> districts;
    private String state;

//    public States(List<String> districts, String state) {
//        this.districts = districts;
//        this.state = state;
//    }


    public ArrayList<String> getDistricts() {
        return districts;
    }

    public void setDistricts(ArrayList<String> districts) {
        this.districts = districts;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
