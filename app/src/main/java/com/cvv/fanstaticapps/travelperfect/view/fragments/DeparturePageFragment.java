package com.cvv.fanstaticapps.travelperfect.view.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cvv.fanstaticapps.travelperfect.R;
import com.cvv.fanstaticapps.travelperfect.view.DateDialogHelper;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Carla
 * Date: 05/01/2017
 * Project: Capstone-Project
 */

public class DeparturePageFragment extends BaseFragment implements DateDialogHelper.OnDatetimeSetListener {
    public static final int PAGE_POSITION = 1;


    @BindView(R.id.departure_date)
    TextView mDepartureDate;
    @BindView(R.id.departure_time)
    TextView mDepartureTime;
    @BindView(R.id.departure_add)
    View mDepartureAdd;
    @BindView(R.id.error_departure)
    TextView mErrorDeparture;

    private DateDialogHelper mDateDialogHelper;
    private long mDeparture;

    public static DeparturePageFragment newInstance() {
        return new DeparturePageFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_departure_page, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        enableButtons(true, false, true);
        mDateDialogHelper = new DateDialogHelper(getActivity());
    }

    @OnClick(R.id.departure_add)
    void addDepartureDateTime() {
        mDateDialogHelper.showDatePicker(mDepartureAdd, mDepartureDate, mDepartureTime, this);
    }

    @OnClick(R.id.left_button)
    void onBackClicked() {
        mOnUserInputSetListener.onBackClicked();
    }


    @OnClick(R.id.right_button)
    void onForwardClicked() {
        if (mDeparture > 0) {
            mOnUserInputSetListener.onDepartureSet(mDeparture);
        } else {
            mErrorDeparture.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onTimestampDefined(long timestamp, boolean departure) {
        mDeparture = timestamp;
        mErrorDeparture.setVisibility(View.GONE);
    }
}
