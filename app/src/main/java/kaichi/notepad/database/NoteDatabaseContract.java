package kaichi.notepad.database;


import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public class NoteDatabaseContract {

    public static final String AUTHORITY =
            "kaichi.notepad.database";

    private static final Uri BASE_CONTENT_URI =
            Uri.parse("content://" + AUTHORITY);

    public static final class Note implements BaseColumns {

        public static final String TABLE_NAME = "notes";

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(TABLE_NAME).build();

        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_CREATION_TIMESTAMP = "creationTimestamp";
        public static final String COLUMN_COLOR = "color";


        //create a Uri for a specific note
        public static Uri buildNoteUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI,
                                              id);
        }

    }

    private NoteDatabaseContract() {
    }
}
