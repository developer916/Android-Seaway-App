package com.example.seaway.database;

public class Transporters {

    public static final String TABLE_NAME = "transporters";

    public static final String COLUMN_ID= "id";
    public static final String COLUMN_COMPANY_NAME ="company_name";

    //create table sql query
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_ID + " TEXT,"
                    + COLUMN_COMPANY_NAME + " TEXT "
                    + ")";
    public String id;
    public String company_name;

    public Transporters(){}

    public Transporters(String id, String company_name) {
        this.id = id;
        this.company_name = company_name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCompanyName() {
        return company_name;
    }

    public void setCompanyName(String company_name) {
        this.company_name = company_name;
    }
}
