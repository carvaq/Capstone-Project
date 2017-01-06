package com.cvv.fanstaticapps.travelperfect.view.activities;

import android.content.ContentUris;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;

import com.cvv.fanstaticapps.travelperfect.R;
import com.cvv.fanstaticapps.travelperfect.model.TripBuilder;
import com.cvv.fanstaticapps.travelperfect.model.TripContract;
import com.cvv.fanstaticapps.travelperfect.view.fragments.BaseFragment;
import com.cvv.fanstaticapps.travelperfect.view.fragments.DeparturePageFragment;
import com.cvv.fanstaticapps.travelperfect.view.fragments.NamePageFragment;
import com.cvv.fanstaticapps.travelperfect.view.fragments.ReturnPageFragment;

import butterknife.BindView;

public class CreateWizardActivity extends BaseActivity implements BaseFragment.OnUserInputSetListener {

    private static final String EXTRA_CURRENT_POSITION = "current_position";
    private static final String EXTRA_TRIP_BUILDER = "trip_builder";

    @BindView(R.id.view_pager)
    ViewPager mViewPager;

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
    protected void onViewsInitialized() {
        final BaseFragment namePageFragment = NamePageFragment.newInstance();
        final BaseFragment departurePageFragment = DeparturePageFragment.newInstance();
        final BaseFragment returnPageFragment = ReturnPageFragment.newInstance();

        mViewPager.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                switch (position) {
                    case NamePageFragment.PAGE_POSITION:
                        return namePageFragment;
                    case DeparturePageFragment.PAGE_POSITION:
                        return departurePageFragment;
                    case ReturnPageFragment.PAGE_POSITION:
                        return returnPageFragment;
                }
                return null;
            }

            @Override
            public int getCount() {
                return 3;
            }
        });

        updateViewPager();
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
        intent.putExtra(DetailActivity.EXTRA_TRIP_ID, tripId);
        startActivity(intent);
        finish();
    }

    private void nextPage() {
        mCurrentPosition++;
        updateViewPager();
    }

    private void updateViewPager() {
        mViewPager.setCurrentItem(mCurrentPosition);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(mStatusBarColor[mCurrentPosition]);
        }
    }

    private void previousPage() {
        mCurrentPosition--;
        updateViewPager();
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
