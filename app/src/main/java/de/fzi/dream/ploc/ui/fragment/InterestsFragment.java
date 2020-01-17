package de.fzi.dream.ploc.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.shape.MaterialShapeDrawable;
import com.google.android.material.snackbar.Snackbar;

import de.fzi.dream.ploc.R;
import de.fzi.dream.ploc.data.structure.Subject;
import de.fzi.dream.ploc.data.structure.entity.Interest;
import de.fzi.dream.ploc.ui.activity.InterestsActivity;
import de.fzi.dream.ploc.utility.Notification;
import de.fzi.dream.ploc.viewmodel.InterestsViewModel;

/**
 * The InterestsFragment is embedded in the {@link InterestsActivity} and manages the
 * ArrayAdapter which is responsible for displaying the interests in a chip group.
 *
 * @author Felix Melcher
 */
public class InterestsFragment extends Fragment {

    /**
     * Public class identifier tag
     */
    public static final String TAG = InterestsFragment.class.getSimpleName();

    // ViewModel
    private InterestsViewModel mViewModel;

    // Activity Context
    private Context mContext;

    // View
    private AutoCompleteTextView mSubjectsInput;
    private ChipGroup mSubjectsChosen;
    private ProgressBar mProgressBar;
    private TextView mRecordCount;

    // AutoComplete Adapter
    private ArrayAdapter<Subject> mAdapter;

    // Activity Interface
    private OnInteractionListener mListener;

    /**
     * Fragments require an empty public constructor.
     */
    public InterestsFragment() {
    }

    /**
     * Create a new instance of the ExpertDetailFragment.
     *
     * @return InterestsFragment
     */
    public static InterestsFragment newInstance() {
        return new InterestsFragment();
    }


    /*
     Lifecycle Methods
    */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
        mListener = (OnInteractionListener) mContext;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_interest, container, false);
        mSubjectsInput = view.findViewById(R.id.auto_complete_search_interest_fragment);
        mSubjectsChosen = view.findViewById(R.id.chip_group_subjects_interest_fragment);
        mProgressBar = view.findViewById(R.id.progress_bar_interest_fragment);
        mRecordCount = view.findViewById(R.id.text_view_result_count_interest_fragment);
        setUpAutoComplete();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewModel = obtainViewModel();
        observeSubjects();
        observeRecordCount();
        observeProfileInterests();
        observeNotifications();
    }

    /*
     ViewModel Methods
    */

    /**
     * Obtain the ViewModel instance responsible for getting the data from the remote or local
     * data source.
     *
     * @return InterestsViewModel
     */
    private InterestsViewModel obtainViewModel() {
        return new ViewModelProvider(this).get(InterestsViewModel.class);
    }

    /**
     * Observe and react to changes on the predefined subjects data set in the ViewModel.
     */
    private void observeSubjects() {
        mViewModel.getSubjects().observe(getViewLifecycleOwner(), subjects -> {
            if (subjects.data != null) {
                mAdapter.clear();
                mAdapter.addAll(subjects.data);
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * Observe and react to changes on the notifications.
     */
    private void observeNotifications() {
        mViewModel.getNotificationMessage().observe(getViewLifecycleOwner(), notification -> {
            if (notification != null) {
                showSnackbar(notification);
            }
        });
    }

    /**
     * Observe and react to changes on the current record count matching the defined interests set.
     */
    private void observeRecordCount() {
        mViewModel.getRecordCount().observe(getViewLifecycleOwner(), recordCount -> {
            if (recordCount != null) {
                mRecordCount.setText(String.valueOf(recordCount));
            }
        });
    }

    /**
     * Observe and react to changes on the interests defined in the profile.
     */
    private void observeProfileInterests() {
        mViewModel.getInterests().observe(getViewLifecycleOwner(), interests -> {
            if (interests != null) {
                if (interests.data != null) {
                    switch (interests.status) {
                        case LOADING: {
                            mProgressBar.setVisibility(View.VISIBLE);
                            break;
                        }

                        case ERROR: {
                            showSnackbar(new Notification(getResources().getString(R.string.standard_error, interests.message), false, false, false));
                            break;
                        }

                        case SUCCESS: {
                            mProgressBar.setVisibility(View.GONE);
                            mSubjectsChosen.removeAllViews();
                            mListener.onInterestAdded(interests.data.size());
                            for (Interest interest : interests.data) {
                                mSubjectsChosen.addView(createInterestChip(interest.getKeyword(), interest.getId()));
                            }
                            break;
                        }
                    }
                }
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
                .make(((Activity) mContext).findViewById(R.id.linear_layout_interest_fragment), notification.getMessage(), Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    /**
     * Set up the AutoCompleteTexView, including the ArrayAdapter for displaying the subjects and
     * the ItemClickListener for adding interests to the backend and database.
     */
    private void setUpAutoComplete() {
        mAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_dropdown_item_1line);
        mSubjectsInput.setAdapter(mAdapter);
        mSubjectsInput.setOnItemClickListener((adapterView, view, position, rowID) -> {
            // Show the progress spinner
            mProgressBar.setVisibility(View.VISIBLE);
            // Update interest profile
            mViewModel.setInterest((Subject) adapterView.getItemAtPosition(position));
            // Clear the AutoCompleteTextView
            mSubjectsInput.getText().clear();
            // Close the keyboard on item click
            InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(mSubjectsInput.getWindowToken(), 0);
        });
    }

    /**
     * Create and add an {@link Chip} to the {@link ChipGroup} for displaying the interests.
     *
     * @param name The name displayed on the chip.
     * @param id   The identifier to identify the interest when deleting one.
     */
    private Chip createInterestChip(String name, int id) {
        Chip chip = new Chip(mContext);
        chip.setText(name);
        chip.setId(id);
        chip.setCloseIconVisible(true);
        ((MaterialShapeDrawable) chip.getBackgroundDrawable()).setCornerSize(0);
        chip.setOnCloseIconClickListener(view -> {
            mProgressBar.setVisibility(View.VISIBLE);
            mViewModel.deleteInterest(view.getId());
            mSubjectsChosen.removeView(view);
            mListener.onInterestRemoved(mSubjectsChosen.getChildCount());
        });
        return chip;
    }

    /**
     * Fragment Interaction Interface.
     */
    public interface OnInteractionListener {
        void onInterestAdded(int totalCount);

        void onInterestRemoved(int totalCount);
    }
}
