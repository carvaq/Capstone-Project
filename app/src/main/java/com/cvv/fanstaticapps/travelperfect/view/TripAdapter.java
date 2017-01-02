package com.cvv.fanstaticapps.travelperfect.view;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cvv.fanstaticapps.travelperfect.R;
import com.cvv.fanstaticapps.travelperfect.view.activities.MainActivity;
import com.squareup.picasso.Picasso;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

/**
 * Created by Carla
 * Date: 31/12/2016
 * Project: Capstone-Project
 */

public class TripAdapter extends RecyclerView.Adapter<TripAdapter.ViewHolder> {

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
        ImageView image;
        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.dates)
        TextView dates;
        @BindView(R.id.edit)
        TextView edit;
        @BindView(R.id.done)
        TextView done;
        @BindView(R.id.attributions)
        TextView attributions;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            edit.setOnClickListener(this);
            done.setOnClickListener(this);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            long itemId = TripAdapter.this.getItemId(getAdapterPosition());
            if (v.getId() == R.id.edit) {
                mTripViewListener.onEditClicked(itemId);
            } else if (v.getId() == R.id.done) {
                mTripViewListener.onDoneClicked(itemId);
            } else {
                mTripViewListener.onDetailOpenClicked(itemId);
            }
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(mLayoutInflater.inflate(R.layout.item_trip, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        mCursor.moveToPosition(position);

        String title = mCursor.getString(MainActivity.IDX_COL_NAME_OF_PLACE);
        long departureDate = mCursor.getLong(MainActivity.IDX_COL_DEPARTURE);
        long returnDate = mCursor.getLong(MainActivity.IDX_COL_RETURN);
        String imageUrl = mCursor.getString(MainActivity.IDX_COL_IMAGE_URL);
        String attributions = mCursor.getString(MainActivity.IDX_COL_ATTRIBUTIONS);
        if (returnDate != 0) {
            holder.dates.setText(
                    DateUtils.formatDateRange(mContext, departureDate, returnDate, DateUtils.FORMAT_SHOW_DATE));
        } else {
            holder.dates.setText(
                    DateUtils.formatDateTime(mContext, departureDate, DateUtils.FORMAT_SHOW_DATE));
        }
        if (attributions != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            holder.attributions.setText(Html.fromHtml(attributions, Html.FROM_HTML_MODE_COMPACT));
        } else if (attributions != null) {
            holder.attributions.setText(Html.fromHtml(attributions));
        }

        holder.image.getLayoutParams().height = mHeight;
        holder.image.getLayoutParams().width = mWidth;

        holder.title.setText(title);
        Timber.d(imageUrl);
        Picasso.with(mContext)
                .load(new File(imageUrl))
                .centerCrop()
                .resize(mWidth, mHeight)
                .placeholder(R.drawable.ic_image)
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
        return mCursor.getLong(MainActivity.IDX_COL_ID);
    }

    public void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
    }

    public interface TripViewListener {
        void onDetailOpenClicked(Long id);

        void onEditClicked(Long id);

        void onDoneClicked(Long id);
    }
}
