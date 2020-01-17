package de.fzi.dream.ploc.ui.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.paging.PagedList;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import de.fzi.dream.ploc.R;
import de.fzi.dream.ploc.data.structure.entity.FeedbackPreview;
import de.fzi.dream.ploc.data.structure.entity.Interest;
import de.fzi.dream.ploc.ui.holder.FeedbackPreviewViewHolder;
import de.fzi.dream.ploc.ui.holder.NetworkStateViewHolder;
import de.fzi.dream.ploc.ui.listener.OnRecordPreviewCardInteraction;
import de.fzi.dream.ploc.utility.Constants;
import de.fzi.dream.ploc.utility.network.NetworkState;

/**
 * The FeedAdapter provides a binding from data set to the PlocsViewHolders that are displayed
 * within a {@link RecyclerView}.
 *
 * @author Felix Melcher
 */
public class FeedbackPreviewAdapter extends PagedListAdapter<FeedbackPreview, RecyclerView.ViewHolder> {

    /**
     * Public class identifier tag
     */
    public static final String TAG = ExpertBookmarkAdapter.class.getSimpleName();
    private static DiffUtil.ItemCallback<FeedbackPreview> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<FeedbackPreview>() {
                @Override
                public boolean areItemsTheSame(FeedbackPreview oldPreview, FeedbackPreview newPreview) {
                    return newPreview.getId() == oldPreview.getId();
                }

                @Override
                public boolean areContentsTheSame(FeedbackPreview oldPreview,
                                                  FeedbackPreview newPreview) {
                    return newPreview.getId() == oldPreview.getId();
                }
            };
    private final OnRecordPreviewCardInteraction mListener;
    private NetworkState mNetworkState;
    private List<Interest> mInterests = new ArrayList<>();

    /**
     * Constructor
     *
     * @param listener the event listener waiting for interactions within this adapter.
     */
    public FeedbackPreviewAdapter(OnRecordPreviewCardInteraction listener) {
        super(DIFF_CALLBACK);
        mListener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        if (viewType == R.layout.recycler_view_item_record_preview) {
            return new FeedbackPreviewViewHolder(layoutInflater.inflate(R.layout.recycler_view_item_feedback_preview, parent, false), mInterests);
        } else if (viewType == R.layout.recycler_view_item_network_state) {
            View network_view = layoutInflater.inflate(R.layout.recycler_view_item_network_state, parent, false);
            return new NetworkStateViewHolder(network_view);
        } else {
            throw new IllegalArgumentException("unknown view type");
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case R.layout.recycler_view_item_record_preview:
                FeedbackPreviewViewHolder viewHolder = (FeedbackPreviewViewHolder) holder;
                FeedbackPreview preview = getItem(position);
                if (preview != null) {
                    viewHolder.bindTo(preview, mListener);
                }
                break;
            case R.layout.recycler_view_item_network_state:
                ((NetworkStateViewHolder) holder).bindTo(mNetworkState);
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (hasExtraRow() && position == getItemCount() - 1) {
            return R.layout.recycler_view_item_network_state;
        } else {
            return R.layout.recycler_view_item_record_preview;
        }
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getId();
    }

    /**
     * Set the current loading state of the {@link PagedList} to add or remove the loading indicator
     * at the last position of the {@link RecyclerView}.
     *
     * @param newNetworkState The current network state of the last request.
     */
    public void setNetworkState(NetworkState newNetworkState) {
        NetworkState previousState = mNetworkState;
        boolean previousExtraRow = hasExtraRow();
        mNetworkState = newNetworkState;
        boolean newExtraRow = hasExtraRow();
        if (previousExtraRow != newExtraRow) {
            if (previousExtraRow) {
                notifyItemRemoved(getItemCount());
            } else {
                notifyItemInserted(getItemCount());
            }
        } else if (newExtraRow && previousState != newNetworkState) {
            notifyItemChanged(getItemCount() - 1);
        }
    }

    /**
     * Check if there is an extra row added to the recycler view.
     *
     * @return boolean True if there is a pending loading request, false if not.
     */
    private boolean hasExtraRow() {
        return mNetworkState != null && mNetworkState != NetworkState.LOADED;
    }

    /**
     * Submit the user defined interests to the adapter to passed to each ViewHolder
     * for highlighting the matching keywords.
     *
     * @param i The user defined interests.
     */
    public void setInterests(List<Interest> i) {
        Log.d(Constants.LOG_TAG,  "new interests");
        mInterests = i;
    }

}