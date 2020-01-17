package de.fzi.dream.ploc.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.snackbar.Snackbar;

import de.fzi.dream.ploc.R;
import de.fzi.dream.ploc.data.structure.entity.RecordPreview;
import de.fzi.dream.ploc.ui.activity.MainActivity;
import de.fzi.dream.ploc.ui.adapter.RecordPreviewAdapter;
import de.fzi.dream.ploc.ui.callback.RecordPreviewSwipeCallback;
import de.fzi.dream.ploc.ui.dialog.NoResultsDialog;
import de.fzi.dream.ploc.ui.listener.OnRecordPreviewCardInteraction;
import de.fzi.dream.ploc.utility.Notification;
import de.fzi.dream.ploc.utility.network.NetworkState;
import de.fzi.dream.ploc.viewmodel.RecordPreviewViewModel;

/**
 * The RecordPreviewFragment is embedded in the {@link MainActivity} and manages the
 * RecyclerView which is holding and displaying the record data items as {@link PagedList}.
 * The Fragment is also responsible for the coordination of the {@link RecordPreviewSwipeCallback}.
 *
 * @author Felix Melcher
 */
public class RecordPreviewFragment extends Fragment implements OnRecordPreviewCardInteraction {

    /**
     * Public class identifier tag
     */
    public static final String TAG = RecordPreviewFragment.class.getSimpleName();

    // Context
    private Context mContext;

    // View
    private SwipeRefreshLayout mLayout;

    // ViewModel
    private RecordPreviewViewModel mViewModel;

    // RecyclerView PagedListAdapter
    private RecordPreviewAdapter mAdapter;

    // Activity Interface
    private OnInteractionListener mListener;

    /**
     * Fragments require an empty public constructor.
     */
    public RecordPreviewFragment() {
    }

    /**
     * Create a new instance of the RecordPreviewFragment.
     *
     * @return RecordPreviewFragment
     */
    public static RecordPreviewFragment newInstance() {
        return new RecordPreviewFragment();
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

        // Init the view
        mLayout = (SwipeRefreshLayout) inflater.inflate(R.layout.fragment_preview, container, false);
        RecyclerView mRecyclerView = mLayout.findViewById(R.id.recycler_view_preview_fragment);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Add paging adapter to the RecyclerView
        mAdapter = new RecordPreviewAdapter(this);

        // Add listener to the SwipeRefreshLayout
        mRecyclerView.setAdapter(mAdapter);
        mLayout.setOnRefreshListener(() -> {
            mViewModel.resetPaging();
            observeNetworkStates();
        });

        // Attach the swipe callback to the RecyclerView for getting response after a user swiped
        new ItemTouchHelper(new RecordPreviewSwipeCallback(this)).attachToRecyclerView(mRecyclerView);

        // Activate loading indicator
        mLayout.setRefreshing(true);

        return mLayout;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.activity_main_toolbar, menu);
        setupSearchView(menu);
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
                observeNetworkStates();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewModel = obtainViewModel();
        mViewModel.setPagingSearchTerm(null);
        observeInterests();
        observeNotifications();
        observeRecordPreviews();
        observeNetworkStates();
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
    private RecordPreviewViewModel obtainViewModel() {
        return new ViewModelProvider(this).get(RecordPreviewViewModel.class);
    }

    /**
     * Observe and react to changes on the record data set in the ViewModel.
     */
    private void observeRecordPreviews() {
        mViewModel.getPreviews().observe(getViewLifecycleOwner(), pagedList -> {
            if (pagedList != null) {
                mAdapter.submitList(pagedList);
            }
        });
    }

    /**
     * Observe and react to changes on new network connection state of the remote data source.
     */
    private void observeNetworkStates() {
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

    }

    /**
     * Observe and react to changes on new notification messages in the ViewModel.
     */
    private void observeNotifications() {
        mViewModel.getNotificationMessage().observe(getViewLifecycleOwner(), message -> {
            if (message != null) {
                showSnackbar(message);
            }
        });
    }

    /**
     * Observe and react to changes on the record data set in the ViewModel. Its triggered with a delay to wait
     * for interests to be loaded and passed to the adapter for highlighting the matching ones.
     */
    private void observeRecordsWithDelay() {
        Runnable r = this::observeRecordPreviews;
        Handler mHandler = new Handler();
        mHandler.postDelayed(r, 500);
    }


    /*
    Fragment specific helper methods
    */

    /**
     * Initialize the expandable {@link SearchView} within the ActionBar, which is started after the
     * search icon in the ActionBar is clicked.
     *
     * @param menu The menu where the search view is embedded in.
     */
    private void setupSearchView(Menu menu) {
        // Inflate the menu item of the search view.
        MenuItem searchItem = menu.findItem(R.id.action_search);
        // Register the click listener on the menu item
        searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return false;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                mViewModel.setPagingSearchTerm(null);
                mAdapter.notifyItemRangeRemoved(0, mAdapter.getItemCount() - 1);
                return false;
            }
        });
        // Define the ActionView and its listeners.
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setIconifiedByDefault(true);
        searchView.setOnCloseListener(() -> {
            mViewModel.setPagingSearchTerm(null);
            mAdapter.notifyItemRangeRemoved(0, mAdapter.getItemCount() - 1);
            mLayout.setRefreshing(true);
            observeNetworkStates();
            return false;
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mViewModel.setPagingSearchTerm(query);
                mAdapter.notifyItemRangeRemoved(0, mAdapter.getItemCount() - 1);
                mLayout.setRefreshing(true);
                observeNetworkStates();
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return newText.isEmpty();
            }
        });
    }

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
                observeNetworkStates();
            });
        } else if (notification.hasUndoAction()) {
            snackbar.setAction(R.string.button_undo, view -> {
                if (notification.isDislike()) {
                    mViewModel.undoLastDisinterest();
                } else {
                    mViewModel.undoLastBookmark();
                }
                observeNetworkStates();
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
        mViewModel.setDisinterest(preview);
    }

    @Override
    public void onCardRightSwipe(RecordPreview preview, int position) {
        mViewModel.setBookmark(preview);
    }

    @Override
    public void onDetailButtonClick(int id, boolean isBookmark) {
        mListener.onRecordPreviewClick(id, isBookmark);
    }

    @Override
    public void onFeedbackButtonClick(int id, boolean isExamined) {

    }

    public interface OnInteractionListener {
        void onRecordPreviewClick(int id, boolean isBookmarked);
    }
}
