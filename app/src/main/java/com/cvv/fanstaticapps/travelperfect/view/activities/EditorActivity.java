package com.cvv.fanstaticapps.travelperfect.view.activities;

import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.NavUtils;
import android.support.v7.widget.SwitchCompat;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cvv.fanstaticapps.travelperfect.R;
import com.cvv.fanstaticapps.travelperfect.model.TripBuilder;
import com.cvv.fanstaticapps.travelperfect.model.TripContract;
import com.cvv.fanstaticapps.travelperfect.view.EditDialogHelper;
import com.cvv.fanstaticapps.travelperfect.view.ListItemHelper;
import com.cvv.fanstaticapps.travelperfect.view.PhotoTask;
import com.cvv.fanstaticapps.travelperfect.view.UiUtils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;

import org.joda.time.DateTime;

import butterknife.BindView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import timber.log.Timber;

public abstract class EditorActivity extends BaseActivity
        implements GoogleApiClient.OnConnectionFailedListener {

    public static final String EXTRA_TRIP_ID = "trip_id";
    public static final String DATE_FORMAT = "dd. MMM yyyy";
    public static final String TIME_FORMAT = "HH:mm";

    @BindView(R.id.plain_name_of_place)
    EditText mEditText;
    @BindView(R.id.feature_toggle)
    SwitchCompat mFeatureToggle;
    @BindView(R.id.departure_date)
    TextView mDepartureDate;
    @BindView(R.id.departure_time)
    TextView mDepartureTime;
    @BindView(R.id.departure_add)
    TextView mDepartureAdd;
    @BindView(R.id.return_date)
    TextView mReturnDate;
    @BindView(R.id.return_time)
    TextView mReturnTime;
    @BindView(R.id.return_add)
    TextView mReturnAdd;
    @BindView(R.id.item_container)
    LinearLayout mItemContainer;
    @BindView(R.id.error_name)
    TextView mErrorName;
    @BindView(R.id.error_departure)
    TextView mErrorDeparture;
    @BindView(R.id.place_autocomplete_container)
    View mAutoCompleteContainer;

    TripBuilder mTripBuilder = new TripBuilder();
    private TripBuilder COMPARE_TRIP_BUILDER;

    private EditDialogHelper mDialogHelper;
    private ListItemHelper mListItemHelper;
    private GoogleApiClient mGoogleApiClient;
    private long mTripId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        mDialogHelper = new EditDialogHelper(this);
        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();
        if (savedInstanceState != null) {
            mTripId = savedInstanceState.getLong(EXTRA_TRIP_ID, -1);
        } else {
            mTripId = getIntent().getLongExtra(EXTRA_TRIP_ID, -1);
        }
    }

    @Override
    protected void onViewsInitialized() {
        mListItemHelper = new ListItemHelper(this, mItemContainer);
        enableBackNavigation();

        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                Timber.d("Place: %s", place.getName());
                processPlace(place);
                mErrorName.setVisibility(View.GONE);
            }

            @Override
            public void onError(Status status) {
                Timber.d("An error occurred: %s", status);
            }
        });
        if (mTripId != -1) {
            Cursor cursor = getContentResolver().query(TripContract.TripEntry.buildTripUri(mTripId), null, null, null, null);
            COMPARE_TRIP_BUILDER = new TripBuilder(cursor);
            mTripBuilder.copy(COMPARE_TRIP_BUILDER);
            mListItemHelper.addListItems(mTripId);
            autocompleteFragment.setText(mTripBuilder.getTitle());
            mEditText.setText(mTripBuilder.getTitle());
        } else {
            COMPARE_TRIP_BUILDER = new TripBuilder();
        }
        setDateTimeInView(mTripBuilder.getDeparture(), mDepartureDate, mDepartureTime, mDepartureAdd);
        setDateTimeInView(mTripBuilder.getReturn(), mReturnDate, mReturnTime, mReturnAdd);
        mListItemHelper.addNewListItem();
    }

    private void setDateTimeInView(long timestamp, TextView dateView, TextView timeView, TextView addView) {
        if (timestamp > 0) {
            DateTime dateTime = new DateTime(timestamp);
            timeView.setText(dateTime.toString(TIME_FORMAT));
            dateView.setText(dateTime.toString(DATE_FORMAT));
            addView.setVisibility(View.GONE);
        } else {
            dateView.setVisibility(View.GONE);
            timeView.setVisibility(View.GONE);
        }
    }

    private void processPlace(Place place) {
        mTripBuilder.setTitle(place.getName().toString());
        int displayWidth = UiUtils.getDisplayWidth(this);
        new PhotoTask(EditorActivity.this, mGoogleApiClient, displayWidth) {
            @Override
            protected void onPostExecute(AttributedPhoto attributedPhoto) {
                if (attributedPhoto != null) {
                    mTripBuilder.setAttributions(attributedPhoto.attribution.toString())
                            .setFilePath(attributedPhoto.path);
                }
            }
        }.execute(place.getId());
    }

    @OnTextChanged(value = R.id.plain_name_of_place, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    void saveUserInput() {
        mErrorName.setVisibility(View.GONE);
        mTripBuilder.setTitle(mEditText.getText().toString());
    }

    @OnClick(value = {R.id.departure_add, R.id.return_add})
    void onAddDateClicked(View view) {
        if (view.getId() == R.id.departure_add) {
            mErrorDeparture.setVisibility(View.GONE);
            mDialogHelper.showDatePicker(mTripBuilder, mDepartureAdd, mDepartureDate, mDepartureTime);
        } else {
            mDialogHelper.showDatePicker(mTripBuilder, mReturnAdd, mReturnDate, mReturnTime);
        }
    }

    @OnClick(value = {R.id.departure_date, R.id.return_date})
    void onChangedDateClicked(View view) {
        if (view.getId() == R.id.departure_date) {
            mDialogHelper.showDatePicker(mTripBuilder, mDepartureAdd, mDepartureDate, null);
        } else {
            mDialogHelper.showDatePicker(mTripBuilder, mReturnAdd, mReturnDate, null);
        }
    }

    @OnClick(value = {R.id.departure_time, R.id.return_time})
    void onChangeTimeClicked(View view) {
        if (view.getId() == R.id.departure_time) {
            mDialogHelper.showTimePicker(mTripBuilder, mDepartureTime);
        } else {
            mDialogHelper.showTimePicker(mTripBuilder, mReturnTime);
        }
    }

    @Override
    public void onBackPressed() {
        saveTrip();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            saveTrip();
            return true;
        } else if (item.getItemId() == R.id.action_discard) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(EXTRA_TRIP_ID, mTripId);
    }

    @OnCheckedChanged(R.id.feature_toggle)
    public void onCheckedChanged(boolean isChecked) {
        if (!isChecked) {
            mDialogHelper.showFeatureDisableDialog(mFeatureToggle);
            mAutoCompleteContainer.setVisibility(View.GONE);
            mEditText.setVisibility(View.VISIBLE);
        } else {
            mAutoCompleteContainer.setVisibility(View.VISIBLE);
            mEditText.setVisibility(View.GONE);
        }
    }

    public void saveTrip() {
        if (TextUtils.isEmpty(mTripBuilder.getTitle()) && mTripBuilder.getDeparture() == 0) {
            finish();
        } else if (TextUtils.isEmpty(mTripBuilder.getTitle())) {
            mErrorName.setVisibility(View.VISIBLE);
        } else if (mTripBuilder.getDeparture() == 0) {
            mErrorDeparture.setVisibility(View.VISIBLE);
        } else {
            if (mTripId != -1) {
                String where = TripContract.TripEntry._ID + "=?";
                String[] selectionArgs = new String[]{String.valueOf(mTripId)};
                getContentResolver().update(TripContract.TripEntry.CONTENT_URI,
                        mTripBuilder.getTripContentValues(), where, selectionArgs);
            } else {
                Uri uri = getContentResolver().insert(TripContract.TripEntry.CONTENT_URI, mTripBuilder.getTripContentValues());
                mTripId = ContentUris.parseId(uri);
            }

            mListItemHelper.saveListItems(mTripId);
            finish();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        mFeatureToggle.setVisibility(View.GONE);
        mAutoCompleteContainer.setVisibility(View.GONE);
        mEditText.setVisibility(View.VISIBLE);
    }
}
