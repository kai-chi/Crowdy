package kaichi.crowdy.database;


import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public class EventDatabaseContract {

    public static final String AUTHORITY =
            "kaichi.crowdy.database";

    private static final Uri BASE_CONTENT_URI =
            Uri.parse("content://" + AUTHORITY);

    public static final class Event implements BaseColumns {

        public static final String TABLE_NAME = "events";

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(TABLE_NAME).build();

        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_CREATION_TIMESTAMP = "creationTimestamp";
        public static final String COLUMN_COLOR = "color";


        //create a Uri for a specific event
        public static Uri buildEventUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI,
                                              id);
        }

    }

    private EventDatabaseContract() {
    }
}
