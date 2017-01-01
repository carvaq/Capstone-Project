package com.cvv.birdsofonefeather.travelperfect.view.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.NavUtils;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.cvv.birdsofonefeather.travelperfect.R;
import com.cvv.birdsofonefeather.travelperfect.model.Item;
import com.cvv.birdsofonefeather.travelperfect.model.TripBuilder;
import com.cvv.birdsofonefeather.travelperfect.view.EditDialogHelper;
import com.cvv.birdsofonefeather.travelperfect.view.ItemAdapter;
import com.cvv.birdsofonefeather.travelperfect.view.PhotoTask;
import com.cvv.birdsofonefeather.travelperfect.view.UiUtils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;

import butterknife.BindView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import timber.log.Timber;

public abstract class EditorActivity extends BaseActivity
        implements GoogleApiClient.OnConnectionFailedListener {

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
    RecyclerView mRecyclerView;

    TripBuilder mTripBuilder = new TripBuilder();

    private EditDialogHelper mDialogHelper;
    private ItemAdapter mAdapter;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        mDialogHelper = new EditDialogHelper(this);
        mAdapter = new ItemAdapter(this);
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
            }

            @Override
            public void onError(Status status) {
                Timber.d("An error occurred: %s", status);
            }
        });
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
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
        mTripBuilder.setTitle(mEditText.getText().toString());
    }

    @OnClick(R.id.add_item)
    void addNewItem() {
        mAdapter.addItem(new Item());
    }

    @OnClick(value = {R.id.departure_add, R.id.return_add})
    void onAddDateClicked(View view) {
        if (view.getId() == R.id.departure_add) {
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
            findViewById(R.id.place_autocomplete_fragment).setVisibility(View.GONE);
            mEditText.setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.place_autocomplete_fragment).setVisibility(View.VISIBLE);
            mEditText.setVisibility(View.GONE);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        mFeatureToggle.setVisibility(View.GONE);
        findViewById(R.id.place_autocomplete_fragment).setVisibility(View.GONE);
        mEditText.setVisibility(View.VISIBLE);
    }

}
