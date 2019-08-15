package com.example.seaway.database;

public class Customers {

    public static final String TABLE_NAME = "customers";

    public static final String COLUMN_ID= "id";
    public static final String COLUMN_NAME ="Name";

    //create table sql query
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_ID + " TEXT,"
                    + COLUMN_NAME + " TEXT "
                    + ")";

    public String id;
    public String name;

    public Customers(){}

    public Customers(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
