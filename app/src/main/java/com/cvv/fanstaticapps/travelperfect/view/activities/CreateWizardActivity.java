package com.cvv.fanstaticapps.travelperfect.view.activities;

import android.content.ContentUris;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.cvv.fanstaticapps.travelperfect.R;
import com.cvv.fanstaticapps.travelperfect.model.TripBuilder;
import com.cvv.fanstaticapps.travelperfect.model.TripContract;
import com.cvv.fanstaticapps.travelperfect.view.fragments.BaseFragment;
import com.cvv.fanstaticapps.travelperfect.view.fragments.NamePageFragment;

import butterknife.BindView;

public class CreateWizardActivity extends BaseActivity implements ViewPager.OnPageChangeListener, BaseFragment.OnUserInputSetListener {

    private static final String EXTRA_CURRENT_POSITION = "current_position";
    private static final String EXTRA_TRIP_BUILDER = "trip_builder";

    @BindView(R.id.view_pager)
    ViewPager mViewPager;
    @BindView(R.id.left_button)
    View mLeftButton;
    @BindView(R.id.right_button)
    View mRightButton;

    private TripBuilder mTripBuilder;
    private int mCurrentPosition;

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
    }

    @Override
    protected void onViewsInitialized() {
        final Fragment namePageFragment = NamePageFragment.newInstance();

        mViewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                switch (position) {
                    case NamePageFragment.PAGE_POSITION:
                        return namePageFragment;
                }
                return null;
            }

            @Override
            public int getCount() {
                return 1;
            }
        });

        mViewPager.addOnPageChangeListener(this);
        mViewPager.setCurrentItem(mCurrentPosition);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(EXTRA_CURRENT_POSITION, mCurrentPosition);
        outState.putParcelable(EXTRA_TRIP_BUILDER, mTripBuilder);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        mCurrentPosition = position;
        switch (mCurrentPosition) {
            case NamePageFragment.PAGE_POSITION:
                mLeftButton.setVisibility(View.GONE);
                mRightButton.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @Override
    public void onNameOfPlaceSet(String nameOfPlace) {
        mTripBuilder.setTitle(nameOfPlace);
    }

    @Override
    public void onDepartureSet(long departureDate) {
        mTripBuilder.setDeparture(departureDate);
    }

    @Override
    public void onReturnSet(long returnDate) {
        mTripBuilder.setDeparture(returnDate);
    }

    @Override
    public void onDone() {
        Uri uri = getContentResolver().insert(TripContract.TripEntry.CONTENT_URI, mTripBuilder.getTripContentValues());
        long tripId = ContentUris.parseId(uri);
    }
}
