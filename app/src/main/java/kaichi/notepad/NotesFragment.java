package kaichi.notepad;


import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import kaichi.notepad.database.NoteDatabaseContract;


/**
 * A simple {@link Fragment} subclass.
 */
public class NotesFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    public interface NotesFragmentListener {

        //called when note selected
        void onNoteSelected(Uri noteUri);

        //called when add button pressed
        void onAddNote();

    }

    private static final int NOTES_LOADER = 0;

    private NotesFragmentListener listener;

    private NotesAdapter notesAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater,
                           container,
                           savedInstanceState);

        setHasOptionsMenu(true);

        View view = inflater.inflate(R.layout.fragment_notes,
                                     container,
                                     false);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getBaseContext()));

        notesAdapter = new NotesAdapter(new NotesAdapter.NoteClickListener() {

            @Override
            public void onClick(Uri noteUri) {
                listener.onNoteSelected(noteUri);
            }
        });

        recyclerView.setAdapter(notesAdapter);

        //added to improve performance - maybe to remove later as the size may vary
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(1,
                                                                     dpToPx(1),
                                                                     true));

        FloatingActionButton addButton = view.findViewById(R.id.addNoteFloatingActionButton);
        addButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                listener.onAddNote();
            }
        });

        return view;
    }

    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                                                    dp,
                                                    r.getDisplayMetrics()));
    }

    //set NotesFragmentListener when fragment attached
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listener = (NotesFragmentListener) context;
    }

    //remove NotesFragmentListener when fragment detached
    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(NOTES_LOADER,
                                      null,
                                      this);
    }

    //called from MainActivity when DB updated
    public void updateNoteList() {
        notesAdapter.notifyDataSetChanged();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case NOTES_LOADER:
                return new CursorLoader(getActivity(),
                                        NoteDatabaseContract.Note.CONTENT_URI,
                                        null,
                                        null,
                                        null,
                                        NoteDatabaseContract.Note.COLUMN_CREATION_TIMESTAMP + " COLLATE NOCASE DESC");
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        notesAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        notesAdapter.swapCursor(null);
    }
}
