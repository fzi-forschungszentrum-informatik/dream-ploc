package de.fzi.dream.ploc.ui.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ImageViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import de.fzi.dream.ploc.R;
import de.fzi.dream.ploc.data.structure.entity.Collection;
import de.fzi.dream.ploc.data.structure.entity.RecordBookmark;
import de.fzi.dream.ploc.ui.fragment.RecordBookmarkFragment;
import de.fzi.dream.ploc.ui.holder.RecordBookmarkViewHolder;
import de.fzi.dream.ploc.ui.listener.OnExpertBookmarkCardViewListener;
import de.fzi.dream.ploc.ui.listener.OnRecordBookmarkCardInteraction;

/**
 * The FeedAdapter provides a binding from data set to the PlocsViewHolders that are displayed
 * within a {@link RecyclerView}.
 *
 * @author Felix Melcher
 */
// TODO Refactoring multi select functionality and highlighting to the view holder.
public class RecordBookmarkAdapter extends RecyclerView.Adapter<RecordBookmarkViewHolder> {

    /**
     * Public class identifier tag
     */
    public static final String TAG = RecordBookmarkAdapter.class.getSimpleName();

    private List<RecordBookmark> mBookmarks = new ArrayList<>();
    private List<Collection> mCollections;
    private OnRecordBookmarkCardInteraction mClickListener;

    private boolean multiSelect = false;
    private ArrayList<RecordBookmark> selectedItems = new ArrayList<RecordBookmark>();
    private ActionMode mActionMode;
    private Context mContext;
    private ActionMode.Callback actionModeCallbacks = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.activity_bookmark_selection_action, menu);
            multiSelect = true;
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            mClickListener.onMultiSelectFinished(selectedItems);
            mode.finish();
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            multiSelect = false;
            selectedItems.clear();
            notifyDataSetChanged();
        }
    };

    /**
     * Constructor
     */
    public RecordBookmarkAdapter(OnRecordBookmarkCardInteraction listener) {
        mClickListener = listener;
    }

    @NonNull
    @Override
    public RecordBookmarkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        return new RecordBookmarkViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_item_record_bookmark, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecordBookmarkViewHolder holder, int position) {
        if (mBookmarks != null && mBookmarks.size() > 0) {
            holder.bindTo(mBookmarks.get(position), mCollections);
            holder.mCardView.findViewById(R.id.history_viewed_at).setVisibility(mBookmarks.get(position).isVisited() ? View.VISIBLE : View.GONE);
            holder.mTitleTextView.setTextColor(ContextCompat.getColor(mContext, mBookmarks.get(position).isVisited() ? R.color.colorTextMediumEmphasis : R.color.colorBlack));
            ImageViewCompat.setImageTintList(holder.mTypeView, ColorStateList.valueOf(mBookmarks.get(position).isVisited() ?
                    ContextCompat.getColor(mContext, R.color.colorTextMediumEmphasis) : ContextCompat.getColor(mContext, R.color.colorBlack)));
            setClickListener(holder, position);
        }
    }

    private void setClickListener(RecordBookmarkViewHolder holder, int position) {
        holder.mCardView.setOnClickListener(v -> {
            if (multiSelect) {
                holder.mCardView.setCardBackgroundColor(selectItem(mBookmarks.get(position)));
                mActionMode.setTitle("Selected: " + selectedItems.size());
            } else {
                mBookmarks.get(position).setVisited(true);
                holder.mTitleTextView.setTextColor(ContextCompat.getColor(mContext, R.color.colorTextMediumEmphasis));
                holder.mCardView.findViewById(R.id.history_viewed_at).setVisibility(View.VISIBLE);
                ImageViewCompat.setImageTintList(holder.mTypeView, ColorStateList.valueOf(mBookmarks.get(position).isVisited() ?
                        ContextCompat.getColor(mContext, R.color.colorTextMediumEmphasis) : ContextCompat.getColor(mContext, R.color.colorBlack)));
                mClickListener.onCardClick(mBookmarks.get(position).getId(), false);
            }
        });
        holder.mCardView.setOnLongClickListener(v -> {
            if (!multiSelect) {
                mActionMode = ((AppCompatActivity) holder.mCardView.getContext()).startSupportActionMode(actionModeCallbacks);
            }
            if (mActionMode != null) {
                holder.mCardView.setCardBackgroundColor(selectItem(mBookmarks.get(position)));
                mActionMode.setTitle("Selected: " + selectedItems.size());
            }
            return true;
        });
    }

    public void setBookmarks(List<RecordBookmark> bookmarks) {
        mBookmarks = bookmarks;
        notifyDataSetChanged();
    }

    public void setCollections(List<Collection> collections) {
        mCollections = collections;
    }

    @Override
    public int getItemCount() {
        return mBookmarks.size();
    }

    private int selectItem(RecordBookmark bookmark) {
        if (multiSelect) {
            if (selectedItems.contains(bookmark)) {
                selectedItems.remove(bookmark);
                return Color.WHITE;
            } else {
                selectedItems.add(bookmark);
                return Color.LTGRAY;
            }
        }
        return 0;
    }


}