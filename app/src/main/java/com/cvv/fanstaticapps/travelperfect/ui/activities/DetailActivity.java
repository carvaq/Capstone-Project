package com.cvv.fanstaticapps.travelperfect.ui.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.cvv.fanstaticapps.travelperfect.R;
import com.cvv.fanstaticapps.travelperfect.ui.fragments.DetailFragment;

import butterknife.BindView;

public class DetailActivity extends BaseActivity {

    public static final int RESULT_DELETE = 123;

    public static final String DATE_FORMAT = "dd. MMM yyyy";
    public static final String TIME_FORMAT = "HH:mm";

    @Nullable
    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    private DetailFragment mDetailFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        mDetailFragment = (DetailFragment) getSupportFragmentManager().findFragmentById(R.id.detail_fragment);
        if (savedInstanceState == null) {
            long tripId = getIntent().getLongExtra(DetailFragment.ARGS_TRIP_ID, -1);
            boolean deleteAsDiscard = getIntent().getBooleanExtra(DetailFragment.ARGS_DISCARD_EQUALS_DELETE, false);
            mDetailFragment.initialize(tripId, deleteAsDiscard);
        }
        supportPostponeEnterTransition();
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            mDetailFragment.saveTrip();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        mDetailFragment.saveTrip();
        super.onBackPressed();
    }
}
