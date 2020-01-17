package de.fzi.dream.ploc.ui.holder;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ImageViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import de.fzi.dream.ploc.R;
import de.fzi.dream.ploc.data.structure.entity.FeedbackPreview;
import de.fzi.dream.ploc.data.structure.entity.Interest;
import de.fzi.dream.ploc.ui.listener.OnRecordPreviewCardInteraction;
import de.fzi.dream.ploc.utility.Constants;

/**
 * The FeedbackPreviewViewHolder represents one item displayed in an recycler view.
 */
public class FeedbackPreviewViewHolder extends RecyclerView.ViewHolder {

    /**
     * Public class identifier tag
     */
    public static final String TAG = FeedbackPreviewViewHolder.class.getSimpleName();

    // Context this view is embedded in
    private Context mContext;

    // View/Layout
    private CardView mCardView;
    private ImageView mTypeView;
    private TextView mTitleTextView;
    private TextView mYearTextView;
    private TextView mCreatorsTextView;
    private TextView mAbstractTextView;
    private TextView mSubjectsTextView;
    private TextView mRecordIDTextView;
    private TextView mVisitedLabel;
    private Button mDetailButton;
    private Button mFeedbackButton;

    // Colors
    private int colorTextMediumEmphasis;
    private int colorBlack;

    // Data
    private FeedbackPreview mRecordPreview;
    private List<String> mInterests;

    public FeedbackPreviewViewHolder(View v, List<Interest> i) {
        super(v);
        if (i != null) {
            mInterests = i.stream().map(Interest::getKeyword).collect(Collectors.toList());
        }
        mContext = v.getContext();
        mCardView = v.findViewById(R.id.card_view);
        mTitleTextView = v.findViewById(R.id.record_title);
        mCreatorsTextView = v.findViewById(R.id.record_creators);
        mAbstractTextView = v.findViewById(R.id.record_abstract);
        mTypeView = v.findViewById(R.id.record_type);
        mYearTextView = v.findViewById(R.id.record_year);
        mSubjectsTextView = v.findViewById(R.id.record_subjects);
        mRecordIDTextView = v.findViewById(R.id.record_id);
        mDetailButton = v.findViewById(R.id.detail_button);
        mFeedbackButton = v.findViewById(R.id.feedback_button);
        mVisitedLabel = v.findViewById(R.id.history_viewed_at);
        colorBlack = ContextCompat.getColor(mContext, R.color.colorBlack);
        colorTextMediumEmphasis = ContextCompat.getColor(mContext, R.color.colorTextMediumEmphasis);
    }

    public void bindTo(FeedbackPreview p, OnRecordPreviewCardInteraction l) {
        mRecordPreview = p;
        if (mRecordPreview != null) {
            // set the content of the record to the TextViews
            mTitleTextView.setText(mRecordPreview.getTitle());
            mYearTextView.setText(String.valueOf(mRecordPreview.getYear()));
            mCreatorsTextView.setText(mRecordPreview.getCreators());
            mAbstractTextView.setText(mRecordPreview.getTeaser());
            mRecordIDTextView.setText(String.valueOf(mRecordPreview.getId()));

            // Make the "visited" view visible or not
            mVisitedLabel.setVisibility(mRecordPreview.isVisited() ? View.VISIBLE : View.GONE);

            // Set the image based on the record type
            mTypeView.setImageResource(mRecordPreview.typeToResourceIcon());
            ImageViewCompat.setImageTintList(mTypeView, ColorStateList.valueOf(mRecordPreview.isVisited() ? colorTextMediumEmphasis : colorBlack));

            // Change the text color based on the visited state
            mTitleTextView.setTextColor(ContextCompat.getColor(mContext, mRecordPreview.isVisited() ? R.color.colorTextMediumEmphasis : R.color.colorBlack));

            // Register ClickListeners
            mCardView.setOnClickListener(v -> {
                markAsVisited();
                l.onDetailButtonClick(mRecordPreview.getId(), false);
            });
            mFeedbackButton.setOnClickListener(v -> {
                markAsVisited();
                l.onFeedbackButtonClick(mRecordPreview.getId(), false);
            });
            mDetailButton.setOnClickListener(v -> {
                markAsVisited();
                l.onDetailButtonClick(mRecordPreview.getId(), false);
            });

            // Highlight the matching interests in the subject keywords of the current record
            if (mInterests != null && mInterests.size() > 0) {
                highlightMatchingSubjects(mRecordPreview.getSubjectKeywords());
            } else {
                mSubjectsTextView.setText(TextUtils.join(mContext.getResources().getString(R.string.subject_delimiter), mRecordPreview.getSubjectKeywords(Constants.RECORD_SUBJECT_COUNT)));
            }
        } else {
            // Covers the case of data not being ready yet.
            mTitleTextView.setText(mContext.getResources().getString(R.string.placeholder_no_records));
        }
    }

    private void markAsVisited() {
        mTitleTextView.setTextColor(ContextCompat.getColor(mContext, R.color.colorTextMediumEmphasis));
        mCardView.findViewById(R.id.history_viewed_at).setVisibility(View.VISIBLE);
        ImageViewCompat.setImageTintList(mTypeView, ColorStateList.valueOf(colorTextMediumEmphasis));
        mRecordPreview.setVisited(true);
    }

    private void highlightMatchingSubjects(List<String> subjects) {
        List<String> matchingSubjects = new ArrayList<>();
        for (String i : mInterests) {
            for (String str : subjects) {
                if (str.trim().contains(i)) {
                    matchingSubjects.add(str);
                }
            }
        }
        subjects.removeAll(matchingSubjects);
        if (matchingSubjects.size() > 0) {
            int lengthOfHighlight = TextUtils.join(mContext.getResources().getString(R.string.subject_delimiter), matchingSubjects).length();
            if (subjects.size() >= 5 - matchingSubjects.size()) {
                matchingSubjects.addAll(subjects.subList(0, 5 - matchingSubjects.size()));
            } else {
                matchingSubjects.addAll(subjects);
            }
            String completeText = TextUtils.join(mContext.getResources().getString(R.string.subject_delimiter), matchingSubjects);
            SpannableStringBuilder ssBuilder = new SpannableStringBuilder(completeText);
            ssBuilder.setSpan(
                    new StyleSpan(Typeface.BOLD),
                    0, // Start of the span (inclusive)
                    lengthOfHighlight, // End of the span (exclusive)
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE // Do not extend the span when text add later
            );
            mSubjectsTextView.setText(ssBuilder);
        }
    }
}
