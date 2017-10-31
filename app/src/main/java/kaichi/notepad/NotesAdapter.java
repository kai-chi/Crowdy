package kaichi.notepad;

import android.database.Cursor;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import kaichi.notepad.database.NoteDatabaseContract.Note;

import static android.support.v7.widget.RecyclerView.ViewHolder;


public class NotesAdapter
        extends RecyclerView.Adapter<NotesAdapter.NoteViewHolder> {

    //interface implemented by NotesFragment to respond
    //when the user touches an item in the RecycerView
    public interface NoteClickListener {
        void onClick(Uri noteUri);
    }

    public class NoteViewHolder extends ViewHolder {
        private final TextView title;
        private final TextView description;
        private long rowID;
        private final RelativeLayout relativeLayout;

        public NoteViewHolder(final View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.noteTitleTextView);
            description = itemView.findViewById(R.id.noteDescriptionTextView);
            relativeLayout = itemView.findViewById(R.id.cardRelativeLayout);
            relativeLayout.setOnClickListener(
                    new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {
                            clickListener.onClick(Note.buildNoteUri(rowID));
                        }
                    }
            );
        }

        private void setRowID(long rowID) {
            this.rowID = rowID;
        }

        private void setColor(int color) {
            relativeLayout.setBackgroundColor(color);
        }
    }

    private final NoteClickListener clickListener;
    private Cursor cursor = null;

    public NotesAdapter(NoteClickListener noteClickListener) {
        this.clickListener = noteClickListener;
    }

    @Override
    public NoteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                                  .inflate(R.layout.note_card,
                                           parent,
                                           false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final NoteViewHolder holder, int position) {
        cursor.moveToPosition(position);
        holder.setRowID(cursor.getLong(cursor.getColumnIndex(Note._ID)));
        holder.title.setText(cursor.getString(cursor.getColumnIndex(Note.COLUMN_TITLE)));
        holder.description.setText(cursor.getString(cursor.getColumnIndex(Note.COLUMN_DESCRIPTION)));
        holder.setColor(cursor.getInt(cursor.getColumnIndex(Note.COLUMN_COLOR)));
    }


    @Override
    public int getItemCount() {
        return (cursor != null) ? cursor.getCount() : 0;
    }

    public void swapCursor(Cursor cursor) {
        this.cursor = cursor;
        notifyDataSetChanged();
    }
}
