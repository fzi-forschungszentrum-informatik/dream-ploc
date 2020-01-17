package de.fzi.dream.ploc.ui.adapter;

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
import de.fzi.dream.ploc.data.structure.entity.Interest;
import de.fzi.dream.ploc.data.structure.entity.RecordPreview;
import de.fzi.dream.ploc.ui.holder.NetworkStateViewHolder;
import de.fzi.dream.ploc.ui.holder.RecordPreviewViewHolder;
import de.fzi.dream.ploc.ui.listener.OnRecordPreviewCardInteraction;
import de.fzi.dream.ploc.utility.network.NetworkState;

/**
 * The RecordPreviewAdapter provides a binding from data set to the {@link RecordPreviewViewHolder}
 * which is displayed within a {@link RecyclerView}.
 *
 * @author Felix Melcher
 */
public class RecordPreviewAdapter extends PagedListAdapter<RecordPreview, RecyclerView.ViewHolder> {

    /**
     * Public class identifier tag
     */
    public static final String TAG = RecordPreviewAdapter.class.getSimpleName();

    private static DiffUtil.ItemCallback<RecordPreview> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<RecordPreview>() {
                @Override
                public boolean areItemsTheSame(RecordPreview oldPreview, RecordPreview newPreview) {
                    return newPreview.getId() == oldPreview.getId();
                }

                @Override
                public boolean areContentsTheSame(RecordPreview oldPreview,
                                                  RecordPreview newPreview) {
                    return newPreview.getId() == oldPreview.getId();
                }
            };
    private final OnRecordPreviewCardInteraction mListener;
    private NetworkState mNetworkState;
    private List<Interest> mInterests = new ArrayList<>();

    /**
     * Constructor
     *
     * @param listener the event listener waiting for interactions on the views displayed by
     *                 this adapter. The listener is passed through to the view holder.
     */
    public RecordPreviewAdapter(OnRecordPreviewCardInteraction listener) {
        super(DIFF_CALLBACK);
        mListener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        if (viewType == R.layout.recycler_view_item_record_preview) {
            return new RecordPreviewViewHolder(layoutInflater.inflate(R.layout.recycler_view_item_record_preview, parent, false), mInterests);
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
                ((RecordPreviewViewHolder) holder).bindTo(getItem(position), mListener);
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
     * for highlighting the matching keywords. Clear and add to avoid NullPointer Exception if
     * the record request is ready before the interest request.
     *
     * @param i The user defined interests.
     */
    public void setInterests(List<Interest> i) {
        mInterests.clear();
        mInterests.addAll(i);
    }
}