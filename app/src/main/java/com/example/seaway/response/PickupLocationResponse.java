package com.example.seaway.response;

import com.example.seaway.list.PickupLocationList;

import java.util.ArrayList;

public class PickupLocationResponse {

    public String result;
    public ArrayList<PickupLocationList> pickupLocations;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
