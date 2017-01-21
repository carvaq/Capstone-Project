package com.cvv.fanstaticapps.travelperfect.ui.helper;

import android.support.test.espresso.ViewInteraction;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Created by Carla
 * Date: 02/01/2017
 * Project: Capstone-Project
 */

public class ViewHelper {
    public static ViewInteraction findView(int resId) {
        return onView(withId(resId));
    }

    public static void scrollToAndType(int resId, String text) {
        findView(resId).perform(scrollTo(), replaceText(text), closeSoftKeyboard());
    }

    public static void scrollToAndClick(int resId) {
        findView(resId).perform(scrollTo(), click());
    }

    public static void performClick(int resId) {
        findView(resId).perform(click());
    }
}
