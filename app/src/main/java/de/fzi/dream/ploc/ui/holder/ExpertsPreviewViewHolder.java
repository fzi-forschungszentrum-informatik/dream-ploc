package de.fzi.dream.ploc.ui.holder;

import android.content.Context;
import android.graphics.Typeface;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import de.fzi.dream.ploc.R;
import de.fzi.dream.ploc.data.structure.entity.ExpertPreview;
import de.fzi.dream.ploc.data.structure.entity.Interest;
import de.fzi.dream.ploc.ui.listener.OnExpertPreviewCardViewListener;
import de.fzi.dream.ploc.utility.Constants;
import de.fzi.dream.ploc.utility.identicon.HashGenerator;
import de.fzi.dream.ploc.utility.identicon.IdenticonGenerator;

public class ExpertsPreviewViewHolder extends RecyclerView.ViewHolder {

    // Context
    private Context mContext;

    // Data
    private ExpertPreview mExpertPreview;
    private List<String> mInterests;

    // View
    private CardView mCardView;
    private ImageView mIdenticon;
    private TextView mFullName;
    private TextView mLastActivity;
    private TextView mSubjects;
    private TextView mPublicationCount;
    private TextView mAffiliation;

    public ExpertsPreviewViewHolder(View view, List<Interest> i) {
        super(view);
        if (i != null) {
            mInterests = i.stream().map(Interest::getKeyword).collect(Collectors.toList());
        }
        mContext = view.getContext();
        mCardView = view.findViewById(R.id.card_view_expert_preview_bookmark);
        mFullName = view.findViewById(R.id.text_view_name_card_view_expert_preview_bookmark);
        mLastActivity = view.findViewById(R.id.text_view_last_activity_card_view_expert_preview_bookmark);
        mSubjects = view.findViewById(R.id.text_view_subjects_card_view_expert_preview_bookmark);
        mIdenticon = view.findViewById(R.id.image_view_identicon_card_view_expert_preview_bookmark);
        mPublicationCount = view.findViewById(R.id.text_view_publication_count_card_view_expert_preview_bookmark);
        mAffiliation = view.findViewById(R.id.text_view_affiliation_card_view_expert_preview_bookmark);
    }

    public void bindTo(ExpertPreview p, OnExpertPreviewCardViewListener l) {
        mExpertPreview = p;
        if (mExpertPreview != null) {
            mFullName.setText(mExpertPreview.getName());
            mLastActivity.setText(String.format(mContext.getResources().getString(R.string.hint_publication_year), mExpertPreview.getLastPublicationYear()));
            mIdenticon.setImageBitmap(IdenticonGenerator.generate(String.valueOf(mExpertPreview.getId()), new HashGenerator()));
            mSubjects.setText(TextUtils.join(mContext.getResources().getString(R.string.subject_delimiter), mExpertPreview.getSubjectKeywords(Constants.EXPERT_SUBJECT_COUNT)));
            mPublicationCount.setText(String.format(mContext.getResources().getString(R.string.hint_publication_count), mExpertPreview.getTotalPublicationCount()));
            mAffiliation.setText(mExpertPreview.getAffiliation());
            mCardView.setOnClickListener(v -> l.onCardClick(mExpertPreview.getId(), mExpertPreview.isBookmark()));
            // Highlight the matching interests in the subject keywords of the current record
            if (mInterests != null && mInterests.size() > 0) {
                highlightMatchingSubjects(mExpertPreview.getSubjectKeywords());
            } else {
                mSubjects.setText(TextUtils.join(mContext.getResources().getString(R.string.subject_delimiter), mExpertPreview.getSubjectKeywords(Constants.EXPERT_SUBJECT_COUNT)));
            }
        }
    }

    public ExpertPreview getExpertPreview() {
        return mExpertPreview;
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
            mSubjects.setText(ssBuilder);
        }
    }
}
