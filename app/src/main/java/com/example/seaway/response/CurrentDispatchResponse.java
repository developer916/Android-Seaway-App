package com.example.seaway.response;

import com.example.seaway.list.CurrentDispatchList;

import java.util.ArrayList;

public class CurrentDispatchResponse {
    public String result;
    public ArrayList<CurrentDispatchList> list;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
