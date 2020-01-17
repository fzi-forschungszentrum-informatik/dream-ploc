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
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
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
import de.fzi.dream.ploc.data.structure.entity.RecordBookmark;
import de.fzi.dream.ploc.ui.activity.RecordBookmarkActivity;
import de.fzi.dream.ploc.ui.adapter.RecordBookmarkAdapter;
import de.fzi.dream.ploc.ui.callback.RecordBookmarkSwipeCallback;
import de.fzi.dream.ploc.ui.dialog.AddCollectionDialog;
import de.fzi.dream.ploc.ui.dialog.AssignCollectionDialog;
import de.fzi.dream.ploc.ui.dialog.NoResultsDialog;
import de.fzi.dream.ploc.ui.listener.OnRecordBookmarkCardInteraction;
import de.fzi.dream.ploc.utility.Constants;
import de.fzi.dream.ploc.viewmodel.RecordBookmarkViewModel;

import static de.fzi.dream.ploc.utility.Constants.EXTRA_FILTER_ID;
import static de.fzi.dream.ploc.utility.Constants.EXTRA_FILTER_NAME;

/**
 * The RecordBookmarkFragment is embedded in the {@link RecordBookmarkActivity} and manages the
 * RecyclerView which is holding and displaying the data item.
 *
 * @author Felix Melcher
 */
public class RecordBookmarkFragment extends Fragment implements OnRecordBookmarkCardInteraction,
        AssignCollectionDialog.OnInteractionListener, AddCollectionDialog.OnInteractionListener {

    /**
     * Public class identifier tag
     */
    public static final String TAG = RecordBookmarkFragment.class.getSimpleName();

    // Context
    private Context mContext;

    // ViewModel
    private RecordBookmarkViewModel mViewModel;

    // Parameters
    private int mFilterID;
    private String mFilterName;

    // View
    private RecyclerView mRecyclerView;
    private DialogFragment mAssignCollectionsDialog;

    // Interfaces
    private RecordBookmarkAdapter mAdapter;
    private OnInteractionListener mListener;

    // Sharing Intent
    private Intent mShareIntent = setUpShareIntent();

    /**
     * Fragments require an empty public constructor.
     */
    public RecordBookmarkFragment() {
    }

    /**
     * Create a new instance of the RecordBookmarkFragment.
     *
     * @return RecordBookmarkFragment
     */
    public static RecordBookmarkFragment newInstance(int filterID, String filterName) {
        RecordBookmarkFragment fragment = new RecordBookmarkFragment();
        Bundle args = new Bundle();
        args.putInt(EXTRA_FILTER_ID, filterID);
        args.putString(EXTRA_FILTER_NAME, filterName);
        fragment.setArguments(args);
        return fragment;
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
        Bundle args = getArguments();
        if (args != null) {
            mFilterID = args.getInt(EXTRA_FILTER_ID, 0);
            mFilterName = args.getString(EXTRA_FILTER_NAME);
        }
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Set up view
        SwipeRefreshLayout view = (SwipeRefreshLayout) inflater.inflate(R.layout.fragment_bookmark, container, false);
        mRecyclerView = view.findViewById(R.id.recycler_view_bookmark_fragment);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        // Add adapter and listener to view
        mAdapter = new RecordBookmarkAdapter(this);
        mRecyclerView.setAdapter(mAdapter);

        // Attach the swipe callback to the RecyclerView for getting response after a user swiped
        new ItemTouchHelper(new RecordBookmarkSwipeCallback(this)).attachToRecyclerView(mRecyclerView);
        view.setOnRefreshListener(() -> view.setRefreshing(false));

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewModel = obtainViewModel();
        observerCollections();
        observeNotifications();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.activity_bookmark_toolbar, menu);
    }

    /*
       ViewModel Methods
    */
    private RecordBookmarkViewModel obtainViewModel() {
        return new ViewModelProvider(this).get(RecordBookmarkViewModel.class);
    }

    /**
     * Observe and react to changes on the collection data set in the ViewModel.
     */
    private void observerCollections() {
        mViewModel.getCollections().observe(getViewLifecycleOwner(), collections -> {
            if (collections != null) {
                if (collections.data != null) {
                    switch (collections.status) {
                        case LOADING: {
                            break;
                        }

                        case ERROR: {
                            showSnackbar(false, "ERROR: " + collections.message);
                            break;
                        }

                        case SUCCESS: {
                            if (collections.data.size() > 0) {
                                mAdapter.setCollections(collections.data);
                                mAdapter.notifyDataSetChanged();
                                mAssignCollectionsDialog = new AssignCollectionDialog(collections.data, this);
                            }
                            observeBookmarks();
                            break;
                        }
                    }
                }
            }
        });
    }

    /**
     * Observe and react to changes on the bookmarks data set in the ViewModel.
     */
    private void observeBookmarks() {
        if (mFilterName != null) {
            mViewModel.getBookmarksByCollectionID(mFilterID).observe(getViewLifecycleOwner(), bookmarks -> {
                if (bookmarks != null) {
                    if (bookmarks.size() > 0) {
                        updateAdapter(bookmarks);
                    } else {
                        showNoResultsDialog(
                                getResources().getString(R.string.dialog_title_no_publications_collection),
                                getResources().getString(R.string.dialog_text_no_publications_collection)
                        );
                    }
                }
            });
        } else {
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
                                if (bookmarks.data.size() == 0) {
                                    showNoResultsDialog(
                                            getResources().getString(R.string.dialog_title_no_publications_saved),
                                            getResources().getString(R.string.dialog_text_no_publications_saved)
                                    );
                                }
                                updateAdapter(bookmarks.data);
                                break;
                            }
                        }
                    }
                }
            });
        }
    }

    /**
     * Observe and react to changes on new notification messages in the ViewModel.
     */
    private void observeNotifications() {
        mViewModel.getNotificationMessage().observe(getViewLifecycleOwner(), message -> {
            if (message != null) {
                showSnackbar(true, message);
            }
        });
    }

    /*
        Fragment specific helper methods
    */

    /**
     * Initialize an intent to show the sharing dialog.
     *
     * @return Intent
     */
    private Intent setUpShareIntent() {
        return new Intent()
                .setAction(Intent.ACTION_SEND)
                .setType(Constants.INTENT_SHARE_TYPE)
                .putExtra(Intent.EXTRA_SUBJECT, Constants.INTENT_SHARE_SUBJECT_RECORD);
    }

    /**
     * Update the data in the adapter after its initialization and create the text for the
     * sharing intent.
     *
     * @param bookmarks the bookmarks to be shown through the adapter.
     */
    private void updateAdapter(List<RecordBookmark> bookmarks) {
        mAdapter.setBookmarks(bookmarks);
        if (bookmarks.size() > 0) {
            StringBuilder sharingText = new StringBuilder(mContext.getResources().getString(R.string.message_sharing_intent));
            for (RecordBookmark bookmark : bookmarks) {
                sharingText.append(bookmark.getSharingText()).append("\n\n");
            }
            mShareIntent.putExtra(Intent.EXTRA_TEXT, sharingText.toString());
        }
    }

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
     *
     * @param title the title to be displayed on the dialog.
     * @param text  the text to be displayed under the title on the dialog.
     */
    private void showNoResultsDialog(String title, String text) {
        NoResultsDialog dialog = new NoResultsDialog(title, text);
        dialog.show(getActivity().getSupportFragmentManager(), NoResultsDialog.TAG);
    }

    /*
     Interface Methods
   */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_share_bookmark) {
            startActivity(Intent.createChooser(mShareIntent, getString(R.string.action_share)));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCardClick(int id, boolean isBookmark) {
        mListener.onBookmarkClicked(id);
    }

    @Override
    public void onMultiSelectFinished(ArrayList<RecordBookmark> selectedItems) {
        if (getActivity() != null) {
            FragmentManager fm = getActivity().getSupportFragmentManager();
            if (mAssignCollectionsDialog != null) {
                ((AssignCollectionDialog) mAssignCollectionsDialog).setDialogData(selectedItems);
                mAssignCollectionsDialog.show(fm, "FilterRecordPreviewDialog");
            } else {
                Fragment fragment = fm.findFragmentByTag(AddCollectionDialog.TAG);
                if (fragment != null) {
                    fm.beginTransaction().remove(fragment).commit();
                }
                AddCollectionDialog addCollectionDialog = new AddCollectionDialog(this);
                addCollectionDialog.show(fm, AddCollectionDialog.TAG);
            }
        }
    }

    @Override
    public void onCardLeftSwipe(RecordBookmark recordBookmark, int position) {
        mViewModel.deleteBookmark(recordBookmark);
        mAdapter.notifyItemRemoved(position);
    }

    @Override
    public void onCollectionsAssigned(List<Integer> bookmarkIDs, List<Integer> collectionIDs) {
        Snackbar.make(mRecyclerView, "Assigned bookmark(s) to collection(s)", Snackbar.LENGTH_LONG).show();
        mViewModel.assignCollectionToBookmarks(bookmarkIDs, collectionIDs);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onFinishAddCollectionDialog(String collectionName) {
        mViewModel.setCollection(collectionName);
    }

    public interface OnInteractionListener {
        void onBookmarkClicked(int id);
    }
}
