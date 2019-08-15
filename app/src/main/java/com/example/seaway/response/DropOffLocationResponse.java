package com.example.seaway.response;

import com.example.seaway.list.DropOffLocationList;

import java.util.ArrayList;

public class DropOffLocationResponse {

    public String result;
    public ArrayList<DropOffLocationList> dropOfflocations;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
