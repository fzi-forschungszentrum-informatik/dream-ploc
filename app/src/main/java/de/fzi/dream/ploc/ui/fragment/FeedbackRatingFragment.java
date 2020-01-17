package de.fzi.dream.ploc.ui.fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;

import de.fzi.dream.ploc.R;
import de.fzi.dream.ploc.data.structure.Feedback;
import de.fzi.dream.ploc.ui.activity.FeedbackActivity;
import de.fzi.dream.ploc.utility.Constants;
import de.fzi.dream.ploc.utility.Notification;
import de.fzi.dream.ploc.viewmodel.FeedbackPreviewViewModel;

/**
 * The FeedbackRatingFragment is embedded in the {@link FeedbackActivity} and displays the rating page
 * for writing a feedback for a specific paper.
 *
 * @author Felix Melcher
 */
public class FeedbackRatingFragment extends Fragment {

    /**
     * Public class identifier tag
     */
    public static final String TAG = FeedbackRatingFragment.class.getSimpleName();

    // Parameters
    private int mRecordID;

    // ViewModel
    private FeedbackPreviewViewModel mViewModel;

    // View
    private CheckBox mRelevance;
    private CheckBox mPresentation;
    private CheckBox mMethodology;

    /**
     * Fragments require an empty public constructor.
     */
    public FeedbackRatingFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param recordID ID of the record to write a feedback for.
     * @return A new instance of fragment FeedbackRatingFragment.
     */
    public static FeedbackRatingFragment newInstance(int recordID) {
        FeedbackRatingFragment fragment = new FeedbackRatingFragment();
        Bundle args = new Bundle();
        args.putInt(Constants.EXTRA_ID, recordID);
        fragment.setArguments(args);
        return fragment;
    }

    /*
     *  Lifecycle Methods
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mRecordID = getArguments().getInt(Constants.EXTRA_ID);
        }
        setHasOptionsMenu(true);
        mViewModel = obtainViewModel();
        observeNotifications();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_feedback_rating, container, false);
        MaterialButton button = rootView.findViewById(R.id.button_save_feedback_rating_fragment);
        mRelevance = rootView.findViewById(R.id.check_box_relevance_rating_fragment);
        mPresentation = rootView.findViewById(R.id.check_box_presentation_rating_fragment);
        mMethodology = rootView.findViewById(R.id.check_box_methodology_rating_fragment);
        button.setOnClickListener(view1 -> mViewModel.createFeedback(new Feedback(mRecordID, mRelevance.isChecked(), mPresentation.isChecked(), mMethodology.isChecked())));
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.activity_feedback_rating_toolbar, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    /*
     *  ViewModel Methods
     */

    /**
     * Obtain the ViewModel instance responsible for getting the data from the remote or local
     * data source.
     *
     * @return FeedbackPreviewViewModel
     */
    private FeedbackPreviewViewModel obtainViewModel() {
        return new ViewModelProvider(this).get(FeedbackPreviewViewModel.class);
    }

    /**
     * Observe and react to new notifications form the ViewModel.
     */
    private void observeNotifications() {
        mViewModel.getNotificationMessage().observe(this, message -> {
            if (message != null) {
                showSnackbar(message);
            }
        });
    }

    /*
     *  Fragment specific helper methods
     */

    /**
     * Show a snackbar within the current fragment.
     *
     * @param notification The notification to display in the snackbar.
     */
    private void showSnackbar(Notification notification) {
        Snackbar snackbar = Snackbar
                .make((getActivity()).findViewById(R.id.feedback_rating_layout), notification.getMessage(), Snackbar.LENGTH_SHORT);
        snackbar.addCallback(new Snackbar.Callback() {
            @Override
            public void onDismissed(Snackbar snackbar, int event) {
                getActivity().onBackPressed();
            }

            @Override
            public void onShown(Snackbar snackbar) {
            }
        });
        snackbar.show();
    }

}
