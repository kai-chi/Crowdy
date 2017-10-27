package kaichi.crowdy;


import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import android.widget.TextView;

import static kaichi.crowdy.database.EventDatabaseContract.Event;


/**
 * A simple {@link Fragment} subclass.
 */
public class DetailFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    public interface DetailFragmentListener {
        void onEventDeleted();

        void onEditEvent(Uri eventUri);
    }

    private static final int EVENT_LOADER = 0;

    private DetailFragmentListener listener;
    private Uri eventUri;

    private TextView titleTextView;
    private TextView descriptionTextView;

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
            eventUri = arguments.getParcelable(MainActivity.EVENT_URI);

        View view =
                inflater.inflate(R.layout.fragment_detail,
                                 container,
                                 false);

        titleTextView = view.findViewById(R.id.titleTextView);
        descriptionTextView = view.findViewById(R.id.descriptionTextView);

        getLoaderManager().initLoader(EVENT_LOADER,
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
                deleteEvent();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void deleteEvent() {
//        confirmDelete.show(getFragmentManager(), "confirm delete");
        getActivity().getContentResolver().delete(
                eventUri,
                null,
                null
        );
        listener.onEventDeleted();
    }


    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        CursorLoader cursorLoader;

        switch (i) {
            case EVENT_LOADER:
                cursorLoader = new CursorLoader(getActivity(),
                                                eventUri,
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
            int titleIndex = cursor.getColumnIndex(Event.COLUMN_TITLE);
            int descriptionIndex = cursor.getColumnIndex(Event.COLUMN_DESCRIPTION);

            titleTextView.setText(cursor.getString(titleIndex));
            descriptionTextView.setText(cursor.getString(descriptionIndex));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
