package com.example.seaway.form;

import java.io.Serializable;

public class PostDispatchForm implements Serializable {
    public String years;
    public String width;
    public String weight;
    public String model;
    public String make;
    public String length;
    public String height;
    public String vin;
    public String dispatchOrderID;
    public String agreePickup;
    public Integer cashed;
    public String cbm;
    public Integer paid;
    public String color;
    public String customer;
    public String loading;
    public Integer dispatchType;
    public String pickUpLocation;
    public String dropOffLocation;
    public String transporter;
    public String orderDate;
    public String price;
    public String pickupDate;
    public String deliveryDate;
    public String deliveredDate;
    public String title;
    public String keys;
    public String storageFee;
    public String check;
    public String otherFee;
    public String lot;
    public String buyer;
    public String userId;

    public PostDispatchForm() {
        this.years = "";
        this.width = "";
        this.weight = "";
        this.model = "";
        this.make = "";
        this.length = "";
        this.height = "";
        this.vin = "";
        this.dispatchOrderID = "";
        this.cbm = "";
        this.agreePickup ="";
        this.cashed = 0;
        this.paid=0;
        this.color ="";
        this.customer ="";
        this.loading = "";
        this.dispatchType = 0;
        this.pickUpLocation="";
        this.dropOffLocation="";
        this.orderDate="";
        this.price = "";
        this.pickupDate ="";
        this.deliveryDate="";
        this.deliveredDate="";
        this.title ="";
        this.keys= "";
        this.storageFee="";
        this.check ="";
        this.otherFee="";
        this.lot = "";
        this.buyer ="";
        this.userId = "";
    }

}
