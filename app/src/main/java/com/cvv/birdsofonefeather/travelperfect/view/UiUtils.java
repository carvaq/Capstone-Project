package com.cvv.birdsofonefeather.travelperfect.view;

import android.app.Activity;
import android.graphics.Point;
import android.view.Display;

/**
 * Created by Carla
 * Date: 31/12/2016
 * Project: Capstone-Project
 */

public class UiUtils {
    private static final double GOLDEN_RATIO = 1.68230;

    public static int getDisplayWidth(Activity context) {
        Display display = context.getWindowManager().getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        return point.x;
    }

    public static int getGoldenHeight(int width) {
        return (int) Math.ceil(width / GOLDEN_RATIO);
    }
}
