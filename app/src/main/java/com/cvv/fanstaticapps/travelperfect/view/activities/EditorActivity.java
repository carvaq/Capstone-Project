package com.cvv.fanstaticapps.travelperfect.view.activities;

import android.content.ContentUris;
import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.NavUtils;
import android.support.v7.widget.SwitchCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cvv.fanstaticapps.travelperfect.R;
import com.cvv.fanstaticapps.travelperfect.model.Item;
import com.cvv.fanstaticapps.travelperfect.model.TripBuilder;
import com.cvv.fanstaticapps.travelperfect.model.TripContract;
import com.cvv.fanstaticapps.travelperfect.view.EditDialogHelper;
import com.cvv.fanstaticapps.travelperfect.view.PhotoTask;
import com.cvv.fanstaticapps.travelperfect.view.UiUtils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import timber.log.Timber;

public abstract class EditorActivity extends BaseActivity
        implements GoogleApiClient.OnConnectionFailedListener, EditDialogHelper.OnSaveClickListener {

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

    private EditDialogHelper mDialogHelper;
    private GoogleApiClient mGoogleApiClient;

    private View.OnFocusChangeListener mOnFocusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus) {
                addNewItem();
            }
        }
    };

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
    }

    @Override
    protected void onViewsInitialized() {
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

    void addNewItem() {
        for (int i = 0; i < mItemContainer.getChildCount(); i++) {
            View child = mItemContainer.getChildAt(i);
            child.findViewById(R.id.number_of).setOnFocusChangeListener(null);
            child.findViewById(R.id.name).setOnFocusChangeListener(null);
        }
        View view = LayoutInflater.from(this).inflate(R.layout.item_list_item, mItemContainer, false);
        view.findViewById(R.id.number_of).setOnFocusChangeListener(mOnFocusChangeListener);
        view.findViewById(R.id.name).setOnFocusChangeListener(mOnFocusChangeListener);
        mItemContainer.addView(view, mItemContainer.getChildCount());
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

    abstract boolean hasTripChanges();

    @Override
    public void onBackPressed() {
        if (hasTripChanges()) {
            mDialogHelper.showSaveDialog(mTripBuilder);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home && hasTripChanges()) {
            mDialogHelper.showSaveDialog(mTripBuilder);
            return true;
        } else if (item.getItemId() == R.id.action_discard) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
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

    @Override
    public void onSaveClicked() {
        if (TextUtils.isEmpty(mTripBuilder.getTitle())) {
            mErrorName.setVisibility(View.VISIBLE);
        } else if (mTripBuilder.getDeparture() == 0) {
            mErrorDeparture.setVisibility(View.VISIBLE);
        } else {
            Uri uri = getContentResolver().insert(TripContract.TripEntry.CONTENT_URI, mTripBuilder.getTripContentValues());
            long tripId = ContentUris.parseId(uri);
            saveListItems(tripId);
            finish();
        }
    }

    private void saveListItems(long tripId) {
        List<ContentValues> contentValues = new ArrayList<>();
        for (int i = 0; i < mItemContainer.getChildCount(); i++) {
            View child = mItemContainer.getChildAt(i);
            TextView name = (TextView) child.findViewById(R.id.name);
            TextView numberOf = (TextView) child.findViewById(R.id.number_of);
            CheckBox checkBox = (CheckBox) child.findViewById(R.id.checkbox);
            int number = 0;
            if (!TextUtils.isEmpty(numberOf.getText())) {
                number = Integer.parseInt(numberOf.getText().toString());
            }
            Item item = new Item(number, name.getText().toString(), checkBox.isChecked(), tripId);
            contentValues.add(item.getContentValues());
        }
        ContentValues[] contentValuesArray = new ContentValues[contentValues.size()];
        getContentResolver().bulkInsert(TripContract.ListItemEntry.CONTENT_URI, contentValues.toArray(contentValuesArray));
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        mFeatureToggle.setVisibility(View.GONE);
        mAutoCompleteContainer.setVisibility(View.GONE);
        mEditText.setVisibility(View.VISIBLE);
    }
}
