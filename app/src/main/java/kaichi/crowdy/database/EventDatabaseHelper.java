package kaichi.crowdy.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import kaichi.crowdy.database.EventDatabaseContract.Event;

/**
 * Created by kaichi on 16.10.17.
 */

public class EventDatabaseHelper extends SQLiteOpenHelper {

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + Event.TABLE_NAME + " (" +
                    Event._ID + " INTEGER PRIMARY KEY," +
                    Event.COLUMN_TITLE + " TEXT," +
                    Event.COLUMN_DESCRIPTION + " TEXT," +
                    Event.COLUMN_CREATION_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    Event.COLUMN_COLOR + " INTEGER)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + Event.TABLE_NAME;

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Crowdy.db";

    public EventDatabaseHelper(Context context) {
        super(context,
              DATABASE_NAME,
              null,
              DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(SQL_DELETE_ENTRIES);
        onCreate(sqLiteDatabase);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db,
                  oldVersion,
                  newVersion);
    }
}
