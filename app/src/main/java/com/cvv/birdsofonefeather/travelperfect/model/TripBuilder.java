package com.cvv.birdsofonefeather.travelperfect.model;

import android.content.ContentValues;

/**
 * Created by Carla
 * Date: 31/12/2016
 * Project: Capstone-Project
 */

public class TripBuilder {
    private String mTitle;
    private String mFilePath;
    private String mAttributions;
    private long mDeparture;
    private long mReturn;

    public TripBuilder setTitle(String title) {
        mTitle = title;
        return this;
    }

    public TripBuilder setFilePath(String filePath) {
        mFilePath = filePath;
        return this;
    }

    public TripBuilder setAttributions(String attributions) {
        mAttributions = attributions;
        return this;
    }

    public TripBuilder setDeparture(long departure) {
        mDeparture = departure;
        return this;
    }

    public TripBuilder setReturn(long aReturn) {
        mReturn = aReturn;
        return this;
    }

    public ContentValues getTripContentValues() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(TripContract.TripEntry.COLUMN_NAME_OF_PLACE, mTitle);
        contentValues.put(TripContract.TripEntry.COLUMN_DEPARTURE, mDeparture);
        contentValues.put(TripContract.TripEntry.COLUMN_RETURN, mReturn);
        contentValues.put(TripContract.TripEntry.COLUMN_IMAGE_URL, mFilePath);
        contentValues.put(TripContract.TripEntry.COLUMN_ATTRIBUTIONS, mAttributions);
        //contentValues.put(TripContract.TripEntry.COLUMN_LIST_ITEM_FK, );
        //contentValues.put(TripContract.TripEntry.COLUMN_REMINDER_FK, );
        return contentValues;
    }

    public long getDeparture() {
        return mDeparture;
    }

    public long getReturn() {
        return mReturn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TripBuilder that = (TripBuilder) o;

        if (mDeparture != that.mDeparture) return false;
        if (mReturn != that.mReturn) return false;
        return mTitle != null ? mTitle.equals(that.mTitle) : that.mTitle == null;
    }

    @Override
    public int hashCode() {
        int result = mTitle != null ? mTitle.hashCode() : 0;
        result = 31 * result + (int) (mDeparture ^ (mDeparture >>> 32));
        result = 31 * result + (int) (mReturn ^ (mReturn >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "TripBuilder{" +
                "mTitle='" + mTitle + '\'' +
                ", mFilePath='" + mFilePath + '\'' +
                ", mAttributions='" + mAttributions + '\'' +
                ", mDeparture=" + mDeparture +
                ", mReturn=" + mReturn +
                '}';
    }
}
