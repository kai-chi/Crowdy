package kaichi.crowdy.database;


import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import kaichi.crowdy.R;
import kaichi.crowdy.database.EventDatabaseContract.Event;

public class EventContentProvider extends ContentProvider {

    private EventDatabaseHelper mDbHelper;

    private SQLiteDatabase mDb;

    //UriMatcher helps ContectProvider determine operation to perform
    private static final UriMatcher uriMatcher =
            new UriMatcher(UriMatcher.NO_MATCH);

    //constants used with UriMatcher to determine operation to perform
    private static final int ONE_EVENT = 1; //manipulate one ievent
    private static final int EVENTS = 2; //manipulate events table

    static {
        //Uri for EventTOBEREMOVED with specific id (#)
        uriMatcher.addURI(EventDatabaseContract.AUTHORITY,
                          Event.TABLE_NAME + "/#",
                          ONE_EVENT);

        //Uri for Events table
        uriMatcher.addURI(EventDatabaseContract.AUTHORITY,
                          Event.TABLE_NAME,
                          EVENTS);
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new EventDatabaseHelper(getContext());
        mDb = mDbHelper.getWritableDatabase();
        return true;
    }

    //query the database
    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(Event.TABLE_NAME);

        switch (uriMatcher.match(uri)) {
            case ONE_EVENT:
                queryBuilder.appendWhere(
                        Event._ID + "=" + uri.getLastPathSegment());
                break;
            case EVENTS:
                break;
            default:
                throw new UnsupportedOperationException(
                        getContext().getString(R.string.invalid_query_uri) + uri);
        }

        Cursor cursor = queryBuilder.query(mDb,
                                           projection,
                                           selection,
                                           selectionArgs,
                                           null,
                                           null,
                                           sortOrder);

        cursor.setNotificationUri(getContext().getContentResolver(),
                                  uri);
        return cursor;
    }

    //insert a new event
    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        Uri newEventUri = null;

        switch (uriMatcher.match(uri)) {
            case EVENTS:
                long rowID = mDb.insert(Event.TABLE_NAME,
                                        null,
                                        values);

                //if the event was inserted create an appropriate Uri
                if (rowID > 0) {
                    newEventUri = Event.buildEventUri(rowID);
                    getContext().getContentResolver().notifyChange(uri,
                                                                   null);
                } else {
                    throw new SQLException(getContext().getString(R.string.insert_failed) + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException(getContext().getString(R.string.invalid_query_uri) + uri);
        }

        return newEventUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int numbersOfRowsDeleted;

        switch (uriMatcher.match(uri)) {
            case ONE_EVENT:
                String id = uri.getLastPathSegment();

                numbersOfRowsDeleted = mDb.delete(Event.TABLE_NAME,
                                                  Event._ID + "=" + id,
                                                  selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException(getContext().getString(R.string.invalid_delete) + uri);
        }

        if (numbersOfRowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri,
                                                           null);
        }

        return numbersOfRowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        int numberOfRowsUpdated;

        switch (uriMatcher.match(uri)) {
            case ONE_EVENT:
                String id = uri.getLastPathSegment();

                numberOfRowsUpdated = mDb.update(Event.TABLE_NAME,
                                                 values,
                                                 Event._ID + "=" + id,
                                                 selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException(getContext().getString(R.string.invalid_update) + uri);
        }

        if (numberOfRowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri,
                                                           null);
        }

        return numberOfRowsUpdated;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

}
