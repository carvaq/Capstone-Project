package com.cvv.fanstaticapps.travelperfect.ui.activities;


import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.ViewInteraction;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.cvv.fanstaticapps.travelperfect.R;
import com.cvv.fanstaticapps.travelperfect.ui.helper.ViewHelper;
import com.cvv.fanstaticapps.travelperfect.ui.helper.ViewIdlingResource;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.registerIdlingResources;
import static android.support.test.espresso.Espresso.unregisterIdlingResources;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class CreationTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void testTripCreation() {
        ViewHelper.performClick(R.id.fab);

        IdlingResource idlingResource = new ViewIdlingResource(mActivityTestRule, R.id.plain_name_of_place);
        registerIdlingResources(idlingResource);

        ViewHelper.scrollToAndType(R.id.plain_name_of_place, "Madagascar");

        ViewHelper.performClick(android.R.id.button1);
        ViewHelper.performClick(android.R.id.button1);

        SimpleDateFormat dateFormat = new SimpleDateFormat(DetailActivity.DATE_FORMAT, Locale.getDefault());
        SimpleDateFormat timeFormat = new SimpleDateFormat(DetailActivity.TIME_FORMAT, Locale.getDefault());

        onView(allOf(withId(R.id.departure_date), withText(dateFormat.format(new Date())), isDisplayed()));
        onView(allOf(withId(R.id.departure_time), withText(timeFormat.format(new Date())), isDisplayed()));

        ViewHelper.scrollToAndType(R.id.number_of, "1");
        ViewHelper.scrollToAndType(R.id.name, "Shoes");

        ViewInteraction appCompatImageButton = onView(
                allOf(withContentDescription("Navigate up"),
                        withParent(withId(R.id.toolbar)),
                        isDisplayed()));
        appCompatImageButton.perform(click());

        unregisterIdlingResources(idlingResource);
        idlingResource = new ViewIdlingResource(mActivityTestRule, R.id.title);
        registerIdlingResources(idlingResource);

        ViewInteraction textView = ViewHelper.findView(R.id.title);
        textView.check(matches(withText("Madagascar")));

        unregisterIdlingResources(idlingResource);
    }
}
