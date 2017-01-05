package com.cvv.fanstaticapps.travelperfect.view.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;

import com.cvv.fanstaticapps.travelperfect.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by Carla
 * Date: 05/01/2017
 * Project: Capstone-Project
 */

public abstract class BaseFragment extends Fragment {

    @BindView(R.id.left_button)
    View mBackButton;
    @BindView(R.id.skip_button)
    View mSkipButton;
    @BindView(R.id.right_button)
    View mRightButton;

    protected OnUserInputSetListener mOnUserInputSetListener;
    private Unbinder mUnbinder;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mUnbinder = ButterKnife.bind(this, view);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnUserInputSetListener) {
            mOnUserInputSetListener = (OnUserInputSetListener) context;
        } else {
            throw new RuntimeException("OnUserInputSetListener has to be instantiated");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mOnUserInputSetListener = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }

    public interface OnUserInputSetListener {
        void onNameOfPlaceSet(String nameOfPlace, String attributions, String filePath);

        void onDepartureSet(long departureDate);

        void onReturnSet(long returnDate);

        void onDone();
    }

    @OnClick(R.id.left_button)
    void onBackClicked() {
    }


    @OnClick(R.id.right_button)
    void onForwardClicked() {
    }


    @OnClick(R.id.skip_button)
    void onSkipClicked() {
    }
}
