package com.cvv.fanstaticapps.travelperfect.view.activities;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cvv.fanstaticapps.travelperfect.R;
import com.cvv.fanstaticapps.travelperfect.model.TripBuilder;
import com.cvv.fanstaticapps.travelperfect.model.TripContract;
import com.cvv.fanstaticapps.travelperfect.view.DateDialogHelper;
import com.cvv.fanstaticapps.travelperfect.view.ListItemHelper;
import com.cvv.fanstaticapps.travelperfect.view.UiUtils;
import com.squareup.picasso.Picasso;

import org.joda.time.DateTime;

import java.io.File;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTextChanged;

public class DetailActivity extends BaseActivity implements DateDialogHelper.OnDatetimeSetListener {

    public static final String EXTRA_TRIP_ID = "trip_id";
    public static final String DATE_FORMAT = "dd. MMM yyyy";
    public static final String TIME_FORMAT = "HH:mm";

    @BindView(R.id.plain_name_of_place)
    EditText mEditText;
    @BindView(R.id.departure_date)
    TextView mDepartureDate;
    @BindView(R.id.departure_time)
    TextView mDepartureTime;
    @BindView(R.id.return_date)
    TextView mReturnDate;
    @BindView(R.id.return_time)
    TextView mReturnTime;
    @BindView(R.id.return_add)
    TextView mReturnAdd;
    @BindView(R.id.item_container)
    LinearLayout mItemContainer;
    @BindView(R.id.image)
    ImageView mImage;

    TripBuilder mTripBuilder = new TripBuilder();

    private DateDialogHelper mDialogHelper;
    private ListItemHelper mListItemHelper;
    private long mTripId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        mDialogHelper = new DateDialogHelper(this);

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
        Cursor cursor = getContentResolver().query(TripContract.TripEntry.buildTripUri(mTripId), null, null, null, null);
        mTripBuilder = new TripBuilder(cursor);
        mListItemHelper.addListItems(mTripId);
        mEditText.setText(mTripBuilder.getTitle());
        setDateTimeInView(mTripBuilder.getDeparture(), mDepartureDate, mDepartureTime);
        setDateTimeInView(mTripBuilder.getReturn(), mReturnDate, mReturnTime);
        if (mTripBuilder.getReturn() > 0) {
            mReturnAdd.setVisibility(View.GONE);
        }
        mListItemHelper.addNewListItem();
        mImage.getLayoutParams().height = UiUtils.getProportionalHeight(UiUtils.getDisplayWidth(this));
        if (!TextUtils.isEmpty(mTripBuilder.getFilePath())) {
            Picasso.with(this)
                    .load(new File(mTripBuilder.getFilePath()))
                    .into(mImage);
        }
    }

    private void setDateTimeInView(long timestamp, TextView dateView, TextView timeView) {
        if (timestamp > 0) {
            DateTime dateTime = new DateTime(timestamp);
            timeView.setText(dateTime.toString(TIME_FORMAT));
            dateView.setText(dateTime.toString(DATE_FORMAT));
        } else {
            dateView.setVisibility(View.GONE);
            timeView.setVisibility(View.GONE);
        }
    }

    @OnTextChanged(value = R.id.plain_name_of_place, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    void saveUserInput() {
        if (!TextUtils.isEmpty(mEditText.getText())) {
            mTripBuilder.setTitle(mEditText.getText().toString());
        }
    }

    @OnClick(value = {R.id.return_add})
    void onAddDateClicked() {
        mDialogHelper.showDatePicker(mReturnDate, mReturnTime, this);
    }

    @OnClick(value = {R.id.departure_date, R.id.return_date})
    void onChangedDateClicked(View view) {
        if (view.getId() == R.id.departure_date) {
            mDialogHelper.showDatePicker(mDepartureDate, null, this);
        } else {
            mDialogHelper.showDatePicker(mReturnDate, null, this);
        }
    }

    @OnClick(value = {R.id.departure_time, R.id.return_time})
    void onChangeTimeClicked(View view) {
        if (view.getId() == R.id.departure_time) {
            mDialogHelper.showTimePicker(mDepartureTime, mTripBuilder.getDeparture(), this);
        } else {
            mDialogHelper.showTimePicker(mReturnTime, mTripBuilder.getReturn(), this);
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
        int currentProgress = mListItemHelper.saveListItems(mTripId);
        mTripBuilder.setProgress(currentProgress);

        String where = TripContract.TripEntry._ID + "=?";
        String[] selectionArgs = new String[]{String.valueOf(mTripId)};
        getContentResolver().update(TripContract.TripEntry.CONTENT_URI,
                mTripBuilder.getTripContentValues(), where, selectionArgs);

        finish();
    }

    @Override
    public void onTimestampDefined(long timestamp, boolean departure) {
        if (departure) {
            mTripBuilder.setDeparture(timestamp);
        } else {
            mTripBuilder.setReturn(timestamp);
            mReturnAdd.setVisibility(View.GONE);
        }
    }
}
