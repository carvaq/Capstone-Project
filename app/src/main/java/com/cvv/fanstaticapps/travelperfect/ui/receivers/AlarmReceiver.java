package com.cvv.fanstaticapps.travelperfect.ui.receivers;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.NotificationCompat;

import com.cvv.fanstaticapps.travelperfect.R;
import com.cvv.fanstaticapps.travelperfect.database.Item;
import com.cvv.fanstaticapps.travelperfect.database.TripBuilder;
import com.cvv.fanstaticapps.travelperfect.database.TripContract;
import com.cvv.fanstaticapps.travelperfect.ui.helpers.IntentUtil;
import com.cvv.fanstaticapps.travelperfect.ui.helpers.ListItemHelper;

public class AlarmReceiver extends BroadcastReceiver {
    public static final String EXTRA_TRIP_ID = "extra_trip_id";
    public static final String EXTRA_REMINDER_ID = "extra_reminder_id";
    private static final String ITEM_TO_DO = "\n-%s";

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        long reminderId = intent.getLongExtra(EXTRA_REMINDER_ID, -1);
        long tripId = intent.getLongExtra(EXTRA_TRIP_ID, -1);
        int tripReminderId = (int) reminderId;

        Cursor cursor = context.getContentResolver().query(TripContract.TripEntry.buildTripUri(tripId), null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            createNotification(context, notificationManager, tripId, tripReminderId, cursor);
        }
    }

    private void createNotification(Context context, NotificationManager notificationManager, long tripId, int tripReminderId, Cursor cursor) {
        TripBuilder tripBuilder = new TripBuilder(cursor);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);


        if (tripBuilder.getProgress() != 100) {
            Cursor itemsCursor = ListItemHelper.getListItemCursor(context, tripId);
            String missingStuffToDo = context.getString(R.string.reminder_no_done_yet, tripBuilder.getProgress());
            while (itemsCursor.moveToNext()) {
                Item item = new Item(itemsCursor);
                if (!item.isDone()) {
                    missingStuffToDo += String.format(ITEM_TO_DO, item.getWhat());
                }
            }
            itemsCursor.close();
            builder.setStyle(new android.support.v4.app.NotificationCompat.BigTextStyle()
                    .bigText(missingStuffToDo));
        } else {
            builder.setContentText(context.getString(R.string.reminder_you_are_all_done));
        }
        Notification notification = builder.setContentTitle(tripBuilder.getTitle())
                .setSmallIcon(R.drawable.ic_stat_name)
                .setContentIntent(PendingIntent.getActivity(context, tripReminderId,
                        IntentUtil.getDetailIntent(context, tripId), PendingIntent.FLAG_UPDATE_CURRENT))
                .build();
        notificationManager.notify(tripReminderId, notification);
    }
}