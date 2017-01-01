package com.cvv.birdsofonefeather.travelperfect.view;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SwitchCompat;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.cvv.birdsofonefeather.travelperfect.R;
import com.cvv.birdsofonefeather.travelperfect.model.TripBuilder;
import com.cvv.birdsofonefeather.travelperfect.model.TripContract;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;

import butterknife.BindView;
import timber.log.Timber;

public class CreateTripActivity extends BaseActivity
        implements GoogleApiClient.OnConnectionFailedListener, CompoundButton.OnCheckedChangeListener {

    private GoogleApiClient mGoogleApiClient;
    private TripBuilder mTripBuilder = new TripBuilder();
    private final TripBuilder EMPTY_TRIP_BUILDER = new TripBuilder();

    @BindView(R.id.plain_name_of_place)
    EditText mEditText;
    @BindView(R.id.feature_toggle)
    SwitchCompat mFeatureToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_trip);
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
        mFeatureToggle.setOnCheckedChangeListener(this);
    }

    private void processPlace(Place place) {
        mTripBuilder.setTitle(place.getName().toString());
        int displayWidth = UiUtils.getDisplayWidth(this);
        new PhotoTask(CreateTripActivity.this, mGoogleApiClient, displayWidth) {
            @Override
            protected void onPostExecute(AttributedPhoto attributedPhoto) {
                if (attributedPhoto != null) {
                    mTripBuilder.setAttributions(attributedPhoto.attribution.toString())
                            .setFilePath(attributedPhoto.path);
                }
            }
        }.execute(place.getId());
    }

    @Override
    public void onBackPressed() {
        if (isTripEmpty()) {
            super.onBackPressed();
        } else {
            showSaveDialog();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home && !isTripEmpty()) {
            showSaveDialog();
            return true;
        } else if (item.getItemId() == R.id.action_discard) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private boolean isTripEmpty() {
        return EMPTY_TRIP_BUILDER.equals(mTripBuilder);
    }

    private void showSaveDialog() {
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (DialogInterface.BUTTON_POSITIVE == which) {
                    getContentResolver().insert(TripContract.TripEntry.CONTENT_URI, mTripBuilder.getTripContentValues());
                }
                finish();
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(CreateTripActivity.this);
        builder.setMessage(R.string.dialog_save_before_exiting_text)
                .setTitle(R.string.dialog_title_attention)
                .setPositiveButton(R.string.btn_save, listener)
                .setNegativeButton(R.string.btn_discard, listener)
                .show();
    }

    private void showFeatureDisableDialog() {
        if (UiUtils.featureToggleDialogAlreadyShown(CreateTripActivity.this)) {
            return;
        }
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (DialogInterface.BUTTON_NEGATIVE == which) {
                    mFeatureToggle.setChecked(true);
                    UiUtils.setFeatureDialogShownPref(CreateTripActivity.this);
                }
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(CreateTripActivity.this);
        builder.setMessage(R.string.dialog_disabling_google_search_text)
                .setTitle(R.string.dialog_title_attention)
                .setPositiveButton(R.string.btn_save, listener)
                .setNegativeButton(R.string.btn_discard, listener)
                .show();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (!isChecked) {
            showFeatureDisableDialog();
            findViewById(R.id.place_autocomplete_fragment).setVisibility(View.GONE);
            mEditText.setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.place_autocomplete_fragment).setVisibility(View.VISIBLE);
            mEditText.setVisibility(View.GONE);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
