package com.cvv.birdsofonefeather.travelperfect.view.activities;

import android.view.View;

public class EditTripActivity extends EditorActivity {

    @Override
    protected void initializedTripBuilder() {
        mDepartureAdd.setVisibility(View.GONE);
        mDepartureDate.setVisibility(View.VISIBLE);
        mDepartureTime.setVisibility(View.VISIBLE);
        mReturnAdd.setVisibility(View.GONE);
        mReturnDate.setVisibility(View.VISIBLE);
        mReturnTime.setVisibility(View.VISIBLE);
    }
}