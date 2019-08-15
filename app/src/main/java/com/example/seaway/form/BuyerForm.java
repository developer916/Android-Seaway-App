package com.example.seaway.form;

import android.text.TextUtils;

public class BuyerForm {
    public String result;
    public String id;

    public BuyerForm() {
        this.result = "";
        this.id = "";
    }
    public boolean is_valid() {
        return (!TextUtils.isEmpty(this.result) && !TextUtils.isEmpty(this.id)) ;
    }
}
