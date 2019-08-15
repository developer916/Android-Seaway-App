package com.example.seaway.form;

import android.text.TextUtils;

public class VinForm {
    public String vin;

    public VinForm() {
        this.vin = "";
    }
    public boolean is_valid() {
        return !TextUtils.isEmpty(this.vin);
    }
}
