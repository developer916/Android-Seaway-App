package com.example.seaway.database;

public class LoginData {
    public static final String TABLE_NAME = "login_data";

    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_USERNAME ="userName";
    public static final String COLUMN_LOGINDATE = "loginDate";

    //create table sql query
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_NAME + " TEXT,"
                    + COLUMN_USERNAME + " TEXT,"
                    + COLUMN_LOGINDATE + " TEXT "
                    + ")";

    public String name;
    public String userName;
    public String loginDate;

    public LoginData(){}

    public LoginData(String name, String userName, String loginDate) {
        this.name = name;
        this.userName = userName;
        this.loginDate = loginDate;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getLoginDate() {
        return loginDate;
    }

    public void setLoginDate(String loginDate) {
        this.loginDate = loginDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
