package com.example.seaway.form;

import android.text.TextUtils;

public class CustomForm {

    public String result;

    public CustomForm() {
        this.result = "";
    }
    public boolean is_valid() {
        return !TextUtils.isEmpty(this.result);
    }
}
