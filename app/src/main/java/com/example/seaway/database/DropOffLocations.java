package com.example.seaway.database;

public class DropOffLocations {
    public static final String TABLE_NAME = "dropoff_locations";

    public static final String COLUMN_ID= "id";
    public static final String COLUMN_LOCATION_NAME ="location_name";

    //create table sql query
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_ID + " TEXT,"
                    + COLUMN_LOCATION_NAME + " TEXT "
                    + ")";

    public String id;
    public String location_name;

    public DropOffLocations(){}

    public DropOffLocations(String id, String location_name) {
        this.id = id;
        this.location_name = location_name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLocationName() {
        return location_name;
    }

    public void setLocationName(String location_name) {
        this.location_name = location_name;
    }
}
