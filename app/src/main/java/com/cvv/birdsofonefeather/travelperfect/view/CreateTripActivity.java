package com.cvv.birdsofonefeather.travelperfect.view;

import android.os.Bundle;

import com.cvv.birdsofonefeather.travelperfect.R;

public class CreateTripActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_trip);
    }

    @Override
    protected void onViewsInitialized() {
        enableBackNavigation();
    }
}
