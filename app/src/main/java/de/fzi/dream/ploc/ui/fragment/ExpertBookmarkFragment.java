package de.fzi.dream.ploc.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import de.fzi.dream.ploc.R;
import de.fzi.dream.ploc.data.structure.entity.ExpertBookmark;
import de.fzi.dream.ploc.ui.activity.ExpertBookmarkActivity;
import de.fzi.dream.ploc.ui.adapter.ExpertBookmarkAdapter;
import de.fzi.dream.ploc.ui.callback.ExpertBookmarkSwipeCallback;
import de.fzi.dream.ploc.ui.dialog.NoResultsDialog;
import de.fzi.dream.ploc.ui.listener.OnExpertBookmarkCardViewListener;
import de.fzi.dream.ploc.utility.Constants;
import de.fzi.dream.ploc.viewmodel.ExpertBookmarkViewModel;

/**
 * The ExpertBookmarkFragment is embedded in the {@link ExpertBookmarkActivity} and manages the
 * RecyclerView which is holding and displaying the data item.
 *
 * @author Felix Melcher
 */
public class ExpertBookmarkFragment extends Fragment implements OnExpertBookmarkCardViewListener {

    /**
     * Public class identifier tag
     */
    public static final String TAG = ExpertBookmarkFragment.class.getSimpleName();

    // Context
    private Context mContext;

    // ViewModel
    private ExpertBookmarkViewModel mViewModel;

    // Parameters
    private List<ExpertBookmark> mBookmarks = new ArrayList<>();

    // RecyclerView Adapter
    private ExpertBookmarkAdapter mAdapter;

    // Activity Interface
    private OnInteractionListener mListener;

    // Sharing Intent
    private Intent mShareIntent = setUpShareIntent();

    /**
     * Fragments require an empty public constructor.
     */
    public ExpertBookmarkFragment() {

    }

    /**
     * Create a new instance of the ExpertBookmarkFragment.
     *
     * @return ExpertBookmarkFragment
     */
    public static ExpertBookmarkFragment newInstance() {
        return new ExpertBookmarkFragment();
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Set up view
        SwipeRefreshLayout view = (SwipeRefreshLayout) inflater.inflate(R.layout.fragment_bookmark, container, false);
        RecyclerView mRecyclerView = view.findViewById(R.id.recycler_view_bookmark_fragment);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        // Add list adapter and listener to view
        mAdapter = new ExpertBookmarkAdapter(mBookmarks, this);
        mRecyclerView.setAdapter(mAdapter);

        // Attach the swipe callback to the RecyclerView for getting response after a user swiped
        new ItemTouchHelper(new ExpertBookmarkSwipeCallback(this)).attachToRecyclerView(mRecyclerView);
        view.setOnRefreshListener(() -> {

            view.setRefreshing(false);
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewModel = obtainViewModel();
        observeBookmarks();
        observeNotifications();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.activity_bookmark_toolbar, menu);
    }

    /*
        ViewModel Methods
    */

    /**
     * Obtain the ViewModel instance responsible for getting the data from the remote or local
     * data source.
     *
     * @return ExpertBookmarkViewModel
     */
    private ExpertBookmarkViewModel obtainViewModel() {
        return new ViewModelProvider(this).get(ExpertBookmarkViewModel.class);
    }

    /**
     * Observe and react to changes on the bookmarks data set in the ViewModel.
     */
    private void observeBookmarks() {
        mViewModel.getBookmarks().observe(getViewLifecycleOwner(), bookmarks -> {
            if (bookmarks != null) {
                if (bookmarks.data != null) {
                    switch (bookmarks.status) {
                        case LOADING: {
                            break;
                        }

                        case ERROR: {
                            showSnackbar(false, "ERROR: " + bookmarks.message);
                            break;
                        }

                        case SUCCESS: {
                            if (bookmarks.data.isEmpty()) {
                                showNoResultsDialog();
                            } else {
                                buildShareIntentText(bookmarks.data);
                                mAdapter.setBookmarks(bookmarks.data);
                                mAdapter.notifyDataSetChanged();
                            }
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
                showSnackbar(true, message);
                mAdapter.notifyDataSetChanged();
            }
        });
    }


    /*
        Fragment specific helper methods
    */

    /**
     * Show a snackbar within the current fragment.
     *
     * @param hasUndoOption true if the undo action should be displayed on the snackbar.
     * @param message       the String message that will be displayed on the snackbar.
     */
    private void showSnackbar(boolean hasUndoOption, String message) {
        Snackbar snackbar = Snackbar
                .make(((Activity) mContext).findViewById(R.id.coordinator_bookmark_activity), message, Snackbar.LENGTH_LONG);
        if (hasUndoOption) {
            snackbar.setAction(R.string.button_undo, view -> mViewModel.undoDeleteBookmark());
            snackbar.setActionTextColor(getResources().getColor(R.color.colorAccentAlternative, null));
        }
        snackbar.show();
    }

    /**
     * Show the {@link NoResultsDialog} if there is no data to be displayed.
     */
    private void showNoResultsDialog() {
        NoResultsDialog dialog = new NoResultsDialog(getResources()
                .getString(R.string.dialog_title_no_experts_saved), getResources().getString(R.string.dialog_text_no_experts_saved));
        if (getActivity() != null) {
            dialog.show(getActivity().getSupportFragmentManager(), NoResultsDialog.TAG);
        }
    }

    /**
     * Initialize an intent to show the sharing dialog.
     *
     * @return Intent
     */
    private Intent setUpShareIntent() {
        return new Intent()
                .setAction(Intent.ACTION_SEND)
                .setType(Constants.INTENT_SHARE_TYPE)
                .putExtra(Intent.EXTRA_SUBJECT, Constants.INTENT_SHARE_SUBJECT_EXPERT);
    }

    /**
     * Add the text that shall be shared to the initialized intent.
     */
    private void buildShareIntentText(List<ExpertBookmark> bookmarks) {
        StringBuilder sharingText = new StringBuilder(mContext.getResources().getString(R.string.message_sharing_intent));
        for (ExpertBookmark bookmark : bookmarks) {
            sharingText.append(bookmark.getSharingText()).append("\n\n");
        }
        mShareIntent.putExtra(Intent.EXTRA_TEXT, sharingText.toString());
    }

    /*
        Interface Methods
    */
    @Override
    public void onCardClick(int id) {
        mListener.onExpertBookmarkClicked(id);
    }

    @Override
    public void onCardLeftSwipe(ExpertBookmark expertBookmark, int position) {
        mViewModel.deleteBookmark(expertBookmark);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_share_bookmark) {
            startActivity(Intent.createChooser(mShareIntent, getString(R.string.action_share)));
            return true;
        }
        return super.onOptionsItemSelected(item);
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
        void onExpertBookmarkClicked(int id);
    }
}
