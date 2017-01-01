package com.cvv.birdsofonefeather.travelperfect.view;

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
import android.view.View;

import com.cvv.birdsofonefeather.travelperfect.R;
import com.cvv.birdsofonefeather.travelperfect.model.TripContract;

import butterknife.BindView;

public class MainActivity extends BaseActivity implements
        LoaderManager.LoaderCallbacks<Cursor>, TripAdapter.TripViewListener {

    static final int IDX_COL_NAME_OF_PLACE = 0;
    static final int IDX_COL_DEPARTURE = 1;
    static final int IDX_COL_RETURN = 2;
    static final int IDX_COL_IMAGE_URL = 3;
    static final int IDX_COL_ATTRIBUTIONS = 4;

    private static final int ID_LOADER = 123;
    private static final String[] MAIN_PROJECTION = new String[]{
            TripContract.TripEntry.COLUMN_NAME_OF_PLACE,
            TripContract.TripEntry.COLUMN_DEPARTURE,
            TripContract.TripEntry.COLUMN_RETURN,
            TripContract.TripEntry.COLUMN_IMAGE_URL,
            TripContract.TripEntry.COLUMN_ATTRIBUTIONS
    };

    @BindView(R.id.fab)
    FloatingActionButton mFab;
    @BindView(R.id.recycle_view)
    RecyclerView mRecyclerView;

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

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    AnimationUtils.fabReveal(mFab, findViewById(R.id.reveal_effect),
                            new AnimationUtils.AnimationAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    super.onAnimationEnd(animation);
                                    startActivity(new Intent(MainActivity.this, CreateTripActivity.class));
                                }
                            });
                } else {
                    startActivity(new Intent(MainActivity.this, CreateTripActivity.class));
                }
            }
        });

        getSupportLoaderManager().initLoader(ID_LOADER, null, this);

    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == ID_LOADER) {
            Uri forecastQueryUri = TripContract.TripEntry.CONTENT_URI;
            String sortOrder = TripContract.TripEntry.COLUMN_DEPARTURE + " ASC";

            return new CursorLoader(this,
                    forecastQueryUri,
                    MAIN_PROJECTION,
                    null,
                    null,
                    sortOrder);
        } else {
            throw new RuntimeException("Loader Not Implemented: " + id);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
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

    }

    @Override
    public void onEditClicked(Long id) {

    }

    @Override
    public void onDoneClicked(Long id) {

    }

}
