package com.example.seaway.response;

import com.example.seaway.list.TransporterList;

import java.util.ArrayList;

public class TransporterResponse {
    public String result;
    public ArrayList<TransporterList> transporters;


    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
