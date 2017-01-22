package com.cvv.fanstaticapps.travelperfect.ui;

import android.content.Context;
import android.content.Intent;

/**
 * Created by Carla
 * Date: 22/01/2017
 * Project: Capstone-Project
 */

public class BroadcastHelper {

    public static void broadcastAction(Context context, String action) {
        Intent intent = new Intent(action);
        intent.setPackage(context.getPackageName());
        context.sendBroadcast(intent);
    }
}
