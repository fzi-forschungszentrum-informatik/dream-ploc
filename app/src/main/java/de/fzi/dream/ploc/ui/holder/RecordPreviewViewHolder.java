package de.fzi.dream.ploc.ui.holder;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import android.view.View;
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
import de.fzi.dream.ploc.data.structure.entity.Interest;
import de.fzi.dream.ploc.data.structure.entity.RecordPreview;
import de.fzi.dream.ploc.ui.callback.RecordPreviewSwipeCallback;
import de.fzi.dream.ploc.ui.listener.OnRecordPreviewCardInteraction;

/**
 * The RecordPreviewViewHolder represents one item displayed in an recycler view.
 */
public class RecordPreviewViewHolder extends RecyclerView.ViewHolder {

    /**
     * Public class identifier tag
     */
    public static final String TAG = RecordPreviewViewHolder.class.getSimpleName();

    // Context this view is embedded in
    private Context mContext;

    // Data
    private RecordPreview mRecordPreview;
    private List<String> mInterests;

    // View/Layout
    private CardView mCardView;
    private ImageView mTypeView;
    private TextView mTitleTextView;
    private TextView mYearTextView;
    private TextView mCreatorsTextView;
    private TextView mAbstractTextView;
    private TextView mSubjectsTextView;
    private TextView mRecordIDTextView;

    // Colors
    private int colorTextMediumEmphasis;
    private int colorBlack;

    /**
     * Constructor, builds the view objects from the layout references and gets the context in which
     * this view is embedded.
     *
     * @param v The inflated layout definition for displaying the current view.
     * @param i A list of interests defined by the user.
     */
    public RecordPreviewViewHolder(View v, List<Interest> i) {
        super(v);
        if(i != null){
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
        colorBlack = ContextCompat.getColor(mContext, R.color.colorBlack);
        colorTextMediumEmphasis = ContextCompat.getColor(mContext, R.color.colorTextMediumEmphasis);
    }

    /**
     * Constructor, builds the view objects from the layout references and gets the context in which
     * this view is embedded.
     *
     * @param p The {@link RecordPreview} to display in this ViewHolder.
     * @param l A listener to communicate CardView interactions back to the Fragment.
     */
    public void bindTo(RecordPreview p, OnRecordPreviewCardInteraction l) {
        mRecordPreview = p;
        if (mRecordPreview != null) {
            // set the content of the record to the TextViews
            mTitleTextView.setText(mRecordPreview.getTitle());
            mYearTextView.setText(String.valueOf(mRecordPreview.getYear()));
            mCreatorsTextView.setText(mRecordPreview.getCreators());
            mAbstractTextView.setText(mRecordPreview.getTeaser());
            mRecordIDTextView.setText(String.valueOf(mRecordPreview.getId()));

            // Make the "visited" view visible or not
            mCardView.findViewById(R.id.history_viewed_at).setVisibility(mRecordPreview.isVisited() ? View.VISIBLE : View.GONE);

            // Set the image based on the record type
            mTypeView.setImageResource(mRecordPreview.typeToResourceIcon());
            ImageViewCompat.setImageTintList(mTypeView, ColorStateList.valueOf(mRecordPreview.isVisited() ? colorTextMediumEmphasis : colorBlack));

            // Change the text color based on the visited state
            mTitleTextView.setTextColor(mRecordPreview.isVisited() ? colorTextMediumEmphasis : colorBlack);

            // Register ClickListeners
            mCardView.setOnClickListener(v -> {
                markAsVisited();
                l.onDetailButtonClick(mRecordPreview.getId(), false);
            });

            // If there are user defined interests, highlight the matching interests
            // in the subject keywords of the current record, else only display the keywords
            if (mInterests != null && mInterests.size() > 0) {
                highlightMatchingSubjects(mRecordPreview.getSubjectKeywords());
            } else {
                mSubjectsTextView.setText(TextUtils.join(mContext.getResources().getString(R.string.subject_delimiter), mRecordPreview.getSubjectKeywords(5)));
            }
        } else {
            // Covers the case of data not being ready yet.
            mTitleTextView.setText(mContext.getResources().getString(R.string.error_record_not_loaded));
        }
    }

    /**
     * Return the RecordPreview displayed in this ViewHolder, needed in the
     * {@link RecordPreviewSwipeCallback} to pass the record to the
     * event listener after a swipe.
     *
     * @return p The {@link RecordPreview} displayed in this ViewHolder
     */
    public RecordPreview getRecordPreview() {
        return mRecordPreview;
    }

    /**
     * Update the ViewHolder after an object is visited.
     */
    private void markAsVisited() {
        ImageViewCompat.setImageTintList(mTypeView, ColorStateList.valueOf(colorTextMediumEmphasis));
        mTitleTextView.setTextColor(colorTextMediumEmphasis);
        mCardView.findViewById(R.id.history_viewed_at).setVisibility(View.VISIBLE);
        mRecordPreview.setVisited(true);
    }

    /**
     * Find the matching keywords between this records subjects and the user defined interest profile.
     * Highlight the matching keywords in the TextView of this ViewHolder.
     * <p>
     * BETA FUNCTION, NOT TESTED EXTENSIVELY
     */
    private void highlightMatchingSubjects(List<String> subjects) {
        // Array to safe all matching subjects
        List<String> matchingSubjects = new ArrayList<>();
        // Iterate over all interest from the user...
        for (String i : mInterests) {
            // ... and over all subjects of the current paper
            for (String str : subjects) {
                // if there is a subject matching add it to the matching list
                if (str.trim().contains(i)) {
                    matchingSubjects.add(str);
                }
            }
        }
        // Remove all matching subjects from the list of subjects bound to the record
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
