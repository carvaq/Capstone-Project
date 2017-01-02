package com.cvv.fanstaticapps.travelperfect.view;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cvv.fanstaticapps.travelperfect.R;
import com.cvv.fanstaticapps.travelperfect.model.Item;
import com.cvv.fanstaticapps.travelperfect.model.TripContract;

import java.util.ArrayList;
import java.util.List;

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

    public void saveListItems(long tripId) {
        List<ContentValues> contentValues = new ArrayList<>();
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
                Item item = new Item(number, name.getText().toString(), checkBox.isChecked(), tripId);
                contentValues.add(item.getContentValues());
            }
        }
        if (!contentValues.isEmpty()) {
            ContentValues[] contentValuesArray = new ContentValues[contentValues.size()];
            contentValues.toArray(contentValuesArray);
            mActivity.getContentResolver().bulkInsert(TripContract.ListItemEntry.CONTENT_URI, contentValuesArray);
        }
    }

    public void addNewListItem() {
        for (int i = 0; i < mItemContainer.getChildCount(); i++) {
            View child = mItemContainer.getChildAt(i);
            child.findViewById(R.id.number_of).setOnFocusChangeListener(null);
            child.findViewById(R.id.name).setOnFocusChangeListener(null);
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

        if (cursor != null && cursor.moveToFirst()) {
            do {
                Item item = new Item(cursor);
                View view = LayoutInflater.from(mActivity).inflate(R.layout.item_list_item, mItemContainer, false);
                TextView name = (TextView) view.findViewById(R.id.name);
                TextView numberOf = (TextView) view.findViewById(R.id.number_of);
                CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkbox);
                name.setText(item.getWhat());
                numberOf.setText(String.valueOf(item.getNumberOf()));
                checkBox.setChecked(item.isDone());
            } while (cursor.moveToNext());
        }
    }
}
