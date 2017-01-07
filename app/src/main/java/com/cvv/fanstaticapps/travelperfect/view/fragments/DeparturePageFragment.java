package com.cvv.fanstaticapps.travelperfect.view.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;

import com.cvv.fanstaticapps.travelperfect.R;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Carla
 * Date: 05/01/2017
 * Project: Capstone-Project
 */

public class DeparturePageFragment extends DatePageFragment {
    public static final int PAGE_POSITION = 1;


    @BindView(R.id.departure_time)
    TextView mDepartureTime;


    public static DeparturePageFragment newInstance() {
        return new DeparturePageFragment();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        enableButtons(true, false, true);

        mContainer.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.wizardBackground2));
        mTitle.setText(R.string.btn_add_departure);
        mAdd.setContentDescription(getString(R.string.btn_add_departure));
    }

    @Override
    public boolean canGoForward() {
        return mTimestamp > 0;
    }

    @Override
    protected TextView getTimeView() {
        return mDepartureTime;
    }


    @OnClick(R.id.right_button)
    void onForwardClicked() {
        if (canGoForward()) {
            mOnUserInputSetListener.onDepartureSet(mTimestamp);
        } else {
            mError.setVisibility(View.VISIBLE);
        }
    }

}
