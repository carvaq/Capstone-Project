package com.cvv.fanstaticapps.travelperfect.ui.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.cvv.fanstaticapps.travelperfect.R;

import org.joda.time.DateTime;
import org.joda.time.Duration;

/**
 * Created by Carla
 * Date: 01/02/2017
 * Project: Capstone-Project
 */

public class ReminderDialogFragment extends DialogFragment {

    private static final String ARGS_TIMESTAMP = "args_timestamp";
    private static final String ARGS_REMINDER_TIMESTAMP = "args_update";

    private long mTimestamp;
    private long mReminderTimestamp;
    private ReminderSetListener mReminderSetListener;

    public static ReminderDialogFragment newInstance(long timestamp, long reminderTimestamp) {
        ReminderDialogFragment dialogFragment = new ReminderDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putLong(ARGS_TIMESTAMP, timestamp);
        bundle.putLong(ARGS_REMINDER_TIMESTAMP, reminderTimestamp);
        dialogFragment.setArguments(bundle);
        return dialogFragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);

        mTimestamp = getArguments().getLong(ARGS_TIMESTAMP);
        mReminderTimestamp = getArguments().getLong(ARGS_REMINDER_TIMESTAMP);

        View view = View.inflate(getActivity(), R.layout.fragment_dialog_reminder, null);
        final EditText editText = (EditText) view.findViewById(R.id.reminder_number);
        final RadioGroup radioGroup = (RadioGroup) view.findViewById(R.id.time_period);

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == DialogInterface.BUTTON_POSITIVE) {
                    DateTime dateTime = getReminderTimestamp(getTimeNumber(editText),
                            radioGroup.getCheckedRadioButtonId() == R.id.hours);
                    mReminderSetListener.onReminderSet(dateTime);
                } else if (which == Dialog.BUTTON_NEUTRAL) {
                    mReminderSetListener.deleteReminder();
                }
            }
        };

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setView(view)
                .setPositiveButton(R.string.btn_save, listener)
                .setNegativeButton(android.R.string.cancel, listener);
        if (mReminderTimestamp != 0) {
            builder.setNeutralButton(R.string.btn_delete, listener);
        }
        AlertDialog dialog = builder.create();

        prepareEditText(view, editText, dialog);

        return dialog;
    }

    private void prepareEditText(View view, final EditText editText, final AlertDialog dialog) {
        final RadioButton hours = (RadioButton) view.findViewById(R.id.hours);
        final RadioButton days = (RadioButton) view.findViewById(R.id.days);

        applyPreviousValue(editText, hours, days);

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int number = getTimeNumber(editText);
                if (number > 23 && !days.isChecked()) {
                    days.setChecked(true);
                    editText.setText(String.valueOf(number / 24));
                }
                setUnit(number, hours, days);
            }

            @Override
            public void afterTextChanged(Editable s) {
                int number = getTimeNumber(editText);
                dialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(number != 0);
            }
        });
    }

    private void setUnit(int number, RadioButton hours, RadioButton days) {
        hours.setText(getActivity().getResources().getQuantityString(R.plurals.hour, number));
        days.setText(getActivity().getResources().getQuantityString(R.plurals.day, number));
    }

    private void applyPreviousValue(EditText editText, RadioButton hoursView, RadioButton daysView) {
        Duration duration;
        if (mReminderTimestamp == 0) {
            duration = Duration.ZERO;
        } else {
            duration = new Duration(mReminderTimestamp, mTimestamp);
        }
        int days = (int) duration.getStandardDays();
        if (days == 0) {
            int hours = (int) duration.getStandardHours();
            hoursView.setChecked(true);
            editText.setText(String.valueOf(hours));
            setUnit(hours, hoursView, daysView);
        } else {
            editText.setText(String.valueOf(days));
            daysView.setChecked(true);
            setUnit(days, hoursView, daysView);
        }
    }

    private DateTime getReminderTimestamp(int number, boolean isHours) {
        DateTime dateTime = new DateTime(mTimestamp);
        if (isHours) {
            return dateTime.minusHours(number);
        } else {
            return dateTime.minusDays(number);
        }
    }


    private int getTimeNumber(EditText editText) {
        if (TextUtils.isEmpty(editText.getText())) {
            return 0;
        } else {
            return Integer.parseInt(editText.getText().toString());
        }
    }

    public void setReminderSetListener(ReminderSetListener reminderSetListener) {
        mReminderSetListener = reminderSetListener;
    }

    public interface ReminderSetListener {
        void onReminderSet(DateTime dateTime);

        void deleteReminder();
    }
}
