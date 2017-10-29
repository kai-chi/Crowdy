package kaichi.crowdy;


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

import kaichi.crowdy.database.EventDatabaseContract;


/**
 * A simple {@link Fragment} subclass.
 */
public class EventsFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    public interface EventsFragmentListener {

        //called when event selected
        void onEventSelected(Uri eventUri);

        //called when add button pressed
        void onAddEvent();

    }

    private static final int EVENTS_LOADER = 0;

    private EventsFragmentListener listener;

    private EventsAdapter eventsAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater,
                           container,
                           savedInstanceState);

        setHasOptionsMenu(true);

        View view = inflater.inflate(R.layout.fragment_events,
                                     container,
                                     false);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getBaseContext()));

        eventsAdapter = new EventsAdapter(new EventsAdapter.EventClickListener() {

            @Override
            public void onClick(Uri eventUri) {
                listener.onEventSelected(eventUri);
            }
        });

        recyclerView.setAdapter(eventsAdapter);

        //added to improve performance - maybe to remove later as the size may vary
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(1,
                                                                     dpToPx(1),
                                                                     true));

        FloatingActionButton addButton = view.findViewById(R.id.addEventFloatingActionButton);
        addButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                listener.onAddEvent();
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

    //set EventsFragmentListener when fragment attached
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listener = (EventsFragmentListener) context;
    }

    //remove EventsFragmentListener when fragment detached
    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(EVENTS_LOADER,
                                      null,
                                      this);
    }

    //called from MainActivity when DB updated
    public void updateEventList() {
        eventsAdapter.notifyDataSetChanged();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case EVENTS_LOADER:
                return new CursorLoader(getActivity(),
                                        EventDatabaseContract.Event.CONTENT_URI,
                                        null,
                                        null,
                                        null,
                                        EventDatabaseContract.Event.COLUMN_CREATION_TIMESTAMP + " COLLATE NOCASE DESC");
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        eventsAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        eventsAdapter.swapCursor(null);
    }
}
