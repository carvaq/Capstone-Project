package com.cvv.birdsofonefeather.travelperfect.model;

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

    public Item() {
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
}
