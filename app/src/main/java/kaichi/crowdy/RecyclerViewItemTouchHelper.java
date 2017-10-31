package kaichi.crowdy;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

public class RecyclerViewItemTouchHelper
        extends ItemTouchHelper.SimpleCallback {

    public interface RecyclerViewItemTouchHelperListener {
        void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position);
    }

    private RecyclerViewItemTouchHelperListener listener;
//    private Uri eventUri;

    public RecyclerViewItemTouchHelper(int dragDirs, int swipeDirs, Context context) {
        super(dragDirs,
              swipeDirs);
        listener = (RecyclerViewItemTouchHelperListener) context;
//        this.eventUri = eventUri;
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        listener.onSwiped(viewHolder, direction, viewHolder.getAdapterPosition());
    }
}
