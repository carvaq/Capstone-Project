package com.cvv.birdsofonefeather.travelperfect.view;

import android.content.Context;
import android.database.Cursor;
import android.provider.BaseColumns;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cvv.birdsofonefeather.travelperfect.R;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Carla
 * Date: 31/12/2016
 * Project: Capstone-Project
 */

class TripAdapter extends RecyclerView.Adapter<TripAdapter.ViewHolder> {

    private Cursor mCursor;
    private TripViewListener mTripViewListener;
    private LayoutInflater mLayoutInflater;
    private Context mContext;

    TripAdapter(Context context, TripViewListener tripViewListener) {
        mTripViewListener = tripViewListener;
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.image)
        ImageView image;
        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.dates)
        TextView dates;
        @BindView(R.id.edit)
        TextView edit;
        @BindView(R.id.done)
        TextView done;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(itemView);
            edit.setOnClickListener(this);
            done.setOnClickListener(this);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.edit) {
                mTripViewListener.onEditClicked(getItemId());
            } else if (v.getId() == R.id.done) {
                mTripViewListener.onDoneClicked(getItemId());
            } else {
                mTripViewListener.onDetailOpenClicked(getItemId());
            }
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(mLayoutInflater.inflate(R.layout.item_trip, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        mCursor.moveToPosition(position);

        String title = mCursor.getString(MainActivity.IDX_COL_NAME_OF_PLACE);
        long departureDate = mCursor.getLong(MainActivity.IDX_COL_DEPARTURE);
        long returnDate = mCursor.getLong(MainActivity.IDX_COL_RETURN);
        String imageUrl = mCursor.getString(MainActivity.IDX_COL_IMAGE_URL);
        if (returnDate != 0) {
            holder.dates.setText(
                    DateUtils.formatDateRange(mContext, departureDate, returnDate, DateUtils.FORMAT_SHOW_DATE));
        } else {
            holder.dates.setText(
                    DateUtils.formatDateTime(mContext, departureDate, DateUtils.FORMAT_SHOW_DATE));
        }
        holder.title.setText(title);
        Picasso.with(mContext)
                .load(imageUrl)
                .into(holder.image);
    }

    @Override
    public int getItemCount() {
        if (null == mCursor) return 0;
        return mCursor.getCount();
    }

    @Override
    public long getItemId(int position) {
        mCursor.moveToPosition(position);
        return mCursor.getLong(mCursor.getColumnIndex(BaseColumns._ID));
    }

    void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
    }

    interface TripViewListener {
        void onDetailOpenClicked(Long id);

        void onEditClicked(Long id);

        void onDoneClicked(Long id);
    }
}
