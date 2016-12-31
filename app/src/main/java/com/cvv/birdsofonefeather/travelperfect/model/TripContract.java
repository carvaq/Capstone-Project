package com.cvv.birdsofonefeather.travelperfect.model;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Carla
 * Date: 31/12/2016
 * Project: Capstone-Project
 */

public class TripContract {
    public static final String CONTENT_AUTHORITY = "com.cvv.birdsofonefeather.travelperfect";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_TRIP = "trip";
    public static final String PATH_REMINDER = "reminder";
    public static final String PATH_LIST_ITEM = "listItem";

    private static final String CONTENT_TYPE_DIR = "vnd.android.cursor.dir/";
    private static final String CONTENT_TYPE_ITEM = "vnd.android.cursor.item/";

    public static final class TripEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_TRIP).build();

        public static final String TABLE_NAME = "trips";
        public static final String COLUMN_NAME_OF_PLACE = "nameOfPlace";
        public static final String COLUMN_DEPARTURE = "departure";
        public static final String COLUMN_RETURN = "return";
        public static final String COLUMN_IMAGE_URL = "imageUrl";
        public static final String COLUMN_LIST_ITEM_FK = "listItemFK";
        public static final String COLUMN_REMINDER_FK = "reminderFK";

        public static final String CONTENT_TYPE =
                CONTENT_TYPE_DIR + CONTENT_URI + "/" + PATH_TRIP;
        public static final String CONTENT_ITEM_TYPE =
                CONTENT_TYPE_ITEM + CONTENT_URI + "/" + PATH_TRIP;

        public static Uri buildTripUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class ListItemEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_LIST_ITEM).build();

        public static final String TABLE_NAME = "listItems";
        public static final String COLUMN_NUMBER_OF = "numberOf";
        public static final String COLUMN_WHAT = "what";

        public static final String CONTENT_TYPE =
                CONTENT_TYPE_DIR + CONTENT_URI + "/" + PATH_LIST_ITEM;
        public static final String CONTENT_ITEM_TYPE =
                CONTENT_TYPE_ITEM + CONTENT_URI + "/" + PATH_LIST_ITEM;

        public static Uri buildListItemUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class ReminderEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_REMINDER).build();

        public static final String TABLE_NAME = "reminders";
        public static final String COLUMN_WHEN = "when";

        public static final String CONTENT_TYPE =
                CONTENT_TYPE_DIR + CONTENT_URI + "/" + PATH_REMINDER;
        public static final String CONTENT_ITEM_TYPE =
                CONTENT_TYPE_ITEM + CONTENT_URI + "/" + PATH_REMINDER;

        public static Uri buildReminderUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}
