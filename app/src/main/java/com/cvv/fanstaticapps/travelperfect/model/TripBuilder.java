package com.cvv.fanstaticapps.travelperfect.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Carla
 * Date: 31/12/2016
 * Project: Capstone-Project
 */

public class TripBuilder implements Parcelable {
    private String mTitle;
    private String mFilePath;
    private String mAttributions;
    private long mDeparture;
    private long mReturn;
    private int mProgress;

    public TripBuilder() {
    }

    public TripBuilder(Cursor cursor) {
        cursor.moveToFirst();
        mTitle = cursor.getString(cursor.getColumnIndex(TripContract.TripEntry.COLUMN_NAME_OF_PLACE));
        mDeparture = cursor.getLong(cursor.getColumnIndex(TripContract.TripEntry.COLUMN_DEPARTURE));
        mReturn = cursor.getLong(cursor.getColumnIndex(TripContract.TripEntry.COLUMN_RETURN));
        mFilePath = cursor.getString(cursor.getColumnIndex(TripContract.TripEntry.COLUMN_IMAGE_URL));
        mAttributions = cursor.getString(cursor.getColumnIndex(TripContract.TripEntry.COLUMN_ATTRIBUTIONS));
        mProgress = cursor.getInt(cursor.getColumnIndex(TripContract.TripEntry.COLUMN_PROGRESS));
    }

    protected TripBuilder(Parcel in) {
        mTitle = in.readString();
        mFilePath = in.readString();
        mAttributions = in.readString();
        mDeparture = in.readLong();
        mReturn = in.readLong();
        mProgress = in.readInt();
    }

    public static final Creator<TripBuilder> CREATOR = new Creator<TripBuilder>() {
        @Override
        public TripBuilder createFromParcel(Parcel in) {
            return new TripBuilder(in);
        }

        @Override
        public TripBuilder[] newArray(int size) {
            return new TripBuilder[size];
        }
    };

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getFilePath() {
        return mFilePath;
    }

    public void setFilePath(String filePath) {
        mFilePath = filePath;
    }

    public String getAttributions() {
        return mAttributions;
    }

    public void setAttributions(String attributions) {
        mAttributions = attributions;
    }

    public long getDeparture() {
        return mDeparture;
    }

    public void setDeparture(long departure) {
        mDeparture = departure;
    }

    public long getReturn() {
        return mReturn;
    }

    public void setReturn(long aReturn) {
        mReturn = aReturn;
    }

    public int getProgress() {
        return mProgress;
    }

    public void setProgress(int progress) {
        mProgress = progress;
    }

    public ContentValues getTripContentValues() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(TripContract.TripEntry.COLUMN_NAME_OF_PLACE, mTitle);
        contentValues.put(TripContract.TripEntry.COLUMN_DEPARTURE, mDeparture);
        contentValues.put(TripContract.TripEntry.COLUMN_RETURN, mReturn);
        contentValues.put(TripContract.TripEntry.COLUMN_IMAGE_URL, mFilePath);
        contentValues.put(TripContract.TripEntry.COLUMN_ATTRIBUTIONS, mAttributions);
        contentValues.put(TripContract.TripEntry.COLUMN_PROGRESS, mProgress);
        return contentValues;
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
                ", mProgress=" + mProgress +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mTitle);
        dest.writeString(mFilePath);
        dest.writeString(mAttributions);
        dest.writeLong(mDeparture);
        dest.writeLong(mReturn);
        dest.writeInt(mProgress);
    }
}
