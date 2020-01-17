package de.fzi.dream.ploc.ui.fragment;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import java.util.List;

import de.fzi.dream.ploc.R;
import de.fzi.dream.ploc.data.structure.Feedback;
import de.fzi.dream.ploc.data.structure.RecordDetail;
import de.fzi.dream.ploc.ui.activity.RecordDetailActivity;
import de.fzi.dream.ploc.ui.dialog.NotificationDialog;
import de.fzi.dream.ploc.utility.Constants;
import de.fzi.dream.ploc.viewmodel.RecordDetailViewModel;

import static android.content.Context.DOWNLOAD_SERVICE;
import static de.fzi.dream.ploc.utility.Constants.RECORD_DETAILS_COLLAPSED_MAX_LINE;
import static de.fzi.dream.ploc.utility.Constants.EXTRA_ID;
import static de.fzi.dream.ploc.utility.Constants.EXTRA_IS_BOOKMARK;
import static de.fzi.dream.ploc.utility.Constants.RECORD_DETAILS_COLLAPSING_TIME;

/**
 * The RecordDetailFragment is embedded in the {@link RecordDetailActivity} and manages the views
 * displaying the data set.
 *
 * @author Felix Melcher
 */
public class RecordDetailFragment extends Fragment {

    /**
     * Public class identifier tag
     */
    public static final String TAG = RecordDetailFragment.class.getSimpleName();

    // Context
    private Context mContext;

    // Parameters
    private int mRecordID;
    private RecordDetail mRecordDetail;
    private List<Feedback> mFeedback;
    private boolean mIsBookmark = false;
    private Long downloadID;

    // ViewModel
    private RecordDetailViewModel mViewModel;

    // View
    private LinearLayout mFeedbackContainer;
    private MenuItem mBookmark;
    private TabLayout mTabLayout;
    private ProgressBar mRelevanceRatingBar;
    private ProgressBar mPresentationRatingBar;
    private ProgressBar mMethodologyRatingBar;
    private ImageView mType;
    private ImageView mExpandableIndicator;
    private TextView mTitle;
    private TextView mCreators;
    private TextView mDateOfPublication;
    private TextView mAbstractText;
    private TextView mSubjects;
    private TextView mTypeText;
    private TextView mRelevanceRatingHeader;
    private TextView mPresentationRatingHeader;
    private TextView mMethodologyRatingHeader;
    private Button mDownloadButton;
    private Button mLinkButton;


    // Intent and BroadcastReceiver
    private Intent mShareIntent = setUpShareIntent();
    private BroadcastReceiver onDownloadComplete = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            if (downloadID == id) {
                showSnackbar(getResources().getString(R.string.snackbar_message_download_completed));
            }
        }
    };

    /**
     * Fragments require an empty public constructor.
     */
    public RecordDetailFragment() {
    }

    /**
     * Create a new instance of the RecordDetailFragment.
     *
     * @return RecordDetailFragment
     */
    public static RecordDetailFragment newInstance(int publicationID, boolean isBookmark) {
        Bundle arguments = new Bundle();
        arguments.putInt(EXTRA_ID, publicationID);
        arguments.putBoolean(EXTRA_IS_BOOKMARK, isBookmark);
        RecordDetailFragment fragment = new RecordDetailFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    /*
      Lifecycle Methods
    */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            mIsBookmark = getArguments().getBoolean(EXTRA_IS_BOOKMARK);
            mRecordID = getArguments().getInt(EXTRA_ID);
        }
        mContext.registerReceiver(onDownloadComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_record_detail, container, false);
        mTitle = rootView.findViewById(R.id.publication_title_detail);
        mType = rootView.findViewById(R.id.record_type_icon_detail);
        mTypeText = rootView.findViewById(R.id.record_type_text_detail);
        mCreators = rootView.findViewById(R.id.record_creators_detail);
        mDateOfPublication = rootView.findViewById(R.id.record_year_detail);
        mAbstractText = rootView.findViewById(R.id.abstract_text_detail);
        mSubjects = rootView.findViewById(R.id.record_subjects_detail);
        mRelevanceRatingBar = rootView.findViewById(R.id.relevanceRatingBar);
        mPresentationRatingBar = rootView.findViewById(R.id.presentationRatingBar);
        mMethodologyRatingBar = rootView.findViewById(R.id.methodologyRatingBar);
        mRelevanceRatingHeader = rootView.findViewById(R.id.relevance_header);
        mPresentationRatingHeader = rootView.findViewById(R.id.presentation_header);
        mMethodologyRatingHeader = rootView.findViewById(R.id.methodology_header);
        mLinkButton = rootView.findViewById(R.id.link_button);
        mDownloadButton = rootView.findViewById(R.id.download_button);
        mFeedbackContainer = rootView.findViewById(R.id.feedback_container);
        mExpandableIndicator = rootView.findViewById(R.id.expandable_indicator_detail);
        mTabLayout = rootView.findViewById(R.id.metaTabs);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if (getArguments() != null) {
            mViewModel = obtainViewModel();
            observeDetails();
            observeFeedback();
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.activity_detail_toolbar, menu);
        mBookmark = menu.findItem(R.id.action_bookmark_record);
        // If record is already bookmarked, change icon to selected.
        if (mIsBookmark) {
            mBookmark.setIcon(R.drawable.ic_bookmark_accent_24dp);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_share_record_details:
                startActivity(Intent.createChooser(mShareIntent, getString(R.string.action_share)));
                return true;
            case R.id.action_bookmark_record:
                if (!mIsBookmark) {
                    mBookmark.setIcon(R.drawable.ic_bookmark_accent_24dp);
                    mViewModel.setBookmark(mRecordDetail);
                    showSnackbar(getResources().getString(R.string.message_bookmarked));
                } else {
                    mBookmark.setIcon(R.drawable.ic_bookmark_white_24dp);
                    mViewModel.deleteBookmark(mRecordDetail);
                    showSnackbar(getResources().getString(R.string.message_unbookmarked));
                }
                mIsBookmark = !mIsBookmark;
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * Bind the data from the ViewModel to the layout views of the fragment.
     *
     * @param details The data set that should be bind to the fragments view.
     */
    public void bindTo(RecordDetail details) {
        // Set text to TextViews
        mTitle.setText(details.getTitle());
        mDateOfPublication.setText(details.getYear());
        mAbstractText.setText(details.getDescription());
        mCreators.setText(details.getCreatorsAsString());
        mType.setImageResource(details.typeToResourceIcon());
        mSubjects.setText(TextUtils.join(getResources().getString(R.string.subject_delimiter), details.getSubjects()));
        mTypeText.setText(details.typeToResourceName());

        // Set up sharing intent
        mShareIntent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.message_sharing_intent) + details.getSharingText());

        // Register ClickListener for TextView expansion
        mExpandableIndicator.setOnClickListener(v -> {
            mAbstractText.callOnClick();
            switchIndicator();
        });
        mAbstractText.setOnClickListener(v -> {
            animateTextExpansion((TextView) v);
            switchIndicator();
        });
        mDownloadButton.setOnClickListener(view -> checkPermissionOrDownload());
        mLinkButton.setOnClickListener(view -> openURLinWebView(mRecordDetail.getRepositoryLink()));

        // Set up TabLayout
        mTabLayout.addTab(mTabLayout.newTab().setText(getResources().getString(R.string.details_tab_title_tags)).setIcon(R.drawable.ic_label_black_24dp), true);
        mTabLayout.addTab(mTabLayout.newTab().setText(getResources().getString(R.string.details_tab_title_comments)).setIcon(R.drawable.ic_comment_black_24dp));
        mTabLayout.addTab(mTabLayout.newTab().setText(getResources().getString(R.string.details_tab_title_feedback)).setIcon(R.drawable.ic_error_black_24dp));
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    mSubjects.setVisibility(View.VISIBLE);
                    mFeedbackContainer.setVisibility(View.GONE);
                    mSubjects.setText(TextUtils.join(getResources().getString(R.string.subject_delimiter), details.getSubjects()));
                } else if (tab.getPosition() == 1) {
                    mSubjects.setVisibility(View.VISIBLE);
                    mFeedbackContainer.setVisibility(View.GONE);
                    mSubjects.setText(getResources().getString(R.string.placeholder_details_comments));
                } else if (tab.getPosition() == 2) {
                    if (mFeedback != null && !mFeedback.isEmpty()) {
                        mSubjects.setVisibility(View.GONE);
                        mFeedbackContainer.setVisibility(View.VISIBLE);
                        double totalFeedbackCount = mFeedback.size();
                        int positiveRelevance = mFeedback.stream().mapToInt(Feedback::getRelevance).sum();
                        int positivePresentation = mFeedback.stream().mapToInt(Feedback::getPresentation).sum();
                        int positiveMethodology = mFeedback.stream().mapToInt(Feedback::getMethodology).sum();
                        mRelevanceRatingHeader.setText(getResources().getString(R.string.rating_overview_relevance, positiveRelevance, (int) totalFeedbackCount));
                        mRelevanceRatingBar.setProgress((int) (positiveRelevance / totalFeedbackCount * 100));
                        mPresentationRatingHeader.setText(getResources().getString(R.string.rating_overview_presentation, positivePresentation, (int)  totalFeedbackCount));
                        mPresentationRatingBar.setProgress((int) (positivePresentation / totalFeedbackCount * 100), true);
                        mMethodologyRatingHeader.setText(getResources().getString(R.string.rating_overview_methodology, positiveMethodology, (int)  totalFeedbackCount));
                        mMethodologyRatingBar.setProgress((int) (positiveMethodology / totalFeedbackCount * 100));
                    } else {
                        mSubjects.setText(getResources().getString(R.string.placeholder_details_feedback));
                    }
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

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
     * Handle the different icon shown under the abstract depending on its expanded state.
     */
    private void switchIndicator() {
        Drawable drawable;
        if (mAbstractText.getMaxLines() != RECORD_DETAILS_COLLAPSED_MAX_LINE) {
            drawable = AppCompatResources.getDrawable(mContext, R.drawable.ic_keyboard_arrow_down_black_24dp);
        } else {
            drawable = AppCompatResources.getDrawable(mContext, R.drawable.ic_keyboard_arrow_up_black_24dp);
        }
        mExpandableIndicator.setImageDrawable(drawable);
    }

    /**
     * Animate the expansion of the abstract text on click.
     */
    private void animateTextExpansion(TextView tv) {
        ObjectAnimator animation = ObjectAnimator.ofInt(tv, "maxLines",
                tv.getMaxLines() == RECORD_DETAILS_COLLAPSED_MAX_LINE ? tv.getLineCount() : RECORD_DETAILS_COLLAPSED_MAX_LINE);
        animation.setDuration(RECORD_DETAILS_COLLAPSING_TIME).start();
    }

    /**
     * Start a browser activity to open a url in a web view. If there is no PDF link available
     * show a snackbar message.
     */
    private void openURLinWebView(String url) {
        if (url != null && !url.equals("") && URLUtil.isValidUrl(url)) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        } else {
            showSnackbar(getResources().getString(R.string.snackbar_message_no_doi));
        }
    }

    /**
     * Download a PDF to the download folder of the android system. If there is no PDF link available
     * show a snackbar message.
     */
    private void downloadPDF(String url) {
        if (url != null && !url.equals("") && URLUtil.isValidUrl(url)) {
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url))
                    .setTitle(getResources().getString(R.string.notification_download_message))
                    .setDescription(mRecordDetail.getTitle())
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
                    .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, mRecordDetail.getTitle() + ".pdf")
                    .setRequiresCharging(false)
                    .setAllowedOverMetered(true)
                    .setAllowedOverRoaming(true);
            DownloadManager downloadManager = (DownloadManager) mContext.getSystemService(DOWNLOAD_SERVICE);
            downloadID = downloadManager.enqueue(request);
            showSnackbar(getResources().getString(R.string.snackbar_message_download_started));
        } else {
            NotificationDialog dialog = new NotificationDialog(getResources()
                    .getString(R.string.dialog_title_no_pdf), getResources().getString(R.string.dialog_message_no_pdf));
            if (getActivity() != null) {
                dialog.show(getActivity().getSupportFragmentManager(), NotificationDialog.TAG);
            }
        }
    }

    /**
     * Check and request permission to write to the file system. If permission is already granted,
     * download the PDF.
     */
    private void checkPermissionOrDownload() {
        if (ContextCompat.checkSelfPermission(getActivity(), "android.permission.WRITE_EXTERNAL_STORAGE") == PackageManager.PERMISSION_DENIED) {
            requestPermissions( new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"},  101);
        } else {
            downloadPDF(mRecordDetail.getPDFLink());
        }
    }

    /**
     * This method is called when the user accepts or decline the permission requested
     * from #checkPermissionOrDownload.
     *
     * @param requestCode  The requestCode is used to check which permission called this function.
     * @param permissions  This array contains the android uri for the permissions.
     * @param grantResults This array contains the result of the permission requests.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 101) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                downloadPDF(mRecordDetail.getPDFLink());
            } else {
                showSnackbar("Download only possible with granted permission.");
            }
        }
    }

    /**
     * Show a snackbar within the current fragment.
     *
     * @param message The String message that will be displayed on the snackbar.
     */
    private void showSnackbar(String message) {
        Snackbar snackbar = Snackbar
                .make(((Activity) mContext).findViewById(R.id.coordinator_detail_activity), message, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    /*
    ViewModel Methods
    */

    /**
     * Obtain the ViewModel instance responsible for getting the data from the remote or local
     * data source.
     *
     * @return RecordDetailViewModel
     */
    private RecordDetailViewModel obtainViewModel() {
        return new ViewModelProvider(this).get(RecordDetailViewModel.class);
    }

    /**
     * Observe and react to changes on record detail updates.
     */
    private void observeDetails() {
        mViewModel.getDetails(mRecordID).observe(getViewLifecycleOwner(), recordDetail -> {
            if (recordDetail != null) {
                mRecordDetail = recordDetail;
                bindTo(recordDetail);
            }
        });
    }

    /**
     * Observe and react to changes on the feedback.
     */
    private void observeFeedback() {
        mViewModel.getFeedbacks(getArguments().getInt(EXTRA_ID)).observe(getViewLifecycleOwner(), feedbackDetail -> {
            if (feedbackDetail != null) {
                mFeedback = feedbackDetail;
            }
        });
    }
}
