package com.cvv.fanstaticapps.travelperfect.widget;

import android.app.Activity;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.app.TaskStackBuilder;
import android.widget.RemoteViews;

import com.cvv.fanstaticapps.travelperfect.R;
import com.cvv.fanstaticapps.travelperfect.database.TripContract;
import com.cvv.fanstaticapps.travelperfect.ui.activities.DetailActivity;
import com.cvv.fanstaticapps.travelperfect.ui.activities.MainActivity;
import com.cvv.fanstaticapps.travelperfect.ui.fragments.DetailFragment;
import com.cvv.fanstaticapps.travelperfect.ui.fragments.MainFragment;

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in {@link TripWidgetConfigureActivity TripWidgetConfigureActivity}
 */
public class TripWidget extends AppWidgetProvider {


    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.trip_widget);


        long tripId = TripWidgetConfigureActivity.loadTitlePref(context, appWidgetId);
        Cursor cursor = context.getContentResolver().query(TripContract.TripEntry.buildTripUri(tripId),
                new String[]{TripContract.TripEntry.COLUMN_NAME_OF_PLACE}, null, null, null);
        if (cursor != null && cursor.moveToNext()) {
            views.setTextViewText(R.id.widget_title, cursor.getString(0));

            Class<? extends Activity> activityClass = context.getResources().getBoolean(R.bool.dual_pane) ?
                    MainActivity.class : DetailActivity.class;
            Intent intent = new Intent(context, activityClass);
            intent.putExtra(DetailFragment.ARGS_TRIP_ID, tripId);

            PendingIntent pendingIntent = TaskStackBuilder.create(context)
                    .addNextIntentWithParentStack(intent)
                    .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            views.setOnClickPendingIntent(R.id.widget_title, pendingIntent);
        }


        Intent remoteViewServiceIntent = new Intent(context, TripRemoteViewsService.class);
        remoteViewServiceIntent.putExtra(DetailFragment.ARGS_TRIP_ID, tripId);
        views.setRemoteAdapter(R.id.widget_list, remoteViewServiceIntent);

        views.setEmptyView(R.id.widget_list, R.id.trip_showing_error);

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (MainFragment.ACTION_TRIP_UPDATED.equals(intent.getAction())) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            ComponentName componentName = new ComponentName(context, getClass());
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(componentName);
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_list);
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

}

