package com.cvv.fanstaticapps.travelperfect.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cvv.fanstaticapps.travelperfect.R;
import com.cvv.fanstaticapps.travelperfect.database.TripContract;

/**
 * The configuration screen for the {@link TripWidget TripWidget} AppWidget.
 */
public class TripWidgetConfigureActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final String PREFS_NAME = "com.cvv.fanstaticapps.travelperfect.widget.TripWidget";
    private static final String PREF_PREFIX_KEY = "appwidget_";
    private static final int ID_LOADER = 123;
    private static final String[] TRIP_PROJECTION = new String[]{
            TripContract.TripEntry.COLUMN_NAME_OF_PLACE,
            TripContract.TripEntry._ID
    };

    int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

    private LinearLayout mAppWidgetList;

    static void saveTripIdPref(Context context, int appWidgetId, long id) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putLong(PREF_PREFIX_KEY + appWidgetId, id);
        prefs.apply();
    }

    static long loadTitlePref(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        return prefs.getLong(PREF_PREFIX_KEY + appWidgetId, 0);
    }

    static void deleteTitlePref(Context context, int appWidgetId) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.remove(PREF_PREFIX_KEY + appWidgetId);
        prefs.apply();
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(RESULT_CANCELED);

        setContentView(R.layout.trip_widget_configure);
        mAppWidgetList = (LinearLayout) findViewById(R.id.appwidget_list);

        // Find the widget id from the intent.
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        // If this activity was started with an intent without an app widget ID, finish with an error.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
            return;
        }

        getSupportLoaderManager().initLoader(ID_LOADER, null, this);
    }

    private void addView(Cursor cursor) {
        TextView title = (TextView) getLayoutInflater()
                .inflate(R.layout.item_widget_trip_configuration, mAppWidgetList, false);
        title.setText(cursor.getString(cursor.getColumnIndex(TripContract.TripEntry.COLUMN_NAME_OF_PLACE)));
        final long id = cursor.getLong(cursor.getColumnIndex(TripContract.TripEntry._ID));
        title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = TripWidgetConfigureActivity.this;
                saveTripIdPref(context, mAppWidgetId, id);

                // It is the responsibility of the configuration activity to update the app widget
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                TripWidget.updateAppWidget(context, appWidgetManager, mAppWidgetId);

                // Make sure we pass back the original appWidgetId
                Intent resultValue = new Intent();
                resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
                setResult(RESULT_OK, resultValue);
                finish();

            }
        });
        mAppWidgetList.addView(title);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri queryUri = TripContract.TripEntry.CONTENT_URI;
        String sortOrder = TripContract.TripEntry.COLUMN_DEPARTURE + " ASC";

        return new CursorLoader(this,
                queryUri,
                TRIP_PROJECTION,
                null,
                null,
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.getCount() > 0) {
            while (data.moveToNext()) {
                addView(data);
            }
        } else {
            Toast.makeText(TripWidgetConfigureActivity.this, R.string.widget_no_trips_message, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

}

