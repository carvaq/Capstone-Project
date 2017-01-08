package com.cvv.fanstaticapps.travelperfect.view.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.cvv.fanstaticapps.travelperfect.R;
import com.cvv.fanstaticapps.travelperfect.view.SelectionListener;
import com.cvv.fanstaticapps.travelperfect.view.fragments.DetailFragment;
import com.cvv.fanstaticapps.travelperfect.view.fragments.MainFragment;

import butterknife.BindBool;

public class MainActivity extends BaseActivity implements SelectionListener {

    @BindBool(R.bool.dual_pane)
    boolean mDualPane;

    private Fragment mMainFragment;

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
            mMainFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_main_container);
        }
    }

    @Override
    public void onItemSelected(long id) {
        if (mDualPane) {
            DetailFragment detailFragment = DetailFragment.newInstance(id, false);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_detail_container, detailFragment)
                    .commit();
        } else {
            Intent intent = new Intent(this, DetailActivity.class);
            intent.putExtra(DetailFragment.ARGS_TRIP_ID, id);
            mMainFragment.startActivityForResult(intent, MainFragment.REQUEST_CODE);
        }
    }
}
