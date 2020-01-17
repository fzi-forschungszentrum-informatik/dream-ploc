package de.fzi.dream.ploc.ui.fragment;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import de.fzi.dream.ploc.R;
import de.fzi.dream.ploc.data.structure.ExpertDetail;
import de.fzi.dream.ploc.data.structure.TinyRecord;
import de.fzi.dream.ploc.ui.activity.ExpertDetailActivity;
import de.fzi.dream.ploc.ui.adapter.GroupedRecordListAdapter;
import de.fzi.dream.ploc.ui.component.NonScrollableListView;
import de.fzi.dream.ploc.utility.Constants;
import de.fzi.dream.ploc.utility.identicon.HashGenerator;
import de.fzi.dream.ploc.utility.identicon.IdenticonGenerator;
import de.fzi.dream.ploc.viewmodel.ExpertDetailViewModel;

import static de.fzi.dream.ploc.utility.Constants.EXTRA_ID;
import static de.fzi.dream.ploc.utility.Constants.EXTRA_IS_BOOKMARK;
import static java.util.stream.Collectors.groupingBy;

/**
 * The ExpertBookmarkFragment is embedded in the {@link ExpertDetailActivity} and manages the views
 * displaying the data set.
 *
 * @author Felix Melcher
 */
public class ExpertDetailFragment extends Fragment implements GroupedRecordListAdapter.OnInteractionListener {

    /**
     * Public class identifier tag
     */
    public static final String TAG = ExpertDetailFragment.class.getSimpleName();

    // Context
    private Context mContext;

    // Parameters
    private ExpertDetail mExpertDetail;

    // Bundle Arguments
    private boolean mIsBookmark = false;
    private int mExpertID;

    // ViewModel
    private ExpertDetailViewModel mViewModel;

    // View
    private TextView mFullName;
    private TextView mSubjects;
    private TextView mOrcidID;
    private TextView mPublicationCount;
    private TextView mLastPublicationYear;
    private TextView mAffiliation;
    private ImageView mIdenticon;
    private MenuItem mBookmark;
    private ImageView mExpandableIndicator;
    private NonScrollableListView mRecords;

    // Activity Interface
    private OnInteractionListener mListener;

    // Sharing Intent
    private Intent mShareIntent = setUpShareIntent();

    /**
     * Fragments require an empty public constructor.
     */
    public ExpertDetailFragment() {
    }

    /**
     * Create a new instance of the ExpertDetailFragment.
     *
     * @return ExpertBookmarkFragment
     */
    public static ExpertDetailFragment newInstance(int id, boolean isBookmark) {
        Bundle args = new Bundle();
        args.putInt(EXTRA_ID, id);
        args.putBoolean(EXTRA_IS_BOOKMARK, isBookmark);
        ExpertDetailFragment fragment = new ExpertDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    /*
     Lifecycle Methods
   */

    /**
     * Method for Setting the Height of the ListView dynamically.
     * Hack to fix the issue of not showing all the items of the ListView
     * when placed inside a ScrollView
     */
    private static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, ViewGroup.LayoutParams.WRAP_CONTENT));
            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
        mListener = (OnInteractionListener) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            mIsBookmark = getArguments().getBoolean(EXTRA_IS_BOOKMARK);
            mExpertID = getArguments().getInt(EXTRA_ID);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_expert_detail, container, false);
        mFullName = rootView.findViewById(R.id.text_view_name_expert_detail_fragment);
        mSubjects = rootView.findViewById(R.id.text_view_subjects_expert_detail_fragment);
        mOrcidID = rootView.findViewById(R.id.text_view_orcid_expert_detail_fragment);
        mLastPublicationYear = rootView.findViewById(R.id.text_view_last_activity_expert_detail_fragment);
        mRecords = rootView.findViewById(R.id.non_scrollable_list_records_expert_detail_fragment);
        mIdenticon = rootView.findViewById(R.id.image_view_identicon_expert_detail_fragment);
        mPublicationCount = rootView.findViewById(R.id.text_view_publication_count_expert_detail_fragment);
        mAffiliation = rootView.findViewById(R.id.text_view_affiliation_expert_detail_fragment);
        mExpandableIndicator = rootView.findViewById(R.id.image_view_expansion_indicator_expert_detail_fragment);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if (getArguments() != null) {
            mViewModel = obtainViewModel();
            observeDetails();
            observeNotifications();
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.activity_expert_detail_toolbar, menu);
        mBookmark = menu.findItem(R.id.action_bookmark);
        if (mIsBookmark) {
            mBookmark.setIcon(R.drawable.ic_star_accent_24dp);
        }
    }

    /*
        ViewModel Methods
    */

    /**
     * Bind the data from the ViewModel to the layout views of the fragment.
     *
     * @param details The data set that should be bind to the fragments view.
     */
    public void bindTo(ExpertDetail details) {
        mExpertDetail = details;
        // Set text to TextViews
        mFullName.setText(mExpertDetail.getFullName());
        mLastPublicationYear.setText(String.format(getResources().getString(R.string.hint_publication_year), mExpertDetail.getLastPublicationYear()));
        mSubjects.setText(TextUtils.join(getResources().getString(R.string.subject_delimiter), mExpertDetail.getSubjects()));
        mPublicationCount.setText(String.format(getResources().getString(R.string.hint_publication_count), mExpertDetail.getRecords().size()));
        // Generate and set identicon
        mIdenticon.setImageBitmap(IdenticonGenerator.generate(String.valueOf(mExpertDetail.getExpertID()), new HashGenerator()));
        mAffiliation.setText(String.format(getResources().getString(R.string.hint_affiliation), mExpertDetail.getAffiliation()));
        // Set ORCID ID if available
        if (mExpertDetail.getOrcidID() == null) {
            mOrcidID.setText(String.format(mContext.getResources().getString(R.string.hint_orcid_id), (mContext.getResources().getString(R.string.unknown))));
        } else {
            SpannableString content = new SpannableString(String.format(mContext.getResources().getString(R.string.hint_orcid_id), mExpertDetail.getOrcidID()));
            content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
            mOrcidID.setText(content);
            mOrcidID.setTextColor(getResources().getColor(R.color.colorAccent, null));
            mOrcidID.setOnClickListener(view -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.ORCID_URL + mExpertDetail.getOrcidID()))));
        }

        // Set up list of experts publications
        mRecords.setAdapter(setUpGroupedAdapter(mExpertDetail.getRecords()));
        setListViewHeightBasedOnChildren(mRecords);
        setExpandableTextListener();

        // Set sharing intent content
        mShareIntent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.message_sharing_intent) + mExpertDetail.getSharingText());
    }

    /**
     * Obtain the ViewModel instance responsible for getting the data from the remote or local
     * data source.
     *
     * @return ExpertBookmarkViewModel
     */
    private ExpertDetailViewModel obtainViewModel() {
        return new ViewModelProvider(this).get(ExpertDetailViewModel.class);
    }

    /**
     * Observe and react to changes on new notification messages in the ViewModel.
     */
    private void observeNotifications() {
        mViewModel.getNotificationMessage().observe(getViewLifecycleOwner(), message -> {
            if (message != null) {
                showSnackbar(message.getMessage());
            }
        });
    }

    /*
        Fragment specific helper methods
    */

    /**
     * Observe and react to changes expert details updates.
     */
    private void observeDetails() {
        mViewModel.getDetails(mExpertID).observe(getViewLifecycleOwner(), expertDetails -> {
            if (expertDetails != null) {
                bindTo(expertDetails);
            }
        });
    }

    /**
     * Show a snackbar within the current fragment.
     *
     * @param message The String message that will be displayed on the snackbar.
     */
    private void showSnackbar(String message) {
        Snackbar.make(((Activity) mContext)
                .findViewById(R.id.coordinator_detail_activity), message, Snackbar.LENGTH_LONG)
                .show();
    }

    /**
     * Group and sort the data by year.
     *
     * @param records The data set that should be sorted and grouped.
     * @return LinkedHashMap<Integer, List < TinyRecord>> The sorted and grouped data set.
     */
    private LinkedHashMap<Integer, List<TinyRecord>> groupAndSortRecords(List<TinyRecord> records) {
        Map<Integer, List<TinyRecord>> groupedByYear = records.stream()
                .collect(groupingBy(TinyRecord::getYear));
        //LinkedHashMap holds the order of insertion.
        LinkedHashMap<Integer, List<TinyRecord>> sortedByYear = new LinkedHashMap<>();
        groupedByYear.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey(Comparator.reverseOrder()))
                .forEachOrdered(x -> sortedByYear.put(x.getKey(), x.getValue()));
        return sortedByYear;
    }

    /**
     * Prepare the adapter to display the grouped data in a ListView.
     */
    private GroupedRecordListAdapter setUpGroupedAdapter(List<TinyRecord> records) {
        GroupedRecordListAdapter groupedListAdapter = new GroupedRecordListAdapter(this);
        int position = 0;
        for (Map.Entry<Integer, List<TinyRecord>> entry : groupAndSortRecords(records).entrySet()) {
            groupedListAdapter.addHeader(String.valueOf(entry.getKey()));
            position++;
            for (TinyRecord record : entry.getValue()) {
                groupedListAdapter.addItem(position, record);
                position++;
            }
        }
        return groupedListAdapter;
    }

    /**
     * Add a ClickListener to the views that should trigger the expansion of the TextView.
     */
    private void setExpandableTextListener() {
        mExpandableIndicator.setOnClickListener(v -> {
            mSubjects.callOnClick();
            switchIndicator();
        });

        mSubjects.setOnClickListener(v -> {
            cycleTextViewExpansion((TextView) v);
            switchIndicator();
        });
    }

    /**
     * Switch the drawable of the expandable indicator arrow.
     */
    private void switchIndicator() {
        Drawable drawable;
        if (mSubjects.getMaxLines() != Constants.EXPERT_DETAILS_COLLAPSED_MAX_LINES) {
            drawable = AppCompatResources.getDrawable(mContext, R.drawable.ic_keyboard_arrow_down_black_24dp);
        } else {
            drawable = AppCompatResources.getDrawable(mContext, R.drawable.ic_keyboard_arrow_up_black_24dp);
        }
        mExpandableIndicator.setImageDrawable(drawable);
    }

    /**
     * Start the animation while expanding a TextView.
     *
     * @param tv the expandable TextView.
     */
    private void cycleTextViewExpansion(TextView tv) {
        ObjectAnimator animation = ObjectAnimator.ofInt(tv, "maxLines",
                tv.getMaxLines() == Constants.EXPERT_DETAILS_COLLAPSED_MAX_LINES ? tv.getLineCount() : Constants.EXPERT_DETAILS_COLLAPSED_MAX_LINES);
        animation.setDuration(Constants.EXPERT_DETAILS_COLLAPSING_TIME).start();
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

    /*
    Interface Methods
    */

    @Override
    public void onRecordListItemClicked(int id, boolean isBookmark) {
        mListener.onExpertRecordClick(id);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_share_expert_details:
                startActivity(Intent.createChooser(mShareIntent, getString(R.string.action_share)));
                return true;
            case R.id.action_bookmark:
                if (!mIsBookmark) {
                    mBookmark.setIcon(R.drawable.ic_star_accent_24dp);
                    mViewModel.setBookmark(mExpertDetail);

                } else {
                    mBookmark.setIcon(R.drawable.ic_star_white_24dp);
                    mViewModel.deleteBookmark(mExpertDetail);
                }
                mIsBookmark = !mIsBookmark;
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Fragment Interaction Interface.
     */
    public interface OnInteractionListener {
        /**
         * Triggered when a ListView item is clicked to open the corresponding detail view.
         *
         * @param id The identifier of the record.
         */
        void onExpertRecordClick(int id);
    }
}
