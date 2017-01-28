package com.cvv.fanstaticapps.travelperfect.ui.fragments;

import android.animation.Animator;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cvv.fanstaticapps.travelperfect.R;
import com.cvv.fanstaticapps.travelperfect.database.TripContract;
import com.cvv.fanstaticapps.travelperfect.ui.AnimationUtils;
import com.cvv.fanstaticapps.travelperfect.ui.BroadcastHelper;
import com.cvv.fanstaticapps.travelperfect.ui.TripAdapter;
import com.cvv.fanstaticapps.travelperfect.ui.activities.CreateWizardActivity;
import com.cvv.fanstaticapps.travelperfect.ui.activities.DetailActivity;
import com.cvv.fanstaticapps.travelperfect.ui.activities.MainActivity;

import butterknife.BindBool;
import butterknife.BindInt;
import butterknife.BindView;
import butterknife.OnClick;

public class MainFragment extends BaseFragment implements
        LoaderManager.LoaderCallbacks<Cursor>, TripAdapter.TripViewListener {

    public static final String ACTION_TRIP_UPDATED = "com.cvv.fanstaticapps.travelperfect.ui.fragments.ACTION_TRIP_UPDATED";
    public static final int REQUEST_CODE = 432;

    private static final int ID_LOADER = 123;
    private static final String LAST_SELECTED_POSITION = "lastSelectedPos";

    @BindBool(R.bool.dual_pane)
    boolean mDualPane;
    @BindInt(R.integer.column_size)
    int mColumnSize;

    @BindView(R.id.fab)
    FloatingActionButton mFab;
    @BindView(R.id.recycle_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.empty_screen_message)
    View mEmptyScreenMessage;
    @BindView(R.id.reveal_effect)
    View mRevealEffect;

    private Handler mHandler = new Handler();

    private TripAdapter mAdapter;
    private int mLastSelectedPosition;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (savedInstanceState != null) {
            mLastSelectedPosition = savedInstanceState.getInt(LAST_SELECTED_POSITION);
        }
        mAdapter = new TripAdapter(getActivity(), this, mColumnSize, mDualPane);

        RecyclerView.LayoutManager layoutManager = new StaggeredGridLayoutManager(mColumnSize, StaggeredGridLayoutManager.VERTICAL);

        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mAdapter);

        setUpItemTouchHelper();

        getActivity().getSupportLoaderManager().initLoader(ID_LOADER, null, this);

    }

    @OnClick(R.id.fab)
    void onAddTripClicked() {
        //We don't want the reveal effect in dual pane. Is a bit overwhelming getting hit with that much green...
        if (!mDualPane && android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            AnimationUtils.fabReveal(mFab, mRevealEffect,
                    new AnimationUtils.AnimationAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            startActivityForResult(new Intent(getActivity(), CreateWizardActivity.class), REQUEST_CODE);
                        }
                    });
        } else {
            startActivityForResult(new Intent(getActivity(), CreateWizardActivity.class), REQUEST_CODE);
        }
    }

    private void setUpItemTouchHelper() {
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                int position = viewHolder.getAdapterPosition();
                long id = mAdapter.getItemId(position);
                deleteTrip(id, position);
            }

        };
        ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        mItemTouchHelper.attachToRecyclerView(mRecyclerView);
    }

    private void deleteTrip(final Long id, final int position) {
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == DialogInterface.BUTTON_POSITIVE) {
                    String where = TripContract.TripEntry._ID + "=?";
                    String[] selectionArgs = new String[]{String.valueOf(id)};
                    getContentResolver().delete(TripContract.TripEntry.CONTENT_URI, where, selectionArgs);
                    where = TripContract.ListItemEntry.COLUMN_TRIP_FK + "=?";
                    getContentResolver().delete(TripContract.ListItemEntry.CONTENT_URI, where, selectionArgs);
                    where = TripContract.ReminderEntry.COLUMN_TRIP_FK + "=?";
                    getContentResolver().delete(TripContract.ReminderEntry.CONTENT_URI, where, selectionArgs);
                    BroadcastHelper.broadcastAction(getActivity(), ACTION_TRIP_UPDATED);
                } else {
                    if (position != -1) {
                        mAdapter.notifyItemChanged(position);
                    } else {
                        mAdapter.notifyDataSetChanged();
                    }
                }
            }
        };
        new AlertDialog.Builder(getActivity(), R.style.AppTheme_Dialog)
                .setMessage(R.string.dialog_delete_message)
                .setNegativeButton(R.string.btn_cancel, listener)
                .setPositiveButton(R.string.btn_delete, listener)
                .show();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(LAST_SELECTED_POSITION, mLastSelectedPosition);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == DetailActivity.RESULT_DELETE
                && data.hasExtra(DetailFragment.ARGS_TRIP_ID)) {
            deleteTrip(data.getLongExtra(DetailFragment.ARGS_TRIP_ID, -1), -1);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == ID_LOADER) {
            Uri queryUri = TripContract.TripEntry.CONTENT_URI;
            String sortOrder = TripContract.TripEntry.COLUMN_DEPARTURE + " ASC";

            return new CursorLoader(getActivity(),
                    queryUri,
                    TripAdapter.TRIP_PROJECTION,
                    null,
                    null,
                    sortOrder);
        } else {
            throw new RuntimeException("Loader Not Implemented: " + id);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data.getCount() > 0) {
            mEmptyScreenMessage.setVisibility(View.GONE);
        } else {
            mEmptyScreenMessage.setVisibility(View.VISIBLE);
        }
        mAdapter.swapCursor(data);
        if (mLastSelectedPosition == RecyclerView.NO_POSITION) {
            mLastSelectedPosition = 0;
        }

        if (mDualPane) {
            openDetailFragment(data);
        }
        mRecyclerView.smoothScrollToPosition(mLastSelectedPosition);
    }

    private void openDetailFragment(final Cursor cursor) {
        if (cursor != null && cursor.moveToPosition(mLastSelectedPosition)) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    long id = cursor.getLong(TripAdapter.IDX_COL_ID);
                    ((MainActivity) getActivity()).onItemSelected(id, null, null);
                }
            });
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    @Override
    public void onDetailOpenClicked(Long id, int position, ImageView tripImage, TextView tripTitle) {
        ((MainActivity) getActivity()).onItemSelected(id, tripImage, tripTitle);
        mLastSelectedPosition = position;
    }
}
