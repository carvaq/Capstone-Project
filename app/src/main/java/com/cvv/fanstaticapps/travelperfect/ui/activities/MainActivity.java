package com.cvv.fanstaticapps.travelperfect.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.cvv.fanstaticapps.travelperfect.R;
import com.cvv.fanstaticapps.travelperfect.ui.SelectionListener;
import com.cvv.fanstaticapps.travelperfect.ui.fragments.DetailFragment;
import com.cvv.fanstaticapps.travelperfect.ui.fragments.MainFragment;

import butterknife.BindBool;

public class MainActivity extends BaseActivity implements SelectionListener {

    @BindBool(R.bool.dual_pane)
    boolean mDualPane;

    private MainFragment mMainFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (savedInstanceState == null) {
            mMainFragment = new MainFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragment_main_container, mMainFragment)
                    .commit();
        } else {
            mMainFragment = (MainFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.fragment_main_container);
        }
    }

    @Override
    public void onPostCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onPostCreate(savedInstanceState, persistentState);
        if (getIntent().hasExtra(DetailFragment.ARGS_TRIP_ID)) {
            mMainFragment.selectItemForId(getIntent().getLongExtra(DetailFragment.ARGS_TRIP_ID, -1));
        }
    }

    @Override
    public void onItemSelected(long id, ImageView transView1, TextView transView2) {
        if (mDualPane) {
            DetailFragment detailFragment = DetailFragment.newInstance(id, false);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_detail_container, detailFragment)
                    .commit();
        } else {
            Intent intent = new Intent(this, DetailActivity.class);
            intent.putExtra(DetailFragment.ARGS_TRIP_ID, id);

            if (transView1 != null && transView2 != null) {
                ActivityOptionsCompat activityOptions =
                        ActivityOptionsCompat.makeSceneTransitionAnimation(this,
                                new Pair<View, String>(transView2, getString(R.string.transition_name_trip_name)));
                ActivityCompat.startActivityForResult(this, intent, MainFragment.REQUEST_CODE, activityOptions.toBundle());
            } else {
                mMainFragment.startActivityForResult(intent, MainFragment.REQUEST_CODE);
            }
        }
    }
}
