package kaichi.notepad;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import static kaichi.notepad.database.NoteDatabaseContract.Note;


/**
 * A simple {@link Fragment} subclass.
 */
public class DetailFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    public interface DetailFragmentListener {
        void onNoteDeleted();

        void onEditNote(Uri noteUri);
    }

    private static final int NOTE_LOADER = 0;

    private DetailFragmentListener listener;
    private Uri noteUri;

    private TextInputEditText detailTitleTextView;
    private TextInputEditText detailDescriptionTextView;
    private LinearLayout linearLayout;

    private boolean deleteNote = false;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listener = (DetailFragmentListener) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater,
                           container,
                           savedInstanceState);
        setHasOptionsMenu(true);

        Bundle arguments = getArguments();

        if (arguments != null)
            noteUri = arguments.getParcelable(MainActivity.NOTE_URI);

        View view =
                inflater.inflate(R.layout.fragment_detail,
                                 container,
                                 false);

        detailTitleTextView = view.findViewById(R.id.detailTitleInputEditText);
        detailDescriptionTextView = view.findViewById(R.id.detailDescriptionInputEditText);
        linearLayout = view.findViewById(R.id.fragmentDetailLinearLayout);
        getLoaderManager().initLoader(NOTE_LOADER,
                                      null,
                                      this);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu,
                                  inflater);
        inflater.inflate(R.menu.fragment_detail_menu,
                         menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                deleteNote();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void deleteNote() {
//        confirmDelete.show(getFragmentManager(), "confirm delete");
        getActivity().getContentResolver().delete(
                noteUri,
                null,
                null
        );
        deleteNote = true;
        listener.onNoteDeleted();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (!deleteNote) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(Note.COLUMN_TITLE,
                              detailTitleTextView.getText().toString());
            contentValues.put(Note.COLUMN_DESCRIPTION,
                              detailDescriptionTextView.getText().toString());

            int updatedRows = getActivity().getContentResolver().update(noteUri,
                                                                        contentValues,
                                                                        null,
                                                                        null);
            if (updatedRows <= 0) {
                Snackbar.make(linearLayout,
                              R.string.note_update_error,
                              Snackbar.LENGTH_LONG).show();
            }

            getActivity().getSupportFragmentManager().popBackStack();

        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        CursorLoader cursorLoader;

        switch (i) {
            case NOTE_LOADER:
                cursorLoader = new CursorLoader(getActivity(),
                                                noteUri,
                                                null,
                                                null,
                                                null,
                                                null);
                break;
            default:
                cursorLoader = null;
                break;
        }

        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor != null && cursor.moveToFirst()) {
            int titleIndex = cursor.getColumnIndex(Note.COLUMN_TITLE);
            int descriptionIndex = cursor.getColumnIndex(Note.COLUMN_DESCRIPTION);
            int colorIndex = cursor.getColumnIndex(Note.COLUMN_COLOR);

            detailTitleTextView.setText(cursor.getString(titleIndex));
            detailDescriptionTextView.setText(cursor.getString(descriptionIndex));
//            linearLayout.setBackgroundColor(cursor.getInt(colorIndex));
            GradientDrawable gd = (GradientDrawable) (linearLayout.getBackground()).getCurrent();
            gd.setColor(cursor.getInt(colorIndex));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
