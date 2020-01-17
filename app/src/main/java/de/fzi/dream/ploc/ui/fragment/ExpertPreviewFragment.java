package de.fzi.dream.ploc.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
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
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.snackbar.Snackbar;

import de.fzi.dream.ploc.R;
import de.fzi.dream.ploc.data.structure.entity.ExpertPreview;
import de.fzi.dream.ploc.ui.activity.MainActivity;
import de.fzi.dream.ploc.ui.adapter.ExpertPreviewAdapter;
import de.fzi.dream.ploc.ui.callback.ExpertPreviewSwipeCallback;
import de.fzi.dream.ploc.ui.dialog.NoResultsDialog;
import de.fzi.dream.ploc.ui.listener.OnExpertPreviewCardViewListener;
import de.fzi.dream.ploc.utility.Notification;
import de.fzi.dream.ploc.utility.network.NetworkState;
import de.fzi.dream.ploc.viewmodel.ExpertPreviewViewModel;

/**
 * The ExpertPreviewFragment is embedded in the {@link MainActivity} and manages the
 * RecyclerView which is holding and displaying the expert data items. The Fragment is also responsible
 * for the coordination of the {@link ExpertPreviewSwipeCallback}.
 *
 * @author Felix Melcher
 */
public class ExpertPreviewFragment extends Fragment implements OnExpertPreviewCardViewListener {

    /**
     * Public class identifier tag
     */
    public static final String TAG = ExpertPreviewFragment.class.getSimpleName();

    // Context
    private Context mContext;

    // ViewModel
    private ExpertPreviewViewModel mViewModel;

    // View
    private SwipeRefreshLayout mLayout;

    // RecyclerView PagedListAdapter
    private ExpertPreviewAdapter mAdapter;

    // Activity Interface
    private OnInteractionListener mListener;

    /**
     * Fragments require an empty public constructor.
     */
    public ExpertPreviewFragment() {
    }

    /**
     * Create a new instance of the ExpertBookmarkFragment.
     *
     * @return ExpertPreviewFragment
     */
    public static ExpertPreviewFragment newInstance() {
        return new ExpertPreviewFragment();
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
        mAdapter = new ExpertPreviewAdapter(this);
        mRecyclerView.setAdapter(mAdapter);

        // Add listener to the SwipeRefreshLayout
        mLayout.setOnRefreshListener(() -> {
            mViewModel.resetPaging();
            observeNotifications();
            observeNetworkStates();
        });

        // Attach the swipe callback to the RecyclerView for getting response after a user swiped
        new ItemTouchHelper(new ExpertPreviewSwipeCallback(this)).attachToRecyclerView(mRecyclerView);

        // Activate loading indicator
        mLayout.setRefreshing(true);

        return mLayout;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.activity_main_toolbar, menu);
        setUpSearchView(menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewModel = obtainViewModel();
        mViewModel.setPagingSearchTerm(null);
        observeExpertsPreviews();
        observeInterests();
        observeNotifications();
        observeNetworkStates();
    }

    /*
     Interface Methods
    */

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                return true;
            case R.id.action_refresh:
                mLayout.setRefreshing(true);
                mViewModel.resetPaging();
                observeNotifications();
                observeNetworkStates();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCardRightSwipe(ExpertPreview expertPreview) {
        mViewModel.setBookmark(expertPreview);
    }

    @Override
    public void onCardClick(int id, boolean isBookmark) {
        mListener.onExpertPreviewClicked(id, isBookmark);
    }

    /*
     ViewModel Methods
    */

    /**
     * Obtain the ViewModel instance responsible for getting the data from the remote or local
     * data source.
     *
     * @return ExpertPreviewViewModel
     */
    private ExpertPreviewViewModel obtainViewModel() {
        return new ViewModelProvider(this).get(ExpertPreviewViewModel.class);
    }

    /**
     * Observe and react to changes on the expert data set in the ViewModel.
     */
    private void observeExpertsPreviews() {
        mViewModel.getPreviews().observe(getViewLifecycleOwner(), previews -> {
            if (previews != null) {
                mAdapter.submitList(previews);
            }
        });
    }

    /**
     * Observe and react to changes on the interests defined in the profile.
     */
    private void observeInterests() {
        mViewModel.getInterests().observe(getViewLifecycleOwner(), interests -> {
            if (interests != null) {
                if (interests.data != null) {
                    switch (interests.status) {
                        case LOADING: {
                            break;
                        }
                        case ERROR: {
                            showSnackbar(new Notification(getResources().getString(R.string.standard_error, interests.message), true, false, false));
                            break;
                        }
                        case SUCCESS: {
                            mAdapter.setInterests(interests.data);
                            mViewModel.compareInterestsSet(interests.data);
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
     * Observe and react to changes on new network connection state of the remote data source.
     */
    private void observeNetworkStates() {
        mViewModel.getNetworkState().observe(getViewLifecycleOwner(), networkState -> {
            mAdapter.setNetworkState(networkState);
            if (networkState.getStatus() == NetworkState.Status.EMPTY) {
                showNoResultsDialog();
            }
            mLayout.setRefreshing(false);
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
                observeNotifications();
            });
            snackbar.setActionTextColor(getResources().getColor(R.color.colorAccentAlternative, null));
        } else if (notification.hasUndoAction()) {
            snackbar.setAction(R.string.button_undo, view -> mViewModel.undoLastBookmark());
            snackbar.setActionTextColor(getResources().getColor(R.color.colorAccentAlternative, null));
        }
        snackbar.show();
        mViewModel.clearNotificationMessage();
    }

    /**
     * Initialize the expandable {@link SearchView} within the ActionBar, which is started after the
     * search icon in the ActionBar is clicked.
     *
     * @param menu The menu where the search view is embedded in.
     */
    private void setUpSearchView(Menu menu) {
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
            observeNotifications();
            observeNetworkStates();
            return false;
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mViewModel.setPagingSearchTerm(query);
                mAdapter.notifyItemRangeRemoved(0, mAdapter.getItemCount() - 1);
                mLayout.setRefreshing(true);
                observeNotifications();
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
     * Show the {@link NoResultsDialog} if there is no data to be displayed.
     */
    private void showNoResultsDialog() {
        NoResultsDialog dialog = new NoResultsDialog(getResources()
                .getString(R.string.dialog_title_no_experts_search), getResources().getString(R.string.dialog_text_no_experts_search));
        if (getActivity() != null) {
            dialog.show(getActivity().getSupportFragmentManager(), NoResultsDialog.TAG);
        }
    }


    /**
     * Fragment Interaction Interface.
     */
    public interface OnInteractionListener {
        /**
         * Triggered when a ExpertBookmarkCardView is clicked to open the corresponding detail view.
         *
         * @param id The identifier of the ExpertBookmark record.
         */
        void onExpertPreviewClicked(int id, boolean isBookmarked);
    }

}
