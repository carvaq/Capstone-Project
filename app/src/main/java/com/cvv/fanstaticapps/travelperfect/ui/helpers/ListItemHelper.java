package com.cvv.fanstaticapps.travelperfect.ui.helpers;

import android.app.Activity;
import android.content.Context;
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
import com.cvv.fanstaticapps.travelperfect.database.Item;
import com.cvv.fanstaticapps.travelperfect.database.TripContract;

/**
 * Created by Carla
 * Date: 02/01/2017
 * Project: Capstone-Project
 */

public class ListItemHelper {

    private static final String WHERE_TRIP_FK = TripContract.ListItemEntry.COLUMN_TRIP_FK + "=?";
    private static final String WHERE_ITEM_ID = TripContract.ListItemEntry._ID + "=?";
    private final LinearLayout mItemContainer;
    private Activity mActivity;
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

    public static Cursor getListItemCursor(Context context, long tripId) {
        return context.getContentResolver().query(TripContract.ListItemEntry.CONTENT_URI, null,
                WHERE_TRIP_FK, new String[]{String.valueOf(tripId)}, null);
    }

    public int saveListItems(long tripId) {
        double itemsDone = 0;
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
                if (checkBox.isChecked()) {
                    itemsDone++;
                }
                saveListItem(tripId, child, name, checkBox, number);
            }
        }
        if (totalItems == 0) {
            return 100;
        } else {
            return (int) ((itemsDone / totalItems) * 100);
        }
    }

    private void saveListItem(long tripId, View child, TextView name, CheckBox checkBox, int number) {
        Item item = new Item(number, name.getText().toString(), checkBox.isChecked(), tripId);
        if (child.getTag(R.id.list_item_db_id) != null) {

            String[] selectionArgs = new String[]{String.valueOf(child.getTag(R.id.list_item_db_id))};
            mActivity.getContentResolver().update(TripContract.ListItemEntry.CONTENT_URI,
                    item.getContentValues(), WHERE_ITEM_ID, selectionArgs);
        } else {
            mActivity.getContentResolver().insert(TripContract.ListItemEntry.CONTENT_URI,
                    item.getContentValues());
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
        Cursor cursor = getListItemCursor(mActivity, tripId);

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
