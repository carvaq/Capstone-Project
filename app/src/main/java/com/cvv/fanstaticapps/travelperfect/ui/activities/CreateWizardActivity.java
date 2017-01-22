package com.cvv.fanstaticapps.travelperfect.ui.activities;

import android.content.ContentUris;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;

import com.cvv.fanstaticapps.travelperfect.R;
import com.cvv.fanstaticapps.travelperfect.database.TripBuilder;
import com.cvv.fanstaticapps.travelperfect.database.TripContract;
import com.cvv.fanstaticapps.travelperfect.ui.fragments.DeparturePageFragment;
import com.cvv.fanstaticapps.travelperfect.ui.fragments.DetailFragment;
import com.cvv.fanstaticapps.travelperfect.ui.fragments.NamePageFragment;
import com.cvv.fanstaticapps.travelperfect.ui.fragments.ReturnPageFragment;
import com.cvv.fanstaticapps.travelperfect.ui.fragments.WizardFragment;

public class CreateWizardActivity extends BaseActivity implements WizardFragment.OnUserInputSetListener {

    private static final String EXTRA_CURRENT_POSITION = "current_position";
    private static final String EXTRA_TRIP_BUILDER = "trip_builder";

    private TripBuilder mTripBuilder;
    private int mCurrentPosition;
    private int[] mStatusBarColor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_wizard);

        if (savedInstanceState != null) {
            mTripBuilder = savedInstanceState.getParcelable(EXTRA_TRIP_BUILDER);
            mCurrentPosition = savedInstanceState.getInt(EXTRA_CURRENT_POSITION);
        } else {
            mTripBuilder = new TripBuilder();
        }
        mStatusBarColor = new int[]{
                ContextCompat.getColor(this, R.color.wizardBackground1),
                ContextCompat.getColor(this, R.color.wizardBackground2),
                ContextCompat.getColor(this, R.color.wizardBackground3)
        };
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        updateCurrentFragment();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(EXTRA_CURRENT_POSITION, mCurrentPosition);
        outState.putParcelable(EXTRA_TRIP_BUILDER, mTripBuilder);
    }

    private void createTripAndShowDetail() {
        Uri uri = getContentResolver().insert(TripContract.TripEntry.CONTENT_URI, mTripBuilder.getTripContentValues());
        long tripId = ContentUris.parseId(uri);
        Intent intent = new Intent(this, DetailActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
        intent.putExtra(DetailFragment.ARGS_DISCARD_EQUALS_DELETE, true);
        intent.putExtra(DetailFragment.ARGS_TRIP_ID, tripId);
        startActivity(intent);
        finish();
    }

    private void nextPage() {
        mCurrentPosition++;
        updateCurrentFragment();
    }

    private void updateCurrentFragment() {
        Fragment fragment = getCurrentFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager()
                .beginTransaction();
        if (mCurrentPosition != 0) {
            fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                    .addToBackStack(fragment.toString());

        }
        fragmentTransaction
                .add(R.id.fragment_wizard_container, fragment)
                .commit();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(mStatusBarColor[mCurrentPosition]);
        }
    }

    @Nullable
    private Fragment getCurrentFragment() {
        switch (mCurrentPosition) {
            case NamePageFragment.PAGE_POSITION:
                return NamePageFragment.newInstance();
            case DeparturePageFragment.PAGE_POSITION:
                return DeparturePageFragment.newInstance();
            case ReturnPageFragment.PAGE_POSITION:
                return ReturnPageFragment.newInstance();
            default:
                return null;
        }
    }

    private void previousPage() {
        mCurrentPosition--;
        getSupportFragmentManager().popBackStack();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(mStatusBarColor[mCurrentPosition]);
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mCurrentPosition--;
    }

    @Override
    public void onNameOfPlaceSet(String nameOfPlace, String attributions, String filePath) {
        mTripBuilder.setTitle(nameOfPlace);
        mTripBuilder.setAttributions(attributions);
        mTripBuilder.setFilePath(filePath);
        nextPage();
    }

    @Override
    public void onDepartureSet(long departureDate) {
        mTripBuilder.setDeparture(departureDate);
        nextPage();
    }

    @Override
    public void onReturnSet(long returnDate) {
        mTripBuilder.setReturn(returnDate);
        createTripAndShowDetail();
    }

    @Override
    public void onDone() {
        createTripAndShowDetail();
    }

    @Override
    public void onBackClicked() {
        previousPage();
    }
}
