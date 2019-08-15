package com.example.seaway.response;

import com.example.seaway.list.CustomersList;

import java.util.List;

public class CustomerResponse {

    public String result;
    public List<CustomersList> customers;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
