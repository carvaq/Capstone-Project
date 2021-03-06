package com.cvv.fanstaticapps.travelperfect.ui;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.preference.PreferenceManager;
import android.view.Display;

/**
 * Created by Carla
 * Date: 31/12/2016
 * Project: Capstone-Project
 */

public class UiUtils {
    private static final String KEY_FEATURE_TOGGLE_DIALOG_SHOWN = "feature_toggle_dialog_shown";
    private static final double RATIO = 0.5;

    public static int getDisplayWidth(Activity context) {
        Display display = context.getWindowManager().getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        return point.x;
    }

    public static int getProportionalHeight(int width) {
        return (int) Math.ceil(width * RATIO);
    }

    public static void setFeatureDialogShownPref(Context context) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(KEY_FEATURE_TOGGLE_DIALOG_SHOWN, true)
                .apply();
    }

    public static boolean featureToggleDialogAlreadyShown(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(KEY_FEATURE_TOGGLE_DIALOG_SHOWN, false);
    }
}
