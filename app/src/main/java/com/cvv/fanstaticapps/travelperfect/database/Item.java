package com.cvv.fanstaticapps.travelperfect.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.provider.BaseColumns;

/**
 * Created by Carla
 * Date: 01/01/2017
 * Project: Capstone-Project
 */
public class Item {
    private long mId;
    private int mNumberOf;
    private String mWhat;
    private boolean mDone;
    private long mTripId;

    public Item(int numberOf, String what, boolean done, long tripId) {
        mNumberOf = numberOf;
        mWhat = what;
        mDone = done;
        mTripId = tripId;
    }

    public Item(Cursor cursor) {
        this.mId = cursor.getLong(cursor.getColumnIndex(BaseColumns._ID));
        this.mNumberOf = cursor.getInt(cursor.getColumnIndex(TripContract.ListItemEntry.COLUMN_NUMBER_OF));
        this.mWhat = cursor.getString(cursor.getColumnIndex(TripContract.ListItemEntry.COLUMN_WHAT));
        this.mDone = cursor.getInt(cursor.getColumnIndex(TripContract.ListItemEntry.COLUMN_DONE)) == 1;
    }

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        mId = id;
    }

    public int getNumberOf() {
        return mNumberOf;
    }

    public void setNumberOf(int numberOf) {
        this.mNumberOf = numberOf;
    }

    public String getWhat() {
        return mWhat;
    }

    public void setWhat(String what) {
        this.mWhat = what;
    }

    public boolean isDone() {
        return mDone;
    }

    public void setDone(boolean done) {
        mDone = done;
    }

    public ContentValues getContentValues() {
        ContentValues contentValues = new ContentValues(4);
        contentValues.put(TripContract.ListItemEntry.COLUMN_NUMBER_OF, mNumberOf);
        contentValues.put(TripContract.ListItemEntry.COLUMN_WHAT, mWhat);
        contentValues.put(TripContract.ListItemEntry.COLUMN_DONE, mDone);
        contentValues.put(TripContract.ListItemEntry.COLUMN_TRIP_FK, mTripId);
        return contentValues;
    }
}
