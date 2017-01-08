package com.cvv.fanstaticapps.travelperfect.view.activities;

import android.animation.Animator;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import com.cvv.fanstaticapps.travelperfect.R;
import com.cvv.fanstaticapps.travelperfect.model.TripContract;
import com.cvv.fanstaticapps.travelperfect.view.AnimationUtils;
import com.cvv.fanstaticapps.travelperfect.view.TripAdapter;
import com.cvv.fanstaticapps.travelperfect.view.fragments.DetailFragment;

import butterknife.BindView;
import butterknife.OnClick;

public class MainActivity extends BaseActivity implements
        LoaderManager.LoaderCallbacks<Cursor>, TripAdapter.TripViewListener {

    private static final int ID_LOADER = 123;
    private static final int WAIT_PERIOD_MS = 7000;
    private static final int REQUEST_CODE = 432;

    @BindView(R.id.fab)
    FloatingActionButton mFab;
    @BindView(R.id.recycle_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.empty_screen_message)
    View mEmptyScreenMessage;

    private TripAdapter mAdapter;
    private int mLastSelectedPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onViewsInitialized() {
        mAdapter = new TripAdapter(this, this);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);

        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mAdapter);

        setUpItemTouchHelper();

        getSupportLoaderManager().initLoader(ID_LOADER, null, this);

    }

    @OnClick(R.id.fab)
    void onAddTripClicked() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            AnimationUtils.fabReveal(mFab, findViewById(R.id.reveal_effect),
                    new AnimationUtils.AnimationAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            startActivityForResult(new Intent(MainActivity.this, CreateWizardActivity.class), REQUEST_CODE);
                        }
                    });
        } else {
            startActivityForResult(new Intent(MainActivity.this, CreateWizardActivity.class), REQUEST_CODE);
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
                long id = mAdapter.getItemId(viewHolder.getAdapterPosition());
                deleteTrip(id);
            }

        };
        ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        mItemTouchHelper.attachToRecyclerView(mRecyclerView);
    }

    private void deleteTrip(final Long id) {
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
                }
            }
        };
        new AlertDialog.Builder(this, R.style.AppTheme_Dialog)
                .setMessage(R.string.dialog_delete_message)
                .setNegativeButton(R.string.btn_cancel, listener)
                .setPositiveButton(R.string.btn_delete, listener)
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == DetailActivity.RESULT_DELETE
                && data.hasExtra(DetailFragment.ARGS_TRIP_ID)) {
            deleteTrip(data.getLongExtra(DetailFragment.ARGS_TRIP_ID, -1));
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == ID_LOADER) {
            Uri queryUri = TripContract.TripEntry.CONTENT_URI;
            String sortOrder = TripContract.TripEntry.COLUMN_DEPARTURE + " ASC";

            return new CursorLoader(this,
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
        mRecyclerView.smoothScrollToPosition(mLastSelectedPosition);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    @Override
    public void onDetailOpenClicked(Long id) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(DetailFragment.ARGS_TRIP_ID, id);
        startActivityForResult(intent, REQUEST_CODE);
    }
}
