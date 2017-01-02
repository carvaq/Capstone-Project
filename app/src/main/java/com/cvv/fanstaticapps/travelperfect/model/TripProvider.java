package com.cvv.fanstaticapps.travelperfect.model;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;

public class TripProvider extends ContentProvider {
    private static final int LIST_ITEM = 100;
    private static final int LIST_ITEM_ID = 101;
    private static final int REMINDER = 200;
    private static final int REMINDER_ID = 201;
    private static final int TRIP = 300;
    private static final int TRIP_ID = 301;

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private DatabaseHelper mOpenHelper;

    @Override
    public boolean onCreate() {
        mOpenHelper = new DatabaseHelper(getContext());
        return true;
    }

    /**
     * Builds a UriMatcher that is used to determine witch database request is being made.
     */
    public static UriMatcher buildUriMatcher() {
        String content = TripContract.CONTENT_AUTHORITY;

        // All paths to the UriMatcher have a corresponding code to return
        // when a match is found (the ints above).
        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(content, TripContract.PATH_LIST_ITEM, LIST_ITEM);
        matcher.addURI(content, TripContract.PATH_LIST_ITEM + "/#", LIST_ITEM_ID);
        matcher.addURI(content, TripContract.PATH_REMINDER, REMINDER);
        matcher.addURI(content, TripContract.PATH_REMINDER + "/#", REMINDER_ID);
        matcher.addURI(content, TripContract.PATH_TRIP, TRIP);
        matcher.addURI(content, TripContract.PATH_TRIP + "/#", TRIP_ID);

        return matcher;
    }

    @Override
    public String getType(@NonNull Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case LIST_ITEM:
                return TripContract.ListItemEntry.CONTENT_TYPE;
            case LIST_ITEM_ID:
                return TripContract.ListItemEntry.CONTENT_ITEM_TYPE;
            case REMINDER:
                return TripContract.ReminderEntry.CONTENT_TYPE;
            case REMINDER_ID:
                return TripContract.ReminderEntry.CONTENT_ITEM_TYPE;
            case TRIP:
                return TripContract.TripEntry.CONTENT_TYPE;
            case TRIP_ID:
                return TripContract.TripEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            case LIST_ITEM:
                retCursor = queryForItems(uri, TripContract.ListItemEntry.TABLE_NAME,
                        projection, selection, selectionArgs, sortOrder);
                break;
            case LIST_ITEM_ID:
                retCursor = queryForId(uri, TripContract.ListItemEntry.TABLE_NAME, projection, sortOrder);
                break;
            case REMINDER:
                retCursor = queryForItems(uri, TripContract.ReminderEntry.TABLE_NAME,
                        projection, selection, selectionArgs, sortOrder);
                break;
            case REMINDER_ID:
                retCursor = queryForId(uri, TripContract.ReminderEntry.TABLE_NAME, projection, sortOrder);
                break;
            case TRIP:
                retCursor = queryForItems(uri, TripContract.TripEntry.TABLE_NAME,
                        projection, selection, selectionArgs, sortOrder);
                break;
            case TRIP_ID:
                retCursor = queryForId(uri, TripContract.TripEntry.TABLE_NAME, projection, sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    private Cursor queryForId(Uri uri, String tableName, String[] projection, String sortOrder) {
        long _id = ContentUris.parseId(uri);
        return mOpenHelper.getReadableDatabase().query(
                tableName,
                projection,
                BaseColumns._ID + " = ?",
                new String[]{String.valueOf(_id)},
                null,
                null,
                sortOrder
        );
    }

    private Cursor queryForItems(Uri uri, String tableName, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return mOpenHelper.getReadableDatabase().query(
                tableName,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        long _id;
        Uri returnUri;

        switch (sUriMatcher.match(uri)) {
            case LIST_ITEM:
                _id = db.insert(TripContract.ListItemEntry.TABLE_NAME, null, values);
                if (_id > 0) {
                    returnUri = TripContract.ListItemEntry.buildListItemUri(_id);
                } else {
                    throw new UnsupportedOperationException("Unable to insert rows into: " + uri);
                }
                break;
            case REMINDER:
                _id = db.insert(TripContract.ReminderEntry.TABLE_NAME, null, values);
                if (_id > 0) {
                    returnUri = TripContract.ReminderEntry.buildReminderUri(_id);
                } else {
                    throw new UnsupportedOperationException("Unable to insert rows into: " + uri);
                }
                break;
            case TRIP:
                _id = db.insert(TripContract.TripEntry.TABLE_NAME, null, values);
                if (_id > 0) {
                    returnUri = TripContract.TripEntry.buildTripUri(_id);
                } else {
                    throw new UnsupportedOperationException("Unable to insert rows into: " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int rowsAffected;

        switch (sUriMatcher.match(uri)) {
            case LIST_ITEM:
                rowsAffected = db.delete(TripContract.ListItemEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case REMINDER:
                rowsAffected = db.delete(TripContract.ReminderEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case TRIP:
                rowsAffected = db.delete(TripContract.TripEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (selection == null || rowsAffected != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsAffected;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int rows;

        switch (sUriMatcher.match(uri)) {
            case LIST_ITEM:
                rows = db.update(TripContract.ListItemEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case REMINDER:
                rows = db.update(TripContract.ReminderEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case TRIP:
                rows = db.update(TripContract.TripEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (rows != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rows;
    }

    /**
     * You do not need to call this method. This is a method specifically to assist the testing
     * framework in running smoothly. You can read more at:
     * http://developer.android.com/reference/android/content/ContentProvider.html#shutdown()
     */
    @Override
    public void shutdown() {
        mOpenHelper.close();
        super.shutdown();
    }
}