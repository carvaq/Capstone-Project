package com.cvv.birdsofonefeather.travelperfect.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "travel_perfect.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(
                "CREATE TABLE " + TripContract.ListItemEntry.TABLE_NAME + " (" +
                        TripContract.ListItemEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        TripContract.ListItemEntry.COLUMN_NUMBER_OF + " INTEGER NOT NULL, " +
                        TripContract.ListItemEntry.COLUMN_WHAT + " TEXT NOT NULL," +
                        TripContract.ListItemEntry.COLUMN_DONE + " SMALLINT NOT NULL," +
                        TripContract.ListItemEntry.COLUMN_TRIP_FK + " INTEGER, " +
                        "FOREIGN KEY (" + TripContract.ListItemEntry.COLUMN_TRIP_FK + ") " +
                        "REFERENCES " + TripContract.TripEntry.TABLE_NAME + " (" + TripContract.TripEntry._ID + "));");
        database.execSQL(
                "CREATE TABLE " + TripContract.ReminderEntry.TABLE_NAME + " (" +
                        TripContract.ReminderEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        TripContract.ReminderEntry.COLUMN_WHEN + " REAL NOT NULL," +
                        TripContract.ReminderEntry.COLUMN_TRIP_FK + " INTEGER, " +
                        "FOREIGN KEY (" + TripContract.ReminderEntry.COLUMN_TRIP_FK + ") " +
                        "REFERENCES " + TripContract.TripEntry.TABLE_NAME + " (" + TripContract.TripEntry._ID + "));");

        database.execSQL(
                "CREATE TABLE " + TripContract.TripEntry.TABLE_NAME + " (" +
                        TripContract.TripEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        TripContract.TripEntry.COLUMN_NAME_OF_PLACE + " TEXT NOT NULL, " +
                        TripContract.TripEntry.COLUMN_DEPARTURE + " REAL NOT NULL, " +
                        TripContract.TripEntry.COLUMN_RETURN + " REAL, " +
                        TripContract.TripEntry.COLUMN_IMAGE_URL + " TEXT, " +
                        TripContract.TripEntry.COLUMN_ATTRIBUTIONS + " TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


}