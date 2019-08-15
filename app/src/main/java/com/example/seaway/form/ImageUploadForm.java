package com.example.seaway.form;

import android.text.TextUtils;

public class ImageUploadForm {
    public String image;
    public Integer DispatchID;

    public ImageUploadForm() {

        this.image = "";
        this.DispatchID = 0;
    }

    public boolean is_valid() {

        return (!TextUtils.isEmpty(this.image) && this.DispatchID != 0);
    }

}
