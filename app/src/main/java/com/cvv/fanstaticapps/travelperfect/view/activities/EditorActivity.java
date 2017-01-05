package com.cvv.fanstaticapps.travelperfect.view.activities;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cvv.fanstaticapps.travelperfect.R;
import com.cvv.fanstaticapps.travelperfect.model.TripBuilder;
import com.cvv.fanstaticapps.travelperfect.model.TripContract;
import com.cvv.fanstaticapps.travelperfect.view.EditDialogHelper;
import com.cvv.fanstaticapps.travelperfect.view.ListItemHelper;

import org.joda.time.DateTime;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTextChanged;

public abstract class EditorActivity extends BaseActivity {

    public static final String EXTRA_TRIP_ID = "trip_id";
    public static final String DATE_FORMAT = "dd. MMM yyyy";
    public static final String TIME_FORMAT = "HH:mm";

    @BindView(R.id.plain_name_of_place)
    EditText mEditText;
    @BindView(R.id.departure_date)
    TextView mDepartureDate;
    @BindView(R.id.departure_time)
    TextView mDepartureTime;
    @BindView(R.id.departure_add)
    TextView mDepartureAdd;
    @BindView(R.id.return_date)
    TextView mReturnDate;
    @BindView(R.id.return_time)
    TextView mReturnTime;
    @BindView(R.id.return_add)
    TextView mReturnAdd;
    @BindView(R.id.item_container)
    LinearLayout mItemContainer;
    @BindView(R.id.error_name)
    TextView mErrorName;
    @BindView(R.id.error_departure)
    TextView mErrorDeparture;
    @BindView(R.id.place_autocomplete_container)
    View mAutoCompleteContainer;

    TripBuilder mTripBuilder = new TripBuilder();

    private EditDialogHelper mDialogHelper;
    private ListItemHelper mListItemHelper;
    private long mTripId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        mDialogHelper = new EditDialogHelper(this);

        if (savedInstanceState != null) {
            mTripId = savedInstanceState.getLong(EXTRA_TRIP_ID, -1);
        } else {
            mTripId = getIntent().getLongExtra(EXTRA_TRIP_ID, -1);
        }
    }

    @Override
    protected void onViewsInitialized() {
        mListItemHelper = new ListItemHelper(this, mItemContainer);
        enableBackNavigation();
        if (mTripId != -1) {
            Cursor cursor = getContentResolver().query(TripContract.TripEntry.buildTripUri(mTripId), null, null, null, null);
            mTripBuilder = new TripBuilder(cursor);
            mListItemHelper.addListItems(mTripId);
            mEditText.setText(mTripBuilder.getTitle());
        }
        setDateTimeInView(mTripBuilder.getDeparture(), mDepartureDate, mDepartureTime, mDepartureAdd);
        setDateTimeInView(mTripBuilder.getReturn(), mReturnDate, mReturnTime, mReturnAdd);
        mListItemHelper.addNewListItem();
    }

    private void setDateTimeInView(long timestamp, TextView dateView, TextView timeView, TextView addView) {
        if (timestamp > 0) {
            DateTime dateTime = new DateTime(timestamp);
            timeView.setText(dateTime.toString(TIME_FORMAT));
            dateView.setText(dateTime.toString(DATE_FORMAT));
            addView.setVisibility(View.GONE);
        } else {
            dateView.setVisibility(View.GONE);
            timeView.setVisibility(View.GONE);
        }
    }

    @OnTextChanged(value = R.id.plain_name_of_place, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    void saveUserInput() {
        mErrorName.setVisibility(View.GONE);
        mTripBuilder.setTitle(mEditText.getText().toString());
    }

    @OnClick(value = {R.id.departure_add, R.id.return_add})
    void onAddDateClicked(View view) {
        if (view.getId() == R.id.departure_add) {
            mErrorDeparture.setVisibility(View.GONE);
            mDialogHelper.showDatePicker(mTripBuilder, mDepartureAdd, mDepartureDate, mDepartureTime);
        } else {
            mDialogHelper.showDatePicker(mTripBuilder, mReturnAdd, mReturnDate, mReturnTime);
        }
    }

    @OnClick(value = {R.id.departure_date, R.id.return_date})
    void onChangedDateClicked(View view) {
        if (view.getId() == R.id.departure_date) {
            mDialogHelper.showDatePicker(mTripBuilder, mDepartureAdd, mDepartureDate, null);
        } else {
            mDialogHelper.showDatePicker(mTripBuilder, mReturnAdd, mReturnDate, null);
        }
    }

    @OnClick(value = {R.id.departure_time, R.id.return_time})
    void onChangeTimeClicked(View view) {
        if (view.getId() == R.id.departure_time) {
            mDialogHelper.showTimePicker(mTripBuilder, mDepartureTime);
        } else {
            mDialogHelper.showTimePicker(mTripBuilder, mReturnTime);
        }
    }

    @Override
    public void onBackPressed() {
        saveTrip();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            saveTrip();
            return true;
        } else if (item.getItemId() == R.id.action_discard) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(EXTRA_TRIP_ID, mTripId);
    }

    public void saveTrip() {
        if (TextUtils.isEmpty(mTripBuilder.getTitle()) && mTripBuilder.getDeparture() == 0) {
            finish();
        } else if (TextUtils.isEmpty(mTripBuilder.getTitle())) {
            mErrorName.setVisibility(View.VISIBLE);
        } else if (mTripBuilder.getDeparture() == 0) {
            mErrorDeparture.setVisibility(View.VISIBLE);
        } else {
            String where = TripContract.TripEntry._ID + "=?";
            String[] selectionArgs = new String[]{String.valueOf(mTripId)};
            getContentResolver().update(TripContract.TripEntry.CONTENT_URI,
                    mTripBuilder.getTripContentValues(), where, selectionArgs);

            mListItemHelper.saveListItems(mTripId);
            finish();
        }
    }

}
