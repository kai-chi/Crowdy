package kaichi.crowdy;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity
        implements EventsFragment.EventsFragmentListener,
        AddEventFragment.AddEventFragmentListener {

    // key for storing a event's Uri in a Bundle passed to a fragment
    public static final String EVENT_URI = "event_uri";


    private EventsFragment eventsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //if layout contains fragment_container, the phone layout is in use
        //create and display am EventsFragment
        if (savedInstanceState == null &&
                findViewById(R.id.fragment_container) != null) {
            eventsFragment = new EventsFragment();

            //add the fragment to the FrameLayout
            FragmentTransaction transaction =
                    getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.fragment_container,
                            eventsFragment);
            transaction.commit();
        } else {
            //TODO: add support for bigger screens
        }
    }

    @Override
    public void onEventSelected(Uri eventUri) {
        if (findViewById(R.id.fragment_container) != null) { //phone
            displayEvent(eventUri,
                         R.id.fragment_container);
        } else { //tablet
            getSupportFragmentManager().popBackStack();
            //TODO: change the id to different container
            displayEvent(eventUri,
                         R.id.fragment_container);
        }
    }

    private void displayEvent(Uri eventUri, int viewID) {
        //TODO: add DetailEventFragment
    }

    //display AddEventFragment to add new event
    @Override
    public void onAddEvent() {
        if (findViewById(R.id.fragment_container) != null) { //phone
            displayAddEventFragment(R.id.fragment_container,
                                    null);
        } else { //tablet
            //TODO: change container for tablet
            displayAddEventFragment(R.id.fragment_container,
                                    null);
        }
    }

    private void displayAddEventFragment(int viewID, Uri eventUri) {
        AddEventFragment addEventFragment = new AddEventFragment();

        //if editing existing contact pass Uri in argument 2
        if (eventUri != null) {
            Bundle arguments = new Bundle();
            arguments.putParcelable(EVENT_URI,
                                    eventUri);
            addEventFragment.setArguments(arguments);
        }

        FragmentTransaction transaction =
                getSupportFragmentManager().beginTransaction();
        transaction.replace(viewID,
                            addEventFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main,
                                  menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onAddEventCompleted(Uri eventUri) {
        getSupportFragmentManager().popBackStack();
        eventsFragment.updateEventList();

        if (findViewById(R.id.fragment_container) == null) { //tablet
            getSupportFragmentManager().popBackStack();

            //TODO: change container
            displayEvent(eventUri,
                         R.id.fragment_container);
        }
    }
}
