package com.cvv.birdsofonefeather.travelperfect.model;

import android.content.ContentValues;

import java.util.ArrayList;
import java.util.List;

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
    private List<Item> mItems = new ArrayList<>();

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

    public TripBuilder addItem(int numberOf, String name) {
        mItems.add(new Item(numberOf, name));
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

    public class Item {
        private int numberOf;
        private String what;

        public Item(int numberOf, String what) {
            this.numberOf = numberOf;
            this.what = what;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TripBuilder that = (TripBuilder) o;

        if (mDeparture != that.mDeparture) return false;
        if (mReturn != that.mReturn) return false;
        if (mTitle != null ? !mTitle.equals(that.mTitle) : that.mTitle != null) return false;
        return mItems != null ? mItems.equals(that.mItems) : that.mItems == null;

    }

    @Override
    public int hashCode() {
        int result = mTitle != null ? mTitle.hashCode() : 0;
        result = 31 * result + (int) (mDeparture ^ (mDeparture >>> 32));
        result = 31 * result + (int) (mReturn ^ (mReturn >>> 32));
        result = 31 * result + (mItems != null ? mItems.hashCode() : 0);
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
                ", mItems=" + mItems +
                '}';
    }
}
