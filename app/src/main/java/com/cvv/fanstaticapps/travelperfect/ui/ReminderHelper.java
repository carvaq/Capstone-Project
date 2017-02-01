package com.cvv.fanstaticapps.travelperfect.ui;

import android.database.Cursor;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.TextView;

import com.cvv.fanstaticapps.travelperfect.R;
import com.cvv.fanstaticapps.travelperfect.database.Reminder;
import com.cvv.fanstaticapps.travelperfect.database.TripContract;
import com.cvv.fanstaticapps.travelperfect.ui.fragments.ReminderDialogFragment;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Created by Carla
 * Date: 01/02/2017
 * Project: Capstone-Project
 */

public class ReminderHelper {
    private static final String WHERE_TRIP_FK = TripContract.ListItemEntry.COLUMN_TRIP_FK + "=?";
    private static final String TAG_REMINDER = "TAG_REMINDER";
    private DateTimeFormatter mDateFormatter = DateTimeFormat.shortDateTime();

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
                    mActivity.getContentResolver()
                            .update(TripContract.ReminderEntry.buildReminderUri(mReminder.getId()), mReminder.getContentValues(), null, null);
                } else {
                    mActivity.getContentResolver()
                            .insert(TripContract.ReminderEntry.CONTENT_URI, mReminder.getContentValues());
                }
                fetchReminder(mReminder.getTripId());
                mAddReminder.setVisibility(View.GONE);
                mReminderView.setText(mActivity.getString(R.string.reminder_text, dateTime.toString(mDateFormatter)));
                mReminderView.setVisibility(View.VISIBLE);
            }

            @Override
            public void deleteReminder() {
                mActivity.getContentResolver()
                        .delete(TripContract.ReminderEntry.buildReminderUri(mReminder.getId()), null, null);
                mReminder.setId(null);
                mReminder.setTimestamp(0L);
                mAddReminder.setVisibility(View.VISIBLE);
                mReminderView.setVisibility(View.GONE);
            }
        });
        dialogFragment.show(mActivity.getSupportFragmentManager(), TAG_REMINDER);
    }

    public void prepareReminder(long tripId, long arrivalTimestamp) {
        mArrivalTimestamp = arrivalTimestamp;
        fetchReminder(tripId);
    }

    private void fetchReminder(long tripId) {
        Cursor cursor = mActivity.getContentResolver().query(TripContract.ReminderEntry.CONTENT_URI, null,
                WHERE_TRIP_FK, new String[]{String.valueOf(tripId)}, null);
        if (cursor != null && cursor.moveToFirst()) {
            mReminder = new Reminder(cursor);
            cursor.close();
        } else {
            mReminder = new Reminder(0L, tripId);
        }
    }
}
