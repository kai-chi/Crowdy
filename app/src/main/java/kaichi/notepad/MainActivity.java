package kaichi.notepad;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.jaredrummler.android.colorpicker.ColorPickerDialogListener;

public class MainActivity extends AppCompatActivity
        implements NotesFragment.NotesFragmentListener,
        AddNoteFragment.AddNoteFragmentListener,
        DetailFragment.DetailFragmentListener,
        ColorPickerDialogListener {

    // key for storing a note's Uri in a Bundle passed to a fragment
    public static final String NOTE_URI = "note_uri";


    private NotesFragment notesFragment;
    private AddNoteFragment addNoteFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //if layout contains fragment_container, the phone layout is in use
        //create and display am NotesFragment
        if (savedInstanceState == null &&
                findViewById(R.id.fragment_container) != null) {
            notesFragment = new NotesFragment();

            //add the fragment to the FrameLayout
            FragmentTransaction transaction =
                    getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.fragment_container,
                            notesFragment);
            transaction.commit();
        } else {
            //TODO: add support for bigger screens
        }
    }

    @Override
    public void onNoteSelected(Uri noteUri) {
        if (findViewById(R.id.fragment_container) != null) { //phone
            displayNote(noteUri,
                        R.id.fragment_container);
        } else { //tablet
            getSupportFragmentManager().popBackStack();
            //TODO: change the id to different container
            displayNote(noteUri,
                        R.id.fragment_container);
        }
    }

    private void displayNote(Uri noteUri, int viewID) {
        DetailFragment detailFragment = new DetailFragment();

        Bundle arguments = new Bundle();
        arguments.putParcelable(NOTE_URI,
                                noteUri);
        detailFragment.setArguments(arguments);

        FragmentTransaction transaction =
                getSupportFragmentManager().beginTransaction();
        transaction.replace(viewID,
                            detailFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    //display AddNoteFragment to add new note
    @Override
    public void onAddNote() {
        if (findViewById(R.id.fragment_container) != null) { //phone
            displayAddNoteFragment(R.id.fragment_container,
                                   null);
        } else { //tablet
            //TODO: change container for tablet
            displayAddNoteFragment(R.id.fragment_container,
                                   null);
        }
    }

    private void displayAddNoteFragment(int viewID, Uri noteUri) {
        addNoteFragment = new AddNoteFragment();

        //if editing existing contact pass Uri in argument 2
        if (noteUri != null) {
            Bundle arguments = new Bundle();
            arguments.putParcelable(NOTE_URI,
                                    noteUri);
            addNoteFragment.setArguments(arguments);
        }

        FragmentTransaction transaction =
                getSupportFragmentManager().beginTransaction();
        transaction.replace(viewID,
                            addNoteFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }


    @Override
    public void onAddNoteCompleted(Uri noteUri) {
        getSupportFragmentManager().popBackStack();
        notesFragment.updateNoteList();

        if (findViewById(R.id.fragment_container) == null) { //tablet
            getSupportFragmentManager().popBackStack();

            //TODO: change container
            displayNote(noteUri,
                        R.id.fragment_container);
        }
    }

    @Override
    public void onNoteDeleted() {
        getSupportFragmentManager().popBackStack();
        notesFragment.updateNoteList();
    }

    @Override
    public void onEditNote(Uri noteUri) {

    }

    @Override
    public void onColorSelected(int dialogId, int color) {
        addNoteFragment.setNoteColor(color);
    }

    @Override
    public void onDialogDismissed(int dialogId) {

    }
}
