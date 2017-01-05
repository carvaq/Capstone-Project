package com.cvv.fanstaticapps.travelperfect.view.fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.cvv.fanstaticapps.travelperfect.R;
import com.cvv.fanstaticapps.travelperfect.view.PhotoTask;
import com.cvv.fanstaticapps.travelperfect.view.UiUtils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;

import butterknife.BindView;
import butterknife.OnClick;
import timber.log.Timber;

/**
 * Created by Carla
 * Date: 05/01/2017
 * Project: Capstone-Project
 */

public class NamePageFragment extends BaseFragment
        implements GoogleApiClient.OnConnectionFailedListener {

    public static final int PAGE_POSITION = 0;

    @BindView(R.id.progress_bar)
    ProgressBar mProgressBar;
    @BindView(R.id.error_name)
    View mErrorMessage;
    @BindView(R.id.plain_name_of_place)
    EditText mEditText;

    private GoogleApiClient mGoogleApiClient;

    private String mNameOfPlace;
    private String mAttributions;
    private String mFilePath;
    private boolean mTasksRunning;

    public static NamePageFragment newInstance() {
        return new NamePageFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_name_page, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        enableButtons(false, false, true);
        mProgressBar.setVisibility(View.GONE);
        mGoogleApiClient = new GoogleApiClient
                .Builder(getActivity())
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(getActivity(), this)
                .build();

        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment) getActivity()
                .getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

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

    private void processPlace(final Place place) {
        mNameOfPlace = place.getName().toString();
        int displayWidth = UiUtils.getDisplayWidth(getActivity());
        new PhotoTask(getActivity(), mGoogleApiClient, displayWidth) {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                mTasksRunning = true;
            }

            @Override
            protected void onPostExecute(AttributedPhoto attributedPhoto) {
                if (attributedPhoto != null && isAdded()) {
                    mAttributions = attributedPhoto.attribution.toString();
                    mFilePath = attributedPhoto.path;
                }
                mTasksRunning = false;
                mProgressBar.setVisibility(View.GONE);
            }
        }.execute(place.getId());
    }

    @OnClick(R.id.left_button)
    void onBackClicked() {
        if (TextUtils.isEmpty(mNameOfPlace)) {
            mNameOfPlace = mEditText.getText().toString();
        }
        if (TextUtils.isEmpty(mNameOfPlace)) {
            mErrorMessage.setVisibility(View.VISIBLE);
        }

        if (mTasksRunning) {
            mProgressBar.setVisibility(View.VISIBLE);
        } else {
            mOnUserInputSetListener.onNameOfPlaceSet(mNameOfPlace, mAttributions, mFilePath);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
