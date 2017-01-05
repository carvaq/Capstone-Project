package com.cvv.fanstaticapps.travelperfect.view.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cvv.fanstaticapps.travelperfect.R;

/**
 * Created by Carla
 * Date: 05/01/2017
 * Project: Capstone-Project
 */

public class NamePageFragment extends BaseFragment {

    public static final int PAGE_POSITION = 0;

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
    }

}
