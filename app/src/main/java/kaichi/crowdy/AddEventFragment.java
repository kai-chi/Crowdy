package kaichi.crowdy;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import static kaichi.crowdy.database.EventDatabaseContract.Event;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddEventFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    //defines callback methd implemented by MainActivity
    public interface AddEventFragmentListener {
        //called when event is saved
        void onAddEventCompleted(Uri eventUri);
    }

    private static final int EVENT_LOADER = 0;

    private AddEventFragmentListener listener;
    private Uri eventUri;
    private boolean addingNewEvent = true;

    private TextInputLayout titleTextInputLayout;
    private TextInputLayout descriptionTextInputLayout;
    private Button saveButton;

    private CoordinatorLayout coordinatorLayout;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listener = (AddEventFragmentListener) context;
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

        View view = inflater.inflate(R.layout.fragment_add_event,
                                     container,
                                     false);
        titleTextInputLayout = view.findViewById(R.id.titleTextInputLayout);
        titleTextInputLayout.getEditText().addTextChangedListener(titleChangedListener);
        descriptionTextInputLayout = view.findViewById(R.id.descriptionTextInputLayout);

        saveButton = view.findViewById(R.id.saveButton);
        saveButton.setOnClickListener(saveEventButtonClicked);
        updateSaveButton();

        coordinatorLayout = getActivity().findViewById(R.id.coordinatorLayout);

        Bundle arguments = getArguments();

        if (arguments != null) {
            addingNewEvent = false;
            eventUri = arguments.getParcelable(MainActivity.EVENT_URI);
        }

        //if editing an existing event - create Loader to get the event
        if (eventUri != null) {
            getLoaderManager().initLoader(EVENT_LOADER,
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

    private final View.OnClickListener saveEventButtonClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //hide the virtual keyboard
            ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(getView().getWindowToken(),
                                             0);
            saveEvent();
        }
    };

    private void saveEvent() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Event.COLUMN_TITLE,
                          titleTextInputLayout.getEditText().getText().toString());
        contentValues.put(Event.COLUMN_DESCRIPTION,
                          descriptionTextInputLayout.getEditText().getText().toString());

        if (addingNewEvent) {
            Uri newEventUri = getActivity().getContentResolver().insert(Event.CONTENT_URI,
                                                                        contentValues);
            if (newEventUri != null) {
                Snackbar.make(coordinatorLayout,
                              getString(R.string.add_event_success),
                              Snackbar.LENGTH_LONG).show();
                listener.onAddEventCompleted(newEventUri);
            } else {
                Snackbar.make(coordinatorLayout,
                              getString(R.string.add_event_error),
                              Snackbar.LENGTH_LONG).show();
            }
        } else {
            int updatedRows = getActivity().getContentResolver().update(eventUri,
                                                                        contentValues,
                                                                        null,
                                                                        null);
            if (updatedRows > 0) {
                listener.onAddEventCompleted(eventUri);
                Snackbar.make(coordinatorLayout,
                              R.string.event_updated,
                              Snackbar.LENGTH_LONG).show();
            } else {
                Snackbar.make(coordinatorLayout,
                              R.string.event_update_error,
                              Snackbar.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case EVENT_LOADER:
                return new CursorLoader(getActivity(),
                                        eventUri,
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
            int titleIndex = data.getColumnIndex(Event.COLUMN_TITLE);
            int descriptionIndex = data.getColumnIndex(Event.COLUMN_DESCRIPTION);

            titleTextInputLayout.getEditText().setText(data.getString(titleIndex));
            descriptionTextInputLayout.getEditText().setText(data.getString(descriptionIndex));

            updateSaveButton();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
