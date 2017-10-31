package kaichi.notepad;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.content.res.ResourcesCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import com.jaredrummler.android.colorpicker.ColorPickerDialog;

import static kaichi.notepad.database.NoteDatabaseContract.Note;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddNoteFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    //defines callback methd implemented by MainActivity
    public interface AddNoteFragmentListener {
        //called when note is saved
        void onAddNoteCompleted(Uri noteUri);
    }

    private static final int NOTE_LOADER = 0;
    private AddNoteFragmentListener listener;
    private Uri noteUri;
    private boolean addingNewNote = true;
    private TextInputLayout titleTextInputLayout;
    private TextInputLayout descriptionTextInputLayout;
    private TextView colorTextView;
    private int color;
    private Button saveButton;
    private CoordinatorLayout coordinatorLayout;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listener = (AddNoteFragmentListener) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater,
                           container,
                           savedInstanceState);
        setHasOptionsMenu(true);

        View view = inflater.inflate(R.layout.fragment_add_note,
                                     container,
                                     false);
        titleTextInputLayout = view.findViewById(R.id.titleTextInputLayout);
        titleTextInputLayout.getEditText().addTextChangedListener(titleChangedListener);
        descriptionTextInputLayout = view.findViewById(R.id.descriptionTextInputLayout);
        colorTextView = view.findViewById(R.id.colorTextView);
        setNoteColor(ResourcesCompat.getColor(getResources(), R.color.colorLightPrimaryColor, null));
        colorTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE))
                        .hideSoftInputFromWindow(getView().getWindowToken(),
                                                 0);
                ColorPickerDialog.newBuilder().setShowColorShades(false).show(getActivity());
            }
        });

        saveButton = view.findViewById(R.id.saveButton);
        saveButton.setOnClickListener(saveNoteButtonClicked);
        updateSaveButton();

        coordinatorLayout = getActivity().findViewById(R.id.coordinatorLayout);

        Bundle arguments = getArguments();

        if (arguments != null) {
            addingNewNote = false;
            noteUri = arguments.getParcelable(MainActivity.NOTE_URI);
        }

        //if editing an existing note - create Loader to get the note
        if (noteUri != null) {
            getLoaderManager().initLoader(NOTE_LOADER,
                                          null,
                                          this);
        }

        return view;

    }

    private final TextWatcher titleChangedListener = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            updateSaveButton();
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };

    private void updateSaveButton() {
        String input = titleTextInputLayout.getEditText().getText().toString();

        if (input.length() != 0)
            saveButton.setEnabled(true);
        else
            saveButton.setEnabled(false);
    }

    private final View.OnClickListener saveNoteButtonClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //hide the virtual keyboard
            ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(getView().getWindowToken(),
                                             0);
            saveNote();
        }
    };

    private void saveNote() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Note.COLUMN_TITLE,
                          titleTextInputLayout.getEditText().getText().toString());
        contentValues.put(Note.COLUMN_DESCRIPTION,
                          descriptionTextInputLayout.getEditText().getText().toString());
        contentValues.put(Note.COLUMN_COLOR,
                          color);

        if (addingNewNote) {
            Uri newNoteUri = getActivity().getContentResolver().insert(Note.CONTENT_URI,
                                                                        contentValues);
            if (newNoteUri != null) {
                listener.onAddNoteCompleted(newNoteUri);
            } else {
                Snackbar.make(coordinatorLayout,
                              getString(R.string.add_note_error),
                              Snackbar.LENGTH_LONG).show();
            }
        } else {
            int updatedRows = getActivity().getContentResolver().update(noteUri,
                                                                        contentValues,
                                                                        null,
                                                                        null);
            if (updatedRows > 0) {
                listener.onAddNoteCompleted(noteUri);
                Snackbar.make(coordinatorLayout,
                              R.string.note_updated,
                              Snackbar.LENGTH_LONG).show();
            } else {
                Snackbar.make(coordinatorLayout,
                              R.string.note_update_error,
                              Snackbar.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case NOTE_LOADER:
                return new CursorLoader(getActivity(),
                                        noteUri,
                                        null,
                                        null,
                                        null,
                                        null);
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {
            int titleIndex = data.getColumnIndex(Note.COLUMN_TITLE);
            int descriptionIndex = data.getColumnIndex(Note.COLUMN_DESCRIPTION);

            titleTextInputLayout.getEditText().setText(data.getString(titleIndex));
            descriptionTextInputLayout.getEditText().setText(data.getString(descriptionIndex));

            updateSaveButton();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    public void setNoteColor(int color) {
        color = Color.argb(128, Color.red(color), Color.green(color), Color.blue(color));
        setColor(color);
        colorTextView.setBackgroundColor(color);
        colorTextView.setTextColor(getContrastColor(color));
    }

    private void setColor(int color) {
        this.color = color;
    }

    private int getContrastColor(int color) {
        int red = Color.red(color);
        double y = (299 * (int) Color.red(color) + 587 * (int) Color.green(color) + 114 * (int) Color.blue(color)) / 1000;
        return y >= 128 ? Color.BLACK : Color.WHITE;
    }
}
