package com.cvv.fanstaticapps.travelperfect.view;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cvv.fanstaticapps.travelperfect.R;
import com.cvv.fanstaticapps.travelperfect.model.TripContract;
import com.squareup.picasso.Picasso;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Carla
 * Date: 31/12/2016
 * Project: Capstone-Project
 */

public class TripAdapter extends RecyclerView.Adapter<TripAdapter.ViewHolder> {

    private static final int IDX_COL_NAME_OF_PLACE = 0;
    private static final int IDX_COL_DEPARTURE = 1;
    private static final int IDX_COL_RETURN = 2;
    private static final int IDX_COL_IMAGE_URL = 3;
    private static final int IDX_COL_ATTRIBUTIONS = 4;
    private static final int IDX_COL_PROGRESS = 5;
    private static final int IDX_COL_ID = 6;

    public static final String[] TRIP_PROJECTION = new String[]{
            TripContract.TripEntry.COLUMN_NAME_OF_PLACE,
            TripContract.TripEntry.COLUMN_DEPARTURE,
            TripContract.TripEntry.COLUMN_RETURN,
            TripContract.TripEntry.COLUMN_IMAGE_URL,
            TripContract.TripEntry.COLUMN_ATTRIBUTIONS,
            TripContract.TripEntry.COLUMN_PROGRESS,
            TripContract.TripEntry._ID
    };

    private Cursor mCursor;
    private TripViewListener mTripViewListener;
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private int mWidth;
    private int mHeight;

    public TripAdapter(Context context, TripViewListener tripViewListener) {
        mTripViewListener = tripViewListener;
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        mWidth = UiUtils.getDisplayWidth((Activity) context);
        mHeight = UiUtils.getProportionalHeight(mWidth);
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.image)
        ImageView mImage;
        @BindView(R.id.title)
        TextView mTitle;
        @BindView(R.id.dates)
        TextView mDates;
        @BindView(R.id.attributions)
        TextView mAttributions;
        @BindView(R.id.progress_bar)
        ProgressBar mProgressBar;
        @BindView(R.id.clickable_content)
        View mClickableGame;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mClickableGame.setOnClickListener(this);

            mImage.getLayoutParams().height = mHeight;
            mImage.getLayoutParams().width = mWidth;
        }

        @Override
        public void onClick(View v) {
            long itemId = TripAdapter.this.getItemId(getAdapterPosition());
            mTripViewListener.onDetailOpenClicked(itemId);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(mLayoutInflater.inflate(R.layout.item_trip, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        mCursor.moveToPosition(position);

        String title = mCursor.getString(IDX_COL_NAME_OF_PLACE);
        long departureDate = mCursor.getLong(IDX_COL_DEPARTURE);
        long returnDate = mCursor.getLong(IDX_COL_RETURN);
        String imageUrl = mCursor.getString(IDX_COL_IMAGE_URL);
        String attributions = mCursor.getString(IDX_COL_ATTRIBUTIONS);
        int progress = mCursor.getInt(IDX_COL_PROGRESS);

        applyDate(holder, departureDate, returnDate);
        applyAttributions(holder, attributions);

        holder.mTitle.setText(title);
        holder.mProgressBar.setProgress(progress);
        applyImage(holder, imageUrl);
    }

    private void applyImage(ViewHolder holder, String imageUrl) {
        if (!TextUtils.isEmpty(imageUrl)) {
            holder.mImage.setVisibility(View.VISIBLE);
            Picasso.with(mContext)
                    .load(new File(imageUrl))
                    .centerCrop()
                    .resize(mWidth, mHeight)
                    .placeholder(R.drawable.ic_image)
                    .error(R.drawable.ic_image)
                    .into(holder.mImage);
        } else {
            Picasso.with(mContext)
                    .cancelRequest(holder.mImage);
            holder.mImage.setVisibility(View.GONE);
        }
    }

    private void applyDate(ViewHolder holder, long departureDate, long returnDate) {
        if (returnDate != 0) {
            holder.mDates.setText(
                    DateUtils.formatDateRange(mContext, departureDate, returnDate, DateUtils.FORMAT_SHOW_DATE));
        } else {
            holder.mDates.setText(
                    DateUtils.formatDateTime(mContext, departureDate, DateUtils.FORMAT_SHOW_DATE));
        }
    }

    @SuppressWarnings("deprecation")
    private void applyAttributions(ViewHolder holder, String attributions) {
        if (attributions != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            holder.mAttributions.setText(Html.fromHtml(attributions, Html.FROM_HTML_MODE_COMPACT));
            holder.mAttributions.setVisibility(View.VISIBLE);
        } else if (attributions != null) {
            holder.mAttributions.setText(Html.fromHtml(attributions));
            holder.mAttributions.setVisibility(View.VISIBLE);
        } else {
            holder.mAttributions.setVisibility(View.GONE);
            holder.mAttributions.setText(null);
        }
    }

    @Override
    public int getItemCount() {
        if (null == mCursor) return 0;
        return mCursor.getCount();
    }

    @Override
    public long getItemId(int position) {
        mCursor.moveToPosition(position);
        return mCursor.getLong(IDX_COL_ID);
    }

    public void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
    }

    public interface TripViewListener {
        void onDetailOpenClicked(Long id);
    }
}
