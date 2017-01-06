package com.cvv.fanstaticapps.travelperfect.view;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Paint;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cvv.fanstaticapps.travelperfect.R;
import com.cvv.fanstaticapps.travelperfect.model.Item;
import com.cvv.fanstaticapps.travelperfect.model.TripContract;

/**
 * Created by Carla
 * Date: 02/01/2017
 * Project: Capstone-Project
 */

public class ListItemHelper {
    private Activity mActivity;
    private final LinearLayout mItemContainer;


    private View.OnFocusChangeListener mOnFocusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus) {
                addNewListItem();
            }
        }
    };

    public ListItemHelper(Activity activity, LinearLayout itemContainer) {
        mActivity = activity;
        mItemContainer = itemContainer;
    }

    public int saveListItems(long tripId) {
        double itemsNotDone = 0;
        int totalItems = 0;
        for (int i = 0; i < mItemContainer.getChildCount(); i++) {
            View child = mItemContainer.getChildAt(i);
            TextView name = (TextView) child.findViewById(R.id.name);
            TextView numberOf = (TextView) child.findViewById(R.id.number_of);
            CheckBox checkBox = (CheckBox) child.findViewById(R.id.checkbox);
            int number = 1;
            if (!TextUtils.isEmpty(numberOf.getText())) {
                number = Integer.parseInt(numberOf.getText().toString());
            }
            if (!TextUtils.isEmpty(name.getText())) {
                totalItems++;
                if (!checkBox.isChecked()) {
                    itemsNotDone++;
                }
                Item item = new Item(number, name.getText().toString(), checkBox.isChecked(), tripId);
                if (child.getTag(R.id.list_item_db_id) != null) {
                    String where = TripContract.ListItemEntry.COLUMN_TRIP_FK + "=?";
                    String[] selectionArgs = new String[]{String.valueOf(child.getTag(R.id.list_item_db_id))};
                    mActivity.getContentResolver().update(TripContract.ListItemEntry.CONTENT_URI,
                            item.getContentValues(), where, selectionArgs);
                } else {
                    mActivity.getContentResolver().insert(TripContract.ListItemEntry.CONTENT_URI,
                            item.getContentValues());
                }
            }
        }
        if (totalItems == 0) {
            return 100;
        } else {
            return (int) ((itemsNotDone / totalItems) * 100);
        }
    }

    public void addNewListItem() {
        for (int i = 0; i < mItemContainer.getChildCount(); i++) {
            View child = mItemContainer.getChildAt(i);
            TextView name = (TextView) child.findViewById(R.id.name);
            TextView numberOf = (TextView) child.findViewById(R.id.number_of);
            CheckBox checkBox = (CheckBox) child.findViewById(R.id.checkbox);
            checkBox.setOnCheckedChangeListener(getOnCheckedChangeListener(name, numberOf));
            numberOf.setOnFocusChangeListener(null);
            name.setOnFocusChangeListener(null);
        }
        View view = LayoutInflater.from(mActivity).inflate(R.layout.item_list_item, mItemContainer, false);
        view.findViewById(R.id.number_of).setOnFocusChangeListener(mOnFocusChangeListener);
        view.findViewById(R.id.name).setOnFocusChangeListener(mOnFocusChangeListener);
        mItemContainer.addView(view, mItemContainer.getChildCount());
    }

    public void addListItems(long tripId) {
        String where = TripContract.ListItemEntry.COLUMN_TRIP_FK + "=?";
        Cursor cursor = mActivity.getContentResolver().query(TripContract.ListItemEntry.CONTENT_URI, null,
                where, new String[]{String.valueOf(tripId)}, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                Item item = new Item(cursor);
                View view = LayoutInflater.from(mActivity).inflate(R.layout.item_list_item, mItemContainer, false);
                TextView name = (TextView) view.findViewById(R.id.name);
                TextView numberOf = (TextView) view.findViewById(R.id.number_of);
                CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkbox);
                checkBox.setOnCheckedChangeListener(getOnCheckedChangeListener(name, numberOf));
                name.setText(item.getWhat());
                numberOf.setText(String.valueOf(item.getNumberOf()));
                checkBox.setChecked(item.isDone());
                view.setTag(R.id.list_item_db_id, item.getId());
                mItemContainer.addView(view);
            }
        }
    }

    private CompoundButton.OnCheckedChangeListener getOnCheckedChangeListener(final TextView name, final TextView numberOf) {
        return new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                name.setEnabled(!isChecked);
                numberOf.setEnabled(!isChecked);
                if (isChecked) {
                    name.setPaintFlags(name.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    numberOf.setPaintFlags(name.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                } else {
                    name.setPaintFlags(name.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
                    numberOf.setPaintFlags(name.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
                }
            }
        };
    }


}
