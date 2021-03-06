package com.cvv.fanstaticapps.travelperfect.ui.helpers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.TextView;

import com.cvv.fanstaticapps.travelperfect.R;
import com.cvv.fanstaticapps.travelperfect.database.Reminder;
import com.cvv.fanstaticapps.travelperfect.database.TripContract;
import com.cvv.fanstaticapps.travelperfect.ui.fragments.ReminderDialogFragment;
import com.cvv.fanstaticapps.travelperfect.ui.receivers.AlarmReceiver;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import timber.log.Timber;

/**
 * Created by Carla
 * Date: 01/02/2017
 * Project: Capstone-Project
 */

public class ReminderHelper {
    private static final String WHERE_TRIP_FK = TripContract.ReminderEntry.COLUMN_TRIP_FK + "=?";
    private static final String TAG_REMINDER = "TAG_REMINDER";
    private static final String WHERE_REMINDER_ID = TripContract.ReminderEntry._ID + "=?";
    private DateTimeFormatter mDateFormatter = DateTimeFormat.forPattern("dd. MM YYYY HH:mm");

    private FragmentActivity mActivity;
    private TextView mAddReminder;
    private TextView mReminderView;
    private long mArrivalTimestamp;

    private Reminder mReminder;

    public ReminderHelper(FragmentActivity activity, TextView addReminder, TextView reminder) {
        mActivity = activity;
        mAddReminder = addReminder;
        mReminderView = reminder;
    }

    public void changeReminder() {
        ReminderDialogFragment dialogFragment = ReminderDialogFragment.newInstance(mArrivalTimestamp, mReminder.getTimestamp());
        dialogFragment.setReminderSetListener(new ReminderDialogFragment.ReminderSetListener() {
            @Override
            public void onReminderSet(DateTime dateTime) {
                mReminder.setTimestamp(dateTime.getMillis());
                if (mReminder.getId() != null) {
                    String[] selectionArgs = new String[]{String.valueOf(mReminder.getId())};
                    int rowsUpdated = mActivity.getContentResolver()
                            .update(TripContract.ReminderEntry.CONTENT_URI, mReminder.getContentValues(),
                                    WHERE_REMINDER_ID, selectionArgs);
                    Timber.d("Updated %s rows", rowsUpdated);
                } else {
                    mActivity.getContentResolver()
                            .insert(TripContract.ReminderEntry.CONTENT_URI, mReminder.getContentValues());
                }
                fetchReminder(mReminder.getTripId());
                applyReminderToViews(dateTime);
                updateNotification();
            }

            @Override
            public void deleteReminder() {
                String[] selectionArgs = new String[]{String.valueOf(mReminder.getId())};
                int rowsDeleted = mActivity.getContentResolver()
                        .delete(TripContract.ReminderEntry.CONTENT_URI, WHERE_REMINDER_ID, selectionArgs);

                Timber.d("Deleted %s rows", rowsDeleted);

                deleteNotification();
                mReminder.setId(null);
                mReminder.setTimestamp(0L);
                removeReminderFromViews();
            }
        });
        dialogFragment.show(mActivity.getSupportFragmentManager(), TAG_REMINDER);
    }

    private void removeReminderFromViews() {
        mAddReminder.setVisibility(View.VISIBLE);
        mReminderView.setVisibility(View.GONE);
    }

    private void applyReminderToViews(DateTime dateTime) {
        mAddReminder.setVisibility(View.GONE);
        mReminderView.setText(mActivity.getString(R.string.reminder_text, dateTime.toString(mDateFormatter)));
        mReminderView.setVisibility(View.VISIBLE);
    }

    public void prepareReminder(long tripId, long arrivalTimestamp) {
        mArrivalTimestamp = arrivalTimestamp;
        fetchReminder(tripId);
        if (mReminder.getTimestamp() != 0) {
            applyReminderToViews(new DateTime(mReminder.getTimestamp()));
        } else {
            removeReminderFromViews();
        }

    }

    private void fetchReminder(long tripId) {
        Cursor cursor = mActivity.getContentResolver().query(TripContract.ReminderEntry.CONTENT_URI, null,
                WHERE_TRIP_FK, new String[]{String.valueOf(tripId)}, null);
        if (cursor != null && cursor.moveToFirst()) {
            mReminder = new Reminder(cursor);
            mReminder.setTripId(tripId);
            cursor.close();
        } else {
            mReminder = new Reminder(0L, tripId);
        }
    }

    private void updateNotification() {
        PendingIntent pendingIntent = getReminderPendingIntent();
        AlarmManager alarmManager = (AlarmManager) mActivity.getSystemService(Context.ALARM_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, mReminder.getTimestamp(), pendingIntent);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, mReminder.getTimestamp(), pendingIntent);
        }
    }

    private void deleteNotification() {
        AlarmManager alarmManager = (AlarmManager) mActivity.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(getReminderPendingIntent());
    }

    private PendingIntent getReminderPendingIntent() {
        int reminderUpdate = mReminder.getId().intValue();
        Intent alarmIntent = new Intent(mActivity, AlarmReceiver.class);
        alarmIntent.putExtra(AlarmReceiver.EXTRA_REMINDER_ID, mReminder.getId());
        alarmIntent.putExtra(AlarmReceiver.EXTRA_TRIP_ID, mReminder.getTripId());
        return PendingIntent.getBroadcast(mActivity, reminderUpdate, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
