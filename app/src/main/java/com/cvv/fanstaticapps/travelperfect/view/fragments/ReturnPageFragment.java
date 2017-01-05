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

public class ReturnPageFragment extends BaseFragment implements DateDialogHelper.OnDatetimeSetListener {
    public static final int PAGE_POSITION = 2;


    @BindView(R.id.return_date)
    TextView mReturnDate;
    @BindView(R.id.return_time)
    TextView mReturnTime;
    @BindView(R.id.return_add)
    View mReturnAdd;
    @BindView(R.id.error_return)
    TextView mErrorReturn;

    private DateDialogHelper mDateDialogHelper;
    private long mReturn;

    public static ReturnPageFragment newInstance() {
        return new ReturnPageFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_return_page, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        enableButtons(true, true, true);
        mDateDialogHelper = new DateDialogHelper(getActivity());
    }

    @OnClick(R.id.return_add)
    void addReturnDateTime() {
        mDateDialogHelper.showDatePicker(mReturnAdd, mReturnDate, mReturnTime, this);
    }

    @OnClick(R.id.left_button)
    void onBackClicked() {
        mOnUserInputSetListener.onBackClicked();
    }


    @OnClick(R.id.right_button)
    void onForwardClicked() {
        if (mReturn > 0) {
            mOnUserInputSetListener.onReturnSet(mReturn);
            mOnUserInputSetListener.onDone();
        } else {
            mErrorReturn.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.skip_button)
    void onSkipClicked() {
        mOnUserInputSetListener.onDone();
    }

    @Override
    public void onTimestampDefined(long timestamp, boolean departure) {
        mReturn = timestamp;
        mErrorReturn.setVisibility(View.GONE);
    }
}
