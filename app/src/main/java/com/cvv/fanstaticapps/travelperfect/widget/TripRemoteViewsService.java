package com.cvv.fanstaticapps.travelperfect.widget;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Paint;
import android.os.Binder;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.cvv.fanstaticapps.travelperfect.R;
import com.cvv.fanstaticapps.travelperfect.database.Item;
import com.cvv.fanstaticapps.travelperfect.ui.ListItemHelper;
import com.cvv.fanstaticapps.travelperfect.ui.fragments.DetailFragment;


public class TripRemoteViewsService extends RemoteViewsService {

    public TripRemoteViewsService() {
    }


    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new TripRemoteViewsFactory(intent.getLongExtra(DetailFragment.ARGS_TRIP_ID, 0));
    }

    private class TripRemoteViewsFactory implements RemoteViewsFactory {

        private Cursor mCursor;
        private long mTripId;

        public TripRemoteViewsFactory(long tripId) {
            mTripId = tripId;
        }

        @Override
        public void onCreate() {

        }

        @Override
        public void onDataSetChanged() {

            final long identityToken = Binder.clearCallingIdentity();

            mCursor = ListItemHelper.getListItemCursor(getApplicationContext(), mTripId);

            Binder.restoreCallingIdentity(identityToken);
        }

        @Override
        public void onDestroy() {
            if (mCursor != null) {
                mCursor.close();
                mCursor = null;
            }
        }

        @Override
        public int getCount() {
            return mCursor != null ? mCursor.getCount() : 0;
        }

        @Override
        public RemoteViews getViewAt(int position) {
            if (mCursor == null || !mCursor.moveToPosition(position)) return null;
            Item item = new Item(mCursor);

            RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.trip_widget_item);
            remoteViews.setTextViewText(R.id.number_of, String.valueOf(item.getNumberOf()));
            remoteViews.setTextViewText(R.id.name, item.getWhat());

            paintStrikeThrough(item, remoteViews);

            return remoteViews;
        }

        private void paintStrikeThrough(Item item, RemoteViews remoteViews) {
            if (item.isDone()) {
                remoteViews.setInt(R.id.number_of, "setPaintFlags", Paint.ANTI_ALIAS_FLAG | Paint.STRIKE_THRU_TEXT_FLAG);
                remoteViews.setInt(R.id.name, "setPaintFlags", Paint.ANTI_ALIAS_FLAG | Paint.STRIKE_THRU_TEXT_FLAG);
            } else {
                remoteViews.setInt(R.id.number_of, "setPaintFlags", Paint.ANTI_ALIAS_FLAG & ~Paint.STRIKE_THRU_TEXT_FLAG);
                remoteViews.setInt(R.id.name, "setPaintFlags", Paint.ANTI_ALIAS_FLAG & ~Paint.STRIKE_THRU_TEXT_FLAG);
            }
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int position) {
            if (mCursor.moveToPosition(position))
                return mCursor.getLong(0);
            return position;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }
    }
}