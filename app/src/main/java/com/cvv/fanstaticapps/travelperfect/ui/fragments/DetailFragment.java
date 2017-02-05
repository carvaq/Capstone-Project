package com.cvv.fanstaticapps.travelperfect.ui.fragments;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.NavUtils;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cvv.fanstaticapps.travelperfect.R;
import com.cvv.fanstaticapps.travelperfect.database.TripBuilder;
import com.cvv.fanstaticapps.travelperfect.database.TripContract;
import com.cvv.fanstaticapps.travelperfect.ui.UiUtils;
import com.cvv.fanstaticapps.travelperfect.ui.activities.DetailActivity;
import com.cvv.fanstaticapps.travelperfect.ui.helpers.BroadcastHelper;
import com.cvv.fanstaticapps.travelperfect.ui.helpers.DateDialogHelper;
import com.cvv.fanstaticapps.travelperfect.ui.helpers.ListItemHelper;
import com.cvv.fanstaticapps.travelperfect.ui.helpers.ReminderHelper;
import com.squareup.picasso.Picasso;

import org.joda.time.DateTime;

import java.io.File;

import butterknife.BindBool;
import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTextChanged;

/**
 * Created by Carla
 * Date: 08/01/2017
 * Project: Capstone-Project
 */

public class DetailFragment extends BaseFragment implements DateDialogHelper.OnDatetimeSetListener {

    public static final String ARGS_TRIP_ID = "trip_id";
    public static final String ARGS_DISCARD_EQUALS_DELETE = "delete_as_discard";

    @BindBool(R.bool.dual_pane)
    boolean mDualPane;

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
    @BindView(R.id.detail_pane)
    View mDetailPane;
    @BindView(R.id.image)
    @Nullable
    ImageView mImage;
    @BindView(R.id.toolbar)
    @Nullable
    Toolbar mToolbar;
    @BindView(R.id.inner_toolbar)
    @Nullable
    Toolbar mInnerToolbar;
    @BindView(R.id.add_reminder)
    TextView mAddReminder;
    @BindView(R.id.reminder)
    TextView mReminder;


    TripBuilder mTripBuilder = new TripBuilder();

    private DateDialogHelper mDialogHelper;
    private ListItemHelper mListItemHelper;
    private ReminderHelper mReminderHelper;

    private long mTripId;
    private boolean mDeleteAsDiscard;

    public static DetailFragment newInstance(long tripId, boolean deleteAsDiscard) {
        Bundle args = new Bundle();
        args.putLong(ARGS_TRIP_ID, tripId);
        args.putBoolean(ARGS_DISCARD_EQUALS_DELETE, deleteAsDiscard);
        DetailFragment fragment = new DetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_detail, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mDialogHelper = new DateDialogHelper(getActivity());
        mListItemHelper = new ListItemHelper(getActivity(), mItemContainer);
        mReminderHelper = new ReminderHelper(getActivity(), mAddReminder, mReminder);

        if (mToolbar != null) {
            mToolbar.setTitle(" ");
        }

        if (savedInstanceState != null) {
            readBundle(savedInstanceState);
        } else if (getArguments() != null) {
            readBundle(getArguments());
        }
    }

    private void readBundle(Bundle bundle) {
        mTripId = bundle.getLong(ARGS_TRIP_ID, -1);
        mDeleteAsDiscard = bundle.getBoolean(ARGS_DISCARD_EQUALS_DELETE, false);
        initialize(mTripId, mDeleteAsDiscard);
    }

    private void setDateTimeInView(long timestamp, TextView dateView, TextView timeView) {
        if (timestamp > 0) {
            DateTime dateTime = new DateTime(timestamp);
            timeView.setText(dateTime.toString(DetailActivity.TIME_FORMAT));
            dateView.setText(dateTime.toString(DetailActivity.DATE_FORMAT));
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
        mDialogHelper.showDatePicker(mTripBuilder.getDeparture(), mReturnDate, mReturnTime, this);
    }

    @OnClick(value = {R.id.departure_date, R.id.return_date})
    void onChangedDateClicked(View view) {
        if (view.getId() == R.id.departure_date) {
            mDialogHelper.showDatePicker(0, mDepartureDate, null, this);
        } else {
            mDialogHelper.showDatePicker(mTripBuilder.getDeparture(), mReturnDate, null, this);
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

    @OnClick(value = {R.id.reminder, R.id.add_reminder})
    void changeReminder() {
        mReminderHelper.changeReminder();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        if (mDualPane) {
            mInnerToolbar.inflateMenu(R.menu.menu_detail);
            menu = mInnerToolbar.getMenu();
            menu.findItem(R.id.action_discard).setVisible(false);
            menu.findItem(R.id.action_save).setVisible(true);
            mInnerToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {

                @Override
                public boolean onMenuItemClick(MenuItem arg0) {
                    if (arg0.getItemId() == R.id.action_save) {
                        saveTrip();
                        return true;
                    } else {
                        return onOptionsItemSelected(arg0);
                    }
                }
            });
        } else {
            inflater.inflate(R.menu.menu_detail, menu);
        }
        if (mDeleteAsDiscard) {
            menu.findItem(R.id.action_delete).setVisible(false);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_delete ||
                (item.getItemId() == R.id.action_discard && mDeleteAsDiscard)) {
            Intent intent = new Intent();
            intent.putExtra(ARGS_TRIP_ID, mTripId);
            getActivity().setResult(DetailActivity.RESULT_DELETE, intent);
            getActivity().finish();
            return true;
        } else if (item.getItemId() == R.id.action_discard) {
            NavUtils.navigateUpFromSameTask(getActivity());
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(ARGS_TRIP_ID, mTripId);
        outState.putBoolean(ARGS_DISCARD_EQUALS_DELETE, mDeleteAsDiscard);
    }


    public void saveTrip() {
        int currentProgress = mListItemHelper.saveListItems(mTripId);
        mTripBuilder.setProgress(currentProgress);

        String where = TripContract.TripEntry._ID + "=?";
        String[] selectionArgs = new String[]{String.valueOf(mTripId)};
        getContentResolver()
                .update(TripContract.TripEntry.CONTENT_URI,
                        mTripBuilder.getTripContentValues(), where, selectionArgs);
        BroadcastHelper.broadcastAction(getActivity(), MainFragment.ACTION_TRIP_UPDATED);
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

    public void initialize(long tripId, boolean deleteAsDiscard) {
        mTripId = tripId;
        mDeleteAsDiscard = deleteAsDiscard;

        Cursor cursor = getContentResolver().query(TripContract.TripEntry.buildTripUri(mTripId), null, null, null, null);
        mTripBuilder = new TripBuilder(cursor);
        mListItemHelper.addListItems(mTripId);
        mEditText.setText(mTripBuilder.getTitle());
        setDateTimeInView(mTripBuilder.getDeparture(), mDepartureDate, mDepartureTime);
        setDateTimeInView(mTripBuilder.getReturn(), mReturnDate, mReturnTime);
        mReminderHelper.prepareReminder(mTripId, mTripBuilder.getDeparture());
        if (mTripBuilder.getReturn() > 0) {
            mReturnAdd.setVisibility(View.GONE);
        }
        mListItemHelper.addNewListItem();

        if (!mDualPane) {
            setUpToolbar();
        }
    }

    private void setUpToolbar() {
        int width = UiUtils.getDisplayWidth(getActivity());
        int height = UiUtils.getProportionalHeight(width);
        mToolbar.getLayoutParams().height = height;
        if (!TextUtils.isEmpty(mTripBuilder.getFilePath())) {
            Picasso.with(getActivity())
                    .load(new File(mTripBuilder.getFilePath()))
                    .centerCrop()
                    .resize(width, height)
                    .into(mImage);
        }

        if (getView().findViewById(R.id.fragment_detail_with_card_view) != null) {
            CoordinatorLayout.LayoutParams params =
                    (CoordinatorLayout.LayoutParams) mDetailPane.getLayoutParams();
            AppBarLayout.ScrollingViewBehavior behavior =
                    (AppBarLayout.ScrollingViewBehavior) params.getBehavior();
            behavior.setOverlayTop(height / 2);
        }

    }
}
