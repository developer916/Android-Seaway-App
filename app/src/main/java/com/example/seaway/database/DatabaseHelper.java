package com.example.seaway.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.seaway.list.CustomersList;
import com.example.seaway.list.DropOffLocationList;
import com.example.seaway.list.PickupLocationList;
import com.example.seaway.list.TransporterList;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    //Database version
    private static final int DATABASE_VERSION = 17;

    //Database Name
    private static final String DATABASE_NAME = "seaway_db";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //creating tables

    @Override
    public void onCreate(SQLiteDatabase db) {
        //create tables
        db.execSQL(LoginData.CREATE_TABLE);
        db.execSQL(Customers.CREATE_TABLE);
        db.execSQL(PickupLocations.CREATE_TABLE);
        db.execSQL(DropOffLocations.CREATE_TABLE);
        db.execSQL(Transporters.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //upgrade tables
        db.execSQL("DROP TABLE IF EXISTS " + LoginData.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + Customers.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + PickupLocations.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DropOffLocations.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + Transporters.TABLE_NAME);
        onCreate(db);
    }

    //insert login data

    public void insertUpdateLoginData(String userName, String loginDate){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        String selectQuery = "SELECT * FROM " + LoginData.TABLE_NAME;
        Cursor cursor = db.rawQuery(selectQuery, null);
        int count = cursor.getCount();
        values.put(LoginData.COLUMN_NAME, "user");
        values.put(LoginData.COLUMN_USERNAME, userName);
        values.put(LoginData.COLUMN_LOGINDATE, loginDate);
        if(count >0) {
            db.execSQL("DELETE FROM " + LoginData.TABLE_NAME );
        }
        db.insert(LoginData.TABLE_NAME, null, values);
        db.close();
    }
// get login data
    public LoginData getLoginData(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(LoginData.TABLE_NAME,
                new String[]{LoginData.COLUMN_NAME, LoginData.COLUMN_USERNAME,  LoginData.COLUMN_LOGINDATE},
                LoginData.COLUMN_NAME + "= ?",
                new String[]{"user"}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();


        if(cursor.getCount() >0){

            LoginData loginData = new LoginData(
                    cursor.getString(cursor.getColumnIndex(LoginData.COLUMN_NAME)),
                    cursor.getString(cursor.getColumnIndex(LoginData.COLUMN_USERNAME)),
                    cursor.getString(cursor.getColumnIndex(LoginData.COLUMN_LOGINDATE))
            );

            cursor.close();

            return loginData;
        } else {
            cursor.close();
            return null;
        }
    }

    //insert customers
    public void insertUpdateCustomer(String id, String name){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        String selectQuery = "SELECT * FROM " + Customers.TABLE_NAME + " WHERE " + Customers.COLUMN_ID + " = " + "'" + String.valueOf(id) + "'";
        Cursor cursor = db.rawQuery(selectQuery, null);
        int count = cursor.getCount();
        values.put(Customers.COLUMN_ID, id);
        values.put(Customers.COLUMN_NAME, name);

        if(count >0) {
            db.update(Customers.TABLE_NAME, values, Customers.COLUMN_ID + " = ?",
                    new String[]{String.valueOf(id)});
        } else {
            db.insert(Customers.TABLE_NAME, null, values);
        }

        db.close();
    }

    //delete customers
    public void deleteAllCustomers() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + Customers.TABLE_NAME);
        db.close();
    }

    // get one customer

    public Customers getCustomer(String id){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(Customers.TABLE_NAME,
                new String[]{Customers.COLUMN_ID, Customers.COLUMN_NAME},
                Customers.COLUMN_ID + "=?",
                new String[]{id}, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            // prepare country object
            Customers customer = new Customers(
                    cursor.getString(cursor.getColumnIndex(Customers.COLUMN_ID)),
                    cursor.getString(cursor.getColumnIndex(Customers.COLUMN_NAME)));

            // close the db connection
            cursor.close();

            return customer;
        } else {
            // close the db connection
            cursor.close();

            return null;
        }
    }

    //get all customers

    public ArrayList<CustomersList> getAllCustomers(){
        ArrayList<CustomersList> customersLists = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + Customers.TABLE_NAME + " ORDER BY " +
                Customers.COLUMN_NAME + " ASC";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                CustomersList customer = new CustomersList();

                customer.setCustomerId(cursor.getString(cursor.getColumnIndex(Customers.COLUMN_ID)));
                customer.setCompanyName(cursor.getString(cursor.getColumnIndex(Customers.COLUMN_NAME)));
                customersLists.add(customer);
            } while (cursor.moveToNext());
        }
        db.close();
        return customersLists;
    }

    //insert & update pickup locations

    public void insertPickupLocation(String id, String location_name){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(PickupLocations.COLUMN_ID, id);
        values.put(PickupLocations.COLUMN_LOCATION_NAME, location_name);
        db.insert(PickupLocations.TABLE_NAME, null, values);
        db.close();
    }

    // delete pickup locations
    public void deletePickupLocations(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + PickupLocations.TABLE_NAME);
        db.close();
    }


    //get one item

    public PickupLocationList getPickupLocation(String id){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(PickupLocations.TABLE_NAME,
                new String[]{PickupLocations.COLUMN_ID, PickupLocations.COLUMN_LOCATION_NAME},
                PickupLocations.COLUMN_ID + "=?",
                new String[]{id}, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            // prepare country object
            PickupLocationList location = new PickupLocationList(
                    cursor.getString(cursor.getColumnIndex(PickupLocations.COLUMN_ID)),
                    cursor.getString(cursor.getColumnIndex(PickupLocations.COLUMN_LOCATION_NAME)));

            // close the db connection
            cursor.close();

            return location;
        } else {
            // close the db connection
            cursor.close();

            return null;
        }
    }

    // get all items of pickup locations
    public ArrayList<PickupLocationList> getAllPickupLocations(){
        ArrayList<PickupLocationList> locations = new ArrayList<>();
//        String selectQuery = "SELECT  * FROM " + PickupLocations.TABLE_NAME + " ORDER BY " +
//                PickupLocations.COLUMN_LOCATION_NAME + " ASC";
        String selectQuery = "SELECT  * FROM " + PickupLocations.TABLE_NAME ;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        PickupLocationList selfLocation = new PickupLocationList();
        selfLocation.setPickUpLocationID("0");
        selfLocation.setLocationName("Self Dispatch");
        locations.add(selfLocation);
        if (cursor.moveToFirst()) {
            do {
                PickupLocationList location = new PickupLocationList();

                location.setPickUpLocationID(cursor.getString(cursor.getColumnIndex(PickupLocations.COLUMN_ID)));
                location.setLocationName(cursor.getString(cursor.getColumnIndex(PickupLocations.COLUMN_LOCATION_NAME)));
                locations.add(location);
            } while (cursor.moveToNext());
        }
        db.close();
        return locations;
    }

    //insert & update pickup locations

    public void insertDropoffLocation(String id, String location_name){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(PickupLocations.COLUMN_ID, id);
        values.put(PickupLocations.COLUMN_LOCATION_NAME, location_name);
        db.insert(DropOffLocations.TABLE_NAME, null, values);
        db.close();
    }

    // delete pickup locations
    public void deleteDropoffLocation(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + DropOffLocations.TABLE_NAME);
        db.close();
    }

    //get one item of drop off location

    public DropOffLocationList getDropOffLocation(String id){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(DropOffLocations.TABLE_NAME,
                new String[]{DropOffLocations.COLUMN_ID, DropOffLocations.COLUMN_LOCATION_NAME},
                DropOffLocations.COLUMN_ID + "=?",
                new String[]{id}, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            // prepare country object
            DropOffLocationList location = new DropOffLocationList(
                    cursor.getString(cursor.getColumnIndex(DropOffLocations.COLUMN_ID)),
                    cursor.getString(cursor.getColumnIndex(DropOffLocations.COLUMN_LOCATION_NAME)));

            // close the db connection
            cursor.close();

            return location;
        } else {
            // close the db connection
            cursor.close();

            return null;
        }
    }

    // get all items of dropoff locations
    public ArrayList<DropOffLocationList> getAllDropOffLocations(){
        ArrayList<DropOffLocationList> locations = new ArrayList<>();
//        String selectQuery = "SELECT  * FROM " + DropOffLocations.TABLE_NAME + " ORDER BY " +
//                DropOffLocations.COLUMN_LOCATION_NAME + " ASC";
        String selectQuery = "SELECT  * FROM " + DropOffLocations.TABLE_NAME ;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        DropOffLocationList selfLocation = new DropOffLocationList();
        selfLocation.setDropOffLocationID("0");
        selfLocation.setLocationName("Self Dispatch");
        locations.add(selfLocation);
        if (cursor.moveToFirst()) {
            do {
                DropOffLocationList location = new DropOffLocationList();

                location.setDropOffLocationID(cursor.getString(cursor.getColumnIndex(PickupLocations.COLUMN_ID)));
                location.setLocationName(cursor.getString(cursor.getColumnIndex(PickupLocations.COLUMN_LOCATION_NAME)));
                locations.add(location);
            } while (cursor.moveToNext());
        }
        db.close();
        return locations;
    }

    // insert transporter

    public void insertTransporter(String id, String company_name){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Transporters.COLUMN_ID, id);
        values.put(Transporters.COLUMN_COMPANY_NAME, company_name);
        db.insert(Transporters.TABLE_NAME, null, values);
        db.close();
    }

    // delete pickup locations
    public void deleteTransporters(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + Transporters.TABLE_NAME);
        db.close();
    }

    //get one item of transporters

    public TransporterList getTransporter(String id){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(Transporters.TABLE_NAME,
                new String[]{Transporters.COLUMN_ID, Transporters.COLUMN_COMPANY_NAME},
                Transporters.COLUMN_ID + "=?",
                new String[]{id}, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            // prepare country object
            TransporterList transporter = new TransporterList(
                    cursor.getString(cursor.getColumnIndex(Transporters.COLUMN_ID)),
                    cursor.getString(cursor.getColumnIndex(Transporters.COLUMN_COMPANY_NAME)));

            // close the db connection
            cursor.close();

            return transporter;
        } else {
            // close the db connection
            cursor.close();

            return null;
        }
    }

    //get all transporters

    public ArrayList<TransporterList> getAllTransporters(){
        ArrayList<TransporterList> transporters = new ArrayList<>();
//        String selectQuery = "SELECT  * FROM " + Transporters.TABLE_NAME + " ORDER BY " +
//                Transporters.COLUMN_COMPANY_NAME + " ASC";
        String selectQuery = "SELECT  * FROM " + Transporters.TABLE_NAME ;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        TransporterList selfTransporter = new TransporterList();
        selfTransporter.setTransporterID("0");
        selfTransporter.setCompanyName("Self Dispatch");
        transporters.add(selfTransporter);

        if (cursor.moveToFirst()) {
            do {
                TransporterList transporter = new TransporterList();

                transporter.setTransporterID(cursor.getString(cursor.getColumnIndex(Transporters.COLUMN_ID)));
                transporter.setCompanyName(cursor.getString(cursor.getColumnIndex(Transporters.COLUMN_COMPANY_NAME)));
                transporters.add(transporter);
            } while (cursor.moveToNext());
        }
        db.close();
        return transporters;
    }


}
