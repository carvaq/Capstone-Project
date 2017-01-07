package com.cvv.fanstaticapps.travelperfect.view.activities;

import android.animation.Animator;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import com.cvv.fanstaticapps.travelperfect.R;
import com.cvv.fanstaticapps.travelperfect.model.TripContract;
import com.cvv.fanstaticapps.travelperfect.view.AnimationUtils;
import com.cvv.fanstaticapps.travelperfect.view.TripAdapter;

import butterknife.BindView;
import butterknife.OnClick;

public class MainActivity extends BaseActivity implements
        LoaderManager.LoaderCallbacks<Cursor>, TripAdapter.TripViewListener {

    private static final int ID_LOADER = 123;


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
                            startActivity(new Intent(MainActivity.this, CreateWizardActivity.class));
                        }
                    });
        } else {
            startActivity(new Intent(MainActivity.this, CreateWizardActivity.class));
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
        Cursor cursor = getContentResolver().query(TripContract.ListItemEntry.CONTENT_URI, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            Intent intent = new Intent(this, DetailActivity.class);
            intent.putExtra(DetailActivity.EXTRA_TRIP_ID, id);
            startActivity(intent);
        }
    }

}
