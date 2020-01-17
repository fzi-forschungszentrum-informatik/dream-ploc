package de.fzi.dream.ploc.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.snackbar.Snackbar;

import de.fzi.dream.ploc.R;
import de.fzi.dream.ploc.data.structure.entity.RecordPreview;
import de.fzi.dream.ploc.ui.activity.MainActivity;
import de.fzi.dream.ploc.ui.adapter.FeedbackPreviewAdapter;
import de.fzi.dream.ploc.ui.dialog.NoResultsDialog;
import de.fzi.dream.ploc.ui.listener.OnRecordPreviewCardInteraction;
import de.fzi.dream.ploc.utility.Constants;
import de.fzi.dream.ploc.utility.Notification;
import de.fzi.dream.ploc.utility.network.NetworkState;
import de.fzi.dream.ploc.viewmodel.FeedbackPreviewViewModel;

/**
 * The FeedbackPreviewFragment is embedded in the {@link MainActivity} and manages the
 * RecyclerView which is holding and displaying the record data items as {@link PagedList}.
 *
 * @author Felix Melcher
 */
public class FeedbackPreviewFragment extends Fragment implements OnRecordPreviewCardInteraction {

    /**
     * Public class identifier tag
     */
    public static final String TAG = FeedbackPreviewFragment.class.getSimpleName();

    // Context
    private Context mContext;

    // ViewModel
    private FeedbackPreviewViewModel mViewModel;

    // Activity Interface
    private OnInteractionListener mListener;

    // View
    private SwipeRefreshLayout mLayout;

    // RecyclerView PagedListAdapter
    private FeedbackPreviewAdapter mAdapter;

    /**
     * Fragments require an empty public constructor.
     */
    public FeedbackPreviewFragment() {
    }

    /**
     * Create a new instance of the FeedbackPreviewFragment.
     *
     * @return FeedbackPreviewFragment
     */
    public static FeedbackPreviewFragment newInstance() {
        return new FeedbackPreviewFragment();
    }


    /*
    Lifecycle Methods
    */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
        mListener = (OnInteractionListener) context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // For inline comments see RecordPreviewFragment
        mLayout = (SwipeRefreshLayout) inflater.inflate(R.layout.fragment_preview, container, false);
        RecyclerView mRecyclerView = mLayout.findViewById(R.id.recycler_view_preview_fragment);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new FeedbackPreviewAdapter(this);
        mRecyclerView.setAdapter(mAdapter);
        mLayout.setOnRefreshListener(() -> {
            mViewModel.resetPaging();
            observeNetworkState();
        });
        mLayout.setRefreshing(true);
        return mLayout;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.fragment_feedback_preview_toolbar, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                return true;
            case R.id.action_refresh:
                mLayout.setRefreshing(true);
                mViewModel.resetPaging();
                observeNetworkState();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewModel = obtainViewModel();
        observeInterests();
        observeRecordPreviews();
        observeNetworkState();
    }

    /*
     ViewModel Methods
    */

    /**
     * Obtain the ViewModel instance responsible for getting the data from the remote or local
     * data source.
     *
     * @return RecordPreviewViewModel
     */
    private FeedbackPreviewViewModel obtainViewModel() {
        return new ViewModelProvider(this).get(FeedbackPreviewViewModel.class);
    }

    /**
     * Observe and react to changes on the record data set in the ViewModel.
     */
    private void observeRecordPreviews() {
        mViewModel.getPreviews().observe(getViewLifecycleOwner(), pagedList -> {
            if (pagedList != null) {
                mAdapter.submitList(pagedList);
                mLayout.setRefreshing(false);
            }
        });
    }

    /**
     * Observe and react to changes on new network connection state of the remote data source.
     */
    private void observeNetworkState() {
        mViewModel.getNetworkState().observe(getViewLifecycleOwner(), networkState -> {
            if (networkState.getStatus() == NetworkState.Status.SUCCESS) {
                mLayout.setRefreshing(false);
            } else if (networkState.getStatus() == NetworkState.Status.EMPTY) {
                mLayout.setRefreshing(false);
                NoResultsDialog dialog = new NoResultsDialog(getResources().getString(R.string.dialog_title_no_publications_search), getResources().getString(R.string.dialog_text_no_publications_search));
                dialog.show(getActivity().getSupportFragmentManager(), NoResultsDialog.TAG);
            }
            mAdapter.setNetworkState(networkState);
        });
    }

    /**
     * Observe and react to changes on the interests defined in the profile.
     */
    private void observeInterests() {
        mViewModel.getInterests().observe(getViewLifecycleOwner(), records -> {
            if (records != null) {
                if (records.data != null) {
                    switch (records.status) {
                        case LOADING: {
                            break;
                        }

                        case ERROR: {
                            showSnackbar(new Notification(getResources().getString(R.string.standard_error, records.message), false, false, false));
                            break;
                        }

                        case SUCCESS: {
                            mAdapter.setInterests(records.data);
                            mViewModel.compareInterestsSet(records.data);
                            break;
                        }
                    }
                }
            }
        });
        mViewModel.getNotificationMessage().observe(getViewLifecycleOwner(), message -> {
            if (message != null) {
                showSnackbar(message);
            }
        });
    }

    /*
    Fragment specific helper methods
    */

    /**
     * Show a snackbar within the current fragment.
     *
     * @param notification The notification to display in the snackbar.
     */
    private void showSnackbar(Notification notification) {
        Snackbar snackbar = Snackbar
                .make(((Activity) mContext).findViewById(R.id.coordinator_main_activity), notification.getMessage(), Snackbar.LENGTH_LONG);
        if (notification.hasReloadAction()) {
            snackbar.setAction(R.string.button_reload, view -> {
                mLayout.setRefreshing(true);
                mViewModel.resetPaging();
                observeNetworkState();
            });
        }
        snackbar.setActionTextColor(getResources().getColor(R.color.colorAccentAlternative, null));
        snackbar.show();
    }


    /*
     Interface Methods
    */
    @Override
    public void onCardLeftSwipe(RecordPreview preview, int position) {
    }

    @Override
    public void onCardRightSwipe(RecordPreview preview, int position) {
    }

    @Override
    public void onDetailButtonClick(int id, boolean isBookmark) {
        mListener.onDetailButtonClick(id, isBookmark);
    }

    @Override
    public void onFeedbackButtonClick(int id, boolean isExamined) {
        mListener.onFeedbackButtonClick(id, isExamined);
    }

    /**
     * FeedbackPreview Interaction Interface.
     */
    public interface OnInteractionListener {
        void onFeedbackButtonClick(int id, boolean isBookmarked);

        void onDetailButtonClick(int id, boolean isBookmarked);
    }
}
