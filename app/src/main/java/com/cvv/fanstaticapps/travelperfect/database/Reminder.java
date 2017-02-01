package com.cvv.fanstaticapps.travelperfect.database;

import android.content.ContentValues;
import android.database.Cursor;

/**
 * Created by Carla
 * Date: 01/02/2017
 * Project: Capstone-Project
 */

public class Reminder {

    private Long mId;
    private Long mTimestamp;
    private Long mTripId;

    public Reminder(Long timestamp, Long tripId) {
        mTimestamp = timestamp;
        mTripId = tripId;
    }

    public Reminder(Cursor cursor) {
        mId = cursor.getLong(cursor.getColumnIndex(TripContract.ReminderEntry._ID));
        mTimestamp = cursor.getLong(cursor.getColumnIndex(TripContract.ReminderEntry.COLUMN_WHEN));
    }

    public Long getId() {
        return mId;
    }

    public void setId(Long id) {
        mId = id;
    }

    public Long getTimestamp() {
        return mTimestamp;
    }

    public void setTimestamp(Long timestamp) {
        mTimestamp = timestamp;
    }

    public Long getTripId() {
        return mTripId;
    }

    public void setTripId(Long tripId) {
        mTripId = tripId;
    }

    public ContentValues getContentValues() {
        ContentValues contentValues = new ContentValues(1);
        contentValues.put(TripContract.ReminderEntry.COLUMN_WHEN, mTimestamp);
        contentValues.put(TripContract.ReminderEntry.COLUMN_TRIP_FK, mTripId);
        return contentValues;
    }
}
