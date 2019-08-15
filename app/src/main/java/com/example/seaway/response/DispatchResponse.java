package com.example.seaway.response;

public class DispatchResponse {
    public String result;
    public Integer dispatchID;
    public Integer dispatchOrderID;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public Integer getDispatchID() {
        return dispatchID;
    }

    public void setDispatchID(Integer dispatchID) {
        this.dispatchID = dispatchID;
    }

    public Integer getDispatchOrderID() {
        return dispatchOrderID;
    }

    public void setDispatchOrderID(Integer dispatchOrderID) {
        this.dispatchOrderID = dispatchOrderID;
    }
}
