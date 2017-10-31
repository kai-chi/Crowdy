package kaichi.notepad.database;


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

import kaichi.notepad.R;
import kaichi.notepad.database.NoteDatabaseContract.Note;

public class NoteContentProvider extends ContentProvider {

    private NoteDatabaseHelper mDbHelper;

    private SQLiteDatabase mDb;

    //UriMatcher helps ContectProvider determine operation to perform
    private static final UriMatcher uriMatcher =
            new UriMatcher(UriMatcher.NO_MATCH);

    //constants used with UriMatcher to determine operation to perform
    private static final int ONE_NOTE = 1; //manipulate one note
    private static final int NOTES = 2; //manipulate notes table

    static {

        uriMatcher.addURI(NoteDatabaseContract.AUTHORITY,
                          Note.TABLE_NAME + "/#",
                          ONE_NOTE);

        //Uri for notes table
        uriMatcher.addURI(NoteDatabaseContract.AUTHORITY,
                          Note.TABLE_NAME,
                          NOTES);
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new NoteDatabaseHelper(getContext());
        mDb = mDbHelper.getWritableDatabase();
        return true;
    }

    //query the database
    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(Note.TABLE_NAME);

        switch (uriMatcher.match(uri)) {
            case ONE_NOTE:
                queryBuilder.appendWhere(
                        Note._ID + "=" + uri.getLastPathSegment());
                break;
            case NOTES:
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

    //insert a new note
    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        Uri newNoteUri = null;

        switch (uriMatcher.match(uri)) {
            case NOTES:
                long rowID = mDb.insert(Note.TABLE_NAME,
                                        null,
                                        values);

                //if the note was inserted create an appropriate Uri
                if (rowID > 0) {
                    newNoteUri = Note.buildNoteUri(rowID);
                    getContext().getContentResolver().notifyChange(uri,
                                                                   null);
                } else {
                    throw new SQLException(getContext().getString(R.string.insert_failed) + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException(getContext().getString(R.string.invalid_query_uri) + uri);
        }

        return newNoteUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int numbersOfRowsDeleted;

        switch (uriMatcher.match(uri)) {
            case ONE_NOTE:
                String id = uri.getLastPathSegment();

                numbersOfRowsDeleted = mDb.delete(Note.TABLE_NAME,
                                                  Note._ID + "=" + id,
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
            case ONE_NOTE:
                String id = uri.getLastPathSegment();

                numberOfRowsUpdated = mDb.update(Note.TABLE_NAME,
                                                 values,
                                                 Note._ID + "=" + id,
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
