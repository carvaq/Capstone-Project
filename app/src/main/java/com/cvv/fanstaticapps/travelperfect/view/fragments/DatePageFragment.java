package com.cvv.fanstaticapps.travelperfect.view.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

public abstract class DatePageFragment extends BaseFragment implements DateDialogHelper.OnDatetimeSetListener {

    @BindView(R.id.container)
    View mContainer;
    @BindView(R.id.calendar)
    ImageView mCalendar;
    @BindView(R.id.arrow)
    ImageView mArrow;
    @BindView(R.id.error)
    TextView mError;
    @BindView(R.id.date)
    TextView mDate;
    @BindView(R.id.add)
    ImageView mAdd;
    @BindView(R.id.title)
    TextView mTitle;

    private DateDialogHelper mDateDialogHelper;
    long mTimestamp;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_date_page, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mDateDialogHelper = new DateDialogHelper(getActivity());
    }

    @OnClick(R.id.date)
    void onDateClicked() {
        mDateDialogHelper.showDatePicker(mDate, null, this);
    }

    @OnClick(R.id.add)
    void addDateTime() {
        mDateDialogHelper.showDatePicker(mDate, getTimeView(), this);
    }

    @OnClick(value = {R.id.departure_time, R.id.return_time})
    void onTimeClicked() {
        mDateDialogHelper.showTimePicker(getTimeView(), mTimestamp, this);
    }

    protected abstract TextView getTimeView();

    @OnClick(R.id.left_button)
    void onBackClicked() {
        mOnUserInputSetListener.onBackClicked();
    }

    @Override
    public void onTimestampDefined(long timestamp, boolean departure) {
        mTimestamp = timestamp;
        mError.setVisibility(View.INVISIBLE);
        mAdd.setVisibility(View.GONE);
    }
}
