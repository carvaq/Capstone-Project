package com.cvv.fanstaticapps.travelperfect.view;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import com.cvv.fanstaticapps.travelperfect.R;
import com.cvv.fanstaticapps.travelperfect.view.activities.EditorActivity;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Created by Carla
 * Date: 01/01/2017
 * Project: Capstone-Project
 */

public class DateDialogHelper {
    private Activity mActivity;

    public DateDialogHelper(Activity activity) {
        mActivity = activity;
    }

    public void showDatePicker(final View addButton, final TextView dateView, final TextView timeView, final OnDatetimeSetListener datetimeSetListener) {
        final DateTimeFormatter formatter = DateTimeFormat.forPattern(EditorActivity.DATE_FORMAT);
        DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                DateTime date = new DateTime(year, month, dayOfMonth, 0, 0);
                dateView.setText(date.toString(formatter));
                addButton.setVisibility(View.GONE);
                dateView.setVisibility(View.VISIBLE);
                if (timeView != null) {
                    showTimePicker(timeView, date.getMillis(), datetimeSetListener);
                    timeView.setVisibility(View.VISIBLE);
                } else {
                    datetimeSetListener.onTimestampDefined(date.getMillis(), dateView.getId() == R.id.departure_date);
                }
            }
        };
        DateTime dateTime = getDateTime(dateView, formatter);

        DatePickerDialog datePickerDialog = new DatePickerDialog(mActivity, listener,
                dateTime.getYear(), dateTime.getMonthOfYear(), dateTime.getDayOfMonth());
        datePickerDialog.show();
    }

    public void showTimePicker(final TextView timeView, final long timestamp, final OnDatetimeSetListener datetimeSetListener) {
        final DateTimeFormatter formatter = DateTimeFormat.forPattern(EditorActivity.TIME_FORMAT);
        TimePickerDialog.OnTimeSetListener listener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                DateTime time = new DateTime(timestamp)
                        .withHourOfDay(hourOfDay)
                        .withMinuteOfHour(minute);
                timeView.setText(time.toString(formatter));
                datetimeSetListener.onTimestampDefined(time.getMillis(), timeView.getId() == R.id.departure_time);
            }
        };
        DateTime dateTime = getDateTime(timeView, formatter);
        TimePickerDialog timePickerDialog = new TimePickerDialog(mActivity, listener,
                dateTime.getHourOfDay(), dateTime.getMinuteOfDay(), DateFormat.is24HourFormat(mActivity));
        timePickerDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                //Covers the case when you create a trip and clicked add departure/return
                //The view will have no text, but the user canceled.
                if (TextUtils.isEmpty(timeView.getText())) {
                    DateTime time = DateTime.now();
                    timeView.setText(time.toString(formatter));
                }
            }
        });
        timePickerDialog.show();
    }

    private DateTime getDateTime(TextView view, DateTimeFormatter formatter) {
        DateTime dateTime;
        if (!TextUtils.isEmpty(view.getText())) {
            dateTime = formatter.parseDateTime(view.getText().toString());
        } else {
            dateTime = DateTime.now();
        }
        return dateTime;
    }


    public interface OnDatetimeSetListener {
        void onTimestampDefined(long timestamp, boolean departure);
    }

}
