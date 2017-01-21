package com.cvv.fanstaticapps.travelperfect.ui.helper;

import android.app.Activity;
import android.support.annotation.Nullable;
import android.support.test.espresso.IdlingResource;
import android.support.test.rule.ActivityTestRule;

/**
 * Created by Carla
 * Date: 02/01/2017
 * Project: Capstone-Project
 */

public class ViewIdlingResource implements IdlingResource {
    @Nullable
    private volatile ResourceCallback mCallback;

    private Activity mActivity;
    private int mViewId;

    public ViewIdlingResource(ActivityTestRule activityTestRule, int viewId) {
        mActivity = activityTestRule.getActivity();
        mViewId = viewId;
    }

    @Override
    public String getName() {
        return this.getClass().getName();
    }

    @Override
    public boolean isIdleNow() {
        boolean idle = !isIdle();
        if (idle && mCallback != null) {
            mCallback.onTransitionToIdle();
        }
        return idle;
    }

    private boolean isIdle() {
        return mActivity.findViewById(mViewId) != null;
    }

    @Override
    public void registerIdleTransitionCallback(ResourceCallback callback) {
        mCallback = callback;
    }

}
