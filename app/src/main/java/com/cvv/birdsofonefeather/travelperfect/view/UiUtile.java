package com.cvv.birdsofonefeather.travelperfect.view;

/**
 * Created by Carla
 * Date: 31/12/2016
 * Project: Capstone-Project
 */

public class UiUtils {
    private static final double GOLDEN_RATIO = 1.68230;

    public static int getGoldenHeight(int width) {
        return (int) Math.ceil(width / GOLDEN_RATIO);
    }
}
