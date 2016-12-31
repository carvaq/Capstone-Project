package com.cvv.birdsofonefeather.travelperfect.view;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.cvv.birdsofonefeather.travelperfect.R;
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
        implements GoogleApiClient.OnConnectionFailedListener {

    @BindView(R.id.place_image)
    ImageView placesImage;

    private GoogleApiClient mGoogleApiClient;

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
    }

    private void processPlace(Place place) {
        new PhotoTask(mGoogleApiClient, placesImage.getWidth(), placesImage.getHeight()) {
            @Override
            protected void onPostExecute(AttributedPhoto attributedPhoto) {
                if (attributedPhoto != null) {
                    // Photo has been loaded, display it.
                    placesImage.setImageBitmap(attributedPhoto.bitmap);
                    TextView mText = (TextView) findViewById(R.id.attributions);
                    // Display the attribution as HTML content if set.
                    if (attributedPhoto.attribution == null) {
                        mText.setVisibility(View.GONE);
                    } else {
                        mText.setVisibility(View.VISIBLE);
                        mText.setText(Html.fromHtml(attributedPhoto.attribution.toString()));
                    }

                }
            }
        }.execute(place.getId());
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
