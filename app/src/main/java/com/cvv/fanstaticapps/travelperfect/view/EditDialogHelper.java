package com.cvv.fanstaticapps.travelperfect.view;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SwitchCompat;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import com.cvv.fanstaticapps.travelperfect.R;
import com.cvv.fanstaticapps.travelperfect.model.TripBuilder;
import com.cvv.fanstaticapps.travelperfect.view.activities.EditorActivity;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Created by Carla
 * Date: 01/01/2017
 * Project: Capstone-Project
 */

public class EditDialogHelper {
    private EditorActivity mActivity;

    public EditDialogHelper(EditorActivity activity) {
        mActivity = activity;
    }


    public void showSaveDialog(final TripBuilder tripBuilder) {
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (DialogInterface.BUTTON_POSITIVE == which) {
                    mActivity.onSaveClicked();
                } else {
                    mActivity.finish();
                }
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setMessage(R.string.dialog_save_before_exiting_text)
                .setTitle(R.string.dialog_title_attention)
                .setPositiveButton(R.string.btn_save, listener)
                .setNegativeButton(R.string.btn_discard, listener)
                .show();
    }

    public void showFeatureDisableDialog(final SwitchCompat featureToggle) {
        if (UiUtils.featureToggleDialogAlreadyShown(mActivity)) {
            return;
        }
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (DialogInterface.BUTTON_NEGATIVE == which) {
                    featureToggle.setChecked(true);
                    UiUtils.setFeatureDialogShownPref(mActivity);
                }
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setMessage(R.string.dialog_disabling_google_search_text)
                .setTitle(R.string.dialog_title_attention)
                .setPositiveButton(R.string.btn_save, listener)
                .setNegativeButton(R.string.btn_discard, listener)
                .show();
    }

    public void showDatePicker(final TripBuilder tripBuilder, final View addButton, final TextView dateView, final TextView timeView) {
        final DateTimeFormatter formatter = DateTimeFormat.forPattern(EditorActivity.DATE_FORMAT);
        DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                DateTime date = new DateTime(year, month, dayOfMonth, 0, 0);
                if (dateView.getId() == R.id.departure_date) {
                    tripBuilder.setDeparture(date.getMillis());
                } else {
                    tripBuilder.setReturn(date.getMillis());
                }
                if (tripBuilder.getReturn() > 0 && tripBuilder.getDeparture() > tripBuilder.getReturn()) {
                    tripBuilder.setDeparture(tripBuilder.getReturn());
                    date.withMillis(tripBuilder.getDeparture());
                }
                dateView.setText(date.toString(formatter));
                addButton.setVisibility(View.GONE);
                dateView.setVisibility(View.VISIBLE);
                if (timeView != null) {
                    showTimePicker(tripBuilder, timeView);
                    timeView.setVisibility(View.VISIBLE);
                }
            }
        };
        DateTime dateTime = getDateTime(dateView, formatter);

        DatePickerDialog datePickerDialog = new DatePickerDialog(mActivity, listener,
                dateTime.getYear(), dateTime.getMonthOfYear(), dateTime.getDayOfMonth());
        datePickerDialog.show();
    }

    public void showTimePicker(final TripBuilder tripBuilder, final TextView timeView) {
        final DateTimeFormatter formatter = DateTimeFormat.forPattern(EditorActivity.TIME_FORMAT);
        TimePickerDialog.OnTimeSetListener listener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                DateTime time;
                if (timeView.getId() == R.id.departure_time) {
                    time = new DateTime(tripBuilder.getDeparture())
                            .withHourOfDay(hourOfDay)
                            .withMinuteOfHour(minute);
                    tripBuilder.setDeparture(time.getMillis());
                } else {
                    time = new DateTime(tripBuilder.getReturn())
                            .withHourOfDay(hourOfDay)
                            .withMinuteOfHour(minute);
                    tripBuilder.setReturn(time.getMillis());
                }
                timeView.setText(time.toString(formatter));
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

    public interface OnSaveClickListener {
        void onSaveClicked();
    }

}
