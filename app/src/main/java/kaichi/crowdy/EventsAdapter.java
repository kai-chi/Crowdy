package kaichi.crowdy;

import android.database.Cursor;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import kaichi.crowdy.database.EventDatabaseContract;

import static android.support.v7.widget.RecyclerView.ViewHolder;


public class EventsAdapter
        extends RecyclerView.Adapter<EventsAdapter.EventViewHolder> {

    //interface implemented by EventsFragment to respond
    //when the user touches an item in the RecycerView
    public interface EventClickListener {
        void onClick(Uri eventUri);
    }

    public class EventViewHolder extends ViewHolder {
        public final TextView title;
        public final TextView description;
        public final TextView numberOfPeopleSignedUp;
        public final TextView moneyLeft;
        public final ImageView menu;
        private long rowID;

        public EventViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.eventTitleTextView);
            description = itemView.findViewById(R.id.eventDescriptionTextView);
            numberOfPeopleSignedUp = itemView.findViewById(R.id.numberOfPeopleSignedUpTextView);
            moneyLeft = itemView.findViewById(R.id.moneyLeftTextView);
            menu = itemView.findViewById(R.id.eventMenuImageView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickListener.onClick(EventDatabaseContract.Event.buildEventUri(rowID));
                }
            });
        }

        public void setRowID(long rowID) {
            this.rowID = rowID;
        }

    }

    private final EventClickListener clickListener;
    private Cursor cursor = null;

    public EventsAdapter(EventClickListener eventClickListener) {
        this.clickListener = eventClickListener;
    }

    @Override
    public EventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                                  .inflate(R.layout.event_card,
                                           parent,
                                           false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final EventViewHolder holder, int position) {
        cursor.moveToPosition(position);
        holder.setRowID(cursor.getLong(cursor.getColumnIndex(EventDatabaseContract.Event._ID)));
        holder.title.setText(cursor.getString(cursor.getColumnIndex(EventDatabaseContract.Event.COLUMN_TITLE)));
        holder.description.setText(cursor.getString(cursor.getColumnIndex(EventDatabaseContract.Event.COLUMN_DESCRIPTION)));
        holder.moneyLeft.setText(cursor.getString(cursor.getColumnIndex(EventDatabaseContract.Event.COLUMN_MONEY_LEFT)));
        holder.numberOfPeopleSignedUp.setText(cursor.getString(cursor.getColumnIndex(EventDatabaseContract.Event.COLUMN_PEOPLE_SIGNED_UP)));
    }

    //TODO: add this fucking menu later
//    private void showPopupMenu(View view) {
//        PopupMenu popupMenu = new PopupMenu(mContext,
//                                            view);
//        MenuInflater inflater = popupMenu.getMenuInflater();
//        inflater.inflate(R.menu.menu_event,
//                         popupMenu.getMenu());
//        popupMenu.setOnMenuItemClickListener(new EventMenuItemClickListener());
//        popupMenu.show();
//    }
//
//    class EventMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {
//
//        @Override
//        public boolean onMenuItemClick(MenuItem item) {
//            switch (item.getItemId()) {
//                case R.id.action_delete_event:
//                    Toast.makeText(mContext,
//                                   R.string.message_not_implemented,
//                                   Toast.LENGTH_SHORT).show();
//                    break;
//                case R.id.action_show_event:
//                    Toast.makeText(mContext,
//                                   R.string.message_not_implemented,
//                                   Toast.LENGTH_SHORT).show();
//                    break;
//                case R.id.action_statistics:
//                    Toast.makeText(mContext,
//                                   R.string.message_not_implemented,
//                                   Toast.LENGTH_SHORT).show();
//                    break;
//                default:
//                    throw new UnsupportedOperationException("Menu item not recognized");
//            }
//            return true;
//        }
//
//    }

    @Override
    public int getItemCount() {
        return (cursor != null) ? cursor.getCount() : 0;
    }

    public void swapCursor(Cursor cursor) {
        this.cursor = cursor;
        notifyDataSetChanged();
    }
}
