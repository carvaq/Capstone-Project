package com.cvv.fanstaticapps.travelperfect.ui.helpers;

import android.content.Context;
import android.content.Intent;

import com.cvv.fanstaticapps.travelperfect.R;
import com.cvv.fanstaticapps.travelperfect.ui.activities.DetailActivity;
import com.cvv.fanstaticapps.travelperfect.ui.activities.MainActivity;
import com.cvv.fanstaticapps.travelperfect.ui.fragments.DetailFragment;

/**
 * Created by Carla
 * Date: 05/02/2017
 * Project: Capstone-Project
 */

public class IntentUtil {

    public static Intent getDetailIntent(Context context, long tripId) {
        boolean dualPane = context.getResources().getBoolean(R.bool.dual_pane);
        Intent intent;
        if (dualPane) {
            intent = new Intent(context, MainActivity.class);
        } else {
            intent = new Intent(context, DetailActivity.class);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
        intent.putExtra(DetailFragment.ARGS_TRIP_ID, tripId);
        return intent;
    }

}
