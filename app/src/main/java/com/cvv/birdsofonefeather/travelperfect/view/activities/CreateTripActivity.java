package com.cvv.birdsofonefeather.travelperfect.view.activities;

import android.view.View;

import com.cvv.birdsofonefeather.travelperfect.model.TripBuilder;

public class CreateTripActivity extends EditorActivity {

    @Override
    protected void initializedTripBuilder() {
        ORIGINAL_TRIP_BUILDER = new TripBuilder();
        mDepartureAdd.setVisibility(View.VISIBLE);
        mDepartureDate.setVisibility(View.GONE);
        mDepartureTime.setVisibility(View.GONE);
        mReturnAdd.setVisibility(View.VISIBLE);
        mReturnDate.setVisibility(View.GONE);
        mReturnTime.setVisibility(View.GONE);
    }
}