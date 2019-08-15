package com.example.seaway.response;

import com.example.seaway.list.BuyerList;

import java.util.ArrayList;
import java.util.List;

public class BuyerResponse {

    public String result;
    public String customerID;
    public ArrayList<BuyerList> buyer;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getCustomerID() {
        return customerID;
    }

    public void setCustomerID(String customerID) {
        this.customerID = customerID;
    }
}
