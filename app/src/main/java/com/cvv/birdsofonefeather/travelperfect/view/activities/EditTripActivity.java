package com.cvv.birdsofonefeather.travelperfect.view.activities;

import android.view.View;

public class EditTripActivity extends EditorActivity {

    public static final String EXTRA_TRIP_ID = "trip_id";


    @Override
    protected void onViewsInitialized() {
        mDepartureAdd.setVisibility(View.GONE);
        mDepartureDate.setVisibility(View.VISIBLE);
        mDepartureTime.setVisibility(View.VISIBLE);
        mReturnAdd.setVisibility(View.GONE);
        mReturnDate.setVisibility(View.VISIBLE);
        mReturnTime.setVisibility(View.VISIBLE);

        super.onViewsInitialized();
    }

    @Override
    boolean hasTripChanges() {
        return false;
    }
}