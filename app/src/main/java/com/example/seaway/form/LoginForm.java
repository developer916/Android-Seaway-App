package com.example.seaway.form;

import android.text.TextUtils;

public class LoginForm {
    public String userid;
    public String password;

    public LoginForm() {
        this.userid = "";
        this.password = "";
    }
    public boolean is_valid() {
        return !TextUtils.isEmpty(this.userid) && !TextUtils.isEmpty(this.password);
    }
}
