package com.cvv.fanstaticapps.travelperfect.view.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.cvv.fanstaticapps.travelperfect.R;
import com.cvv.fanstaticapps.travelperfect.model.TripContract;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Carla
 * Date: 31/12/2016
 * Project: Capstone-Project
 */

public abstract class BaseActivity extends AppCompatActivity {

    @Nullable
    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        onViewsInitialized();
    }

    protected abstract void onViewsInitialized();

    void enableBackNavigation() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void deleteTrip(Long id) {
        String where = TripContract.TripEntry._ID + "=?";
        String[] selectionArgs = new String[]{String.valueOf(id)};
        getContentResolver().delete(TripContract.TripEntry.CONTENT_URI, where, selectionArgs);
        where = TripContract.ListItemEntry.COLUMN_TRIP_FK + "=?";
        getContentResolver().delete(TripContract.ListItemEntry.CONTENT_URI, where, selectionArgs);
        where = TripContract.ReminderEntry.COLUMN_TRIP_FK + "=?";
        getContentResolver().delete(TripContract.ReminderEntry.CONTENT_URI, where, selectionArgs);
    }

}
