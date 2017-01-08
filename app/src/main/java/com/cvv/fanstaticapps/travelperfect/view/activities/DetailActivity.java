package com.cvv.fanstaticapps.travelperfect.view.activities;

import android.os.Bundle;
import android.view.MenuItem;

import com.cvv.fanstaticapps.travelperfect.R;
import com.cvv.fanstaticapps.travelperfect.view.fragments.DetailFragment;

public class DetailActivity extends BaseActivity {

    public static final int RESULT_DELETE = 123;

    public static final String DATE_FORMAT = "dd. MMM yyyy";
    public static final String TIME_FORMAT = "HH:mm";

    private DetailFragment mDetailFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        mDetailFragment = (DetailFragment) getSupportFragmentManager().findFragmentById(R.id.detail_fragment);
        if (savedInstanceState == null) {
            long tripId = getIntent().getLongExtra(DetailFragment.ARGS_TRIP_ID, -1);
            boolean deleteAsDiscard = getIntent().getBooleanExtra(DetailFragment.ARGS_DISCARD_EQUALS_DELETE, false);
            mDetailFragment.initialize(tripId, deleteAsDiscard, false);
        }
    }

    @Override
    protected void onViewsInitialized() {
        enableBackNavigation();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            mDetailFragment.saveTrip();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mDetailFragment.saveTrip();
    }
}
