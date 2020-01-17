package de.fzi.dream.ploc.ui.holder;

import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import de.fzi.dream.ploc.R;
import de.fzi.dream.ploc.utility.network.NetworkState;

/**
 * The NetworkStateViewHolder is responsible for displaying the current network loading state
 * at the end of an RecyclerView.
 */
public class NetworkStateViewHolder extends RecyclerView.ViewHolder {

    /**
     * Public class identifier tag
     */
    public static final String TAG = NetworkStateViewHolder.class.getSimpleName();

    private final ProgressBar mProgressBar;
    private final TextView mErrorMessage;

    public NetworkStateViewHolder(View itemView) {
        super(itemView);
        mProgressBar = itemView.findViewById(R.id.progress_bar);
        mErrorMessage = itemView.findViewById(R.id.error_msg);
    }

    public void bindTo(NetworkState networkState) {
        if (networkState != null && networkState.getStatus() == NetworkState.Status.RUNNING) {
            mProgressBar.setVisibility(View.VISIBLE);
        } else {
            mProgressBar.setVisibility(View.GONE);
        }
        if (networkState != null && networkState.getStatus() == NetworkState.Status.FAILED) {
            mErrorMessage.setVisibility(View.VISIBLE);
            mErrorMessage.setText(networkState.getMsg());
        } else {
            mErrorMessage.setVisibility(View.GONE);
        }
    }
}