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

public class ReturnPageFragment extends DatePageFragment {
    public static final int PAGE_POSITION = 2;


    @BindView(R.id.return_time)
    TextView mReturnTime;

    public static ReturnPageFragment newInstance() {
        return new ReturnPageFragment();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        enableButtons(true, true, true);

        mContainer.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.wizardBackground3));
        mTitle.setText(R.string.btn_add_return);
        mCalendar.setScaleX(-1);
        mArrow.setScaleX(-1);
        mAdd.setContentDescription(getString(R.string.btn_add_return));
    }

    @Override
    protected TextView getTimeView() {
        return mReturnTime;
    }

    @OnClick(R.id.skip_button)
    void onSkipClicked() {
        mOnUserInputSetListener.onDone();
    }


    @OnClick(R.id.right_button)
    void onForwardClicked() {
        if (mTimestamp > 0) {
            mOnUserInputSetListener.onReturnSet(mTimestamp);
        } else {
            mOnUserInputSetListener.onDone();
        }
    }

}
