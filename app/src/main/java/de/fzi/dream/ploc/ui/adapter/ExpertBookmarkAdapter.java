package de.fzi.dream.ploc.ui.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import de.fzi.dream.ploc.R;
import de.fzi.dream.ploc.data.structure.entity.ExpertBookmark;
import de.fzi.dream.ploc.ui.holder.ExpertBookmarkViewHolder;
import de.fzi.dream.ploc.ui.listener.OnExpertBookmarkCardViewListener;

/**
 * The ExpertBookmarkAdapter provides a binding from data set to the {@link ExpertBookmarkViewHolder}
 * which is displayed within a {@link RecyclerView}.
 *
 * @author Felix Melcher
 */
public class ExpertBookmarkAdapter extends RecyclerView.Adapter<ExpertBookmarkViewHolder> {

    /**
     * Public class identifier tag
     */
    public static final String TAG = ExpertBookmarkAdapter.class.getSimpleName();

    private List<ExpertBookmark> mBookmarks;
    private OnExpertBookmarkCardViewListener mListener;

    /**
     * Constructor
     *
     * @param bookmarks the list of bookmarks to be displayed through the adapter.
     * @param listener  the event listener waiting for interactions within this adapter.
     */
    public ExpertBookmarkAdapter(List<ExpertBookmark> bookmarks, OnExpertBookmarkCardViewListener listener) {
        mBookmarks = bookmarks;
        mListener = listener;
    }

    @NonNull
    @Override
    public ExpertBookmarkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ExpertBookmarkViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_view_item_expert_preview, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ExpertBookmarkViewHolder holder, int position) {
        if (mBookmarks.size() > 0) {
            holder.bindTo(mBookmarks.get(position), mListener);
        }
    }

    @Override
    public int getItemCount() {
        return mBookmarks.size();
    }

    /**
     * Set or update the current list of bookmark items to be displayed through this adapter.
     *
     * @param bookmarks the list of bookmarks to be displayed through the adapter.
     */
    public void setBookmarks(List<ExpertBookmark> bookmarks) {
        mBookmarks = bookmarks;
    }

}