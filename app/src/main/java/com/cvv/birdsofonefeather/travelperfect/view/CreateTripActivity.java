package com.cvv.birdsofonefeather.travelperfect.view;

import com.cvv.birdsofonefeather.travelperfect.model.TripBuilder;

public class CreateTripActivity extends EditorActivity {

    @Override
    protected void initializedTripBuilder() {
        ORIGINAL_TRIP_BUILDER = new TripBuilder();
    }
}
