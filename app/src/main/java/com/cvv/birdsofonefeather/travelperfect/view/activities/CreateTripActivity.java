package com.cvv.birdsofonefeather.travelperfect.view.activities;

import android.view.View;

import com.cvv.birdsofonefeather.travelperfect.model.TripBuilder;

public class CreateTripActivity extends EditorActivity {

    private static final TripBuilder EMPTY_TRIP_BUILDER = new TripBuilder();

    @Override
    protected void onViewsInitialized() {
        mDepartureAdd.setVisibility(View.VISIBLE);
        mDepartureDate.setVisibility(View.GONE);
        mDepartureTime.setVisibility(View.GONE);
        mReturnAdd.setVisibility(View.VISIBLE);
        mReturnDate.setVisibility(View.GONE);
        mReturnTime.setVisibility(View.GONE);
        super.onViewsInitialized();
    }

    @Override
    boolean hasTripChanges() {
        return !EMPTY_TRIP_BUILDER.equals(mTripBuilder);
    }
}