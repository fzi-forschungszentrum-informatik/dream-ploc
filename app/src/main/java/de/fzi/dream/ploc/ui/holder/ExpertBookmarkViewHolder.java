package de.fzi.dream.ploc.ui.holder;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import de.fzi.dream.ploc.R;
import de.fzi.dream.ploc.data.structure.entity.ExpertBookmark;
import de.fzi.dream.ploc.ui.listener.OnExpertBookmarkCardViewListener;
import de.fzi.dream.ploc.utility.Constants;
import de.fzi.dream.ploc.utility.identicon.HashGenerator;
import de.fzi.dream.ploc.utility.identicon.IdenticonGenerator;

public class ExpertBookmarkViewHolder extends RecyclerView.ViewHolder {

    // Context
    private Context mContext;

    // Data
    private ExpertBookmark mBookmark;

    // View
    private CardView mCardView;
    private ImageView mIdenticon;
    private TextView mFullName;
    private TextView mLastActivity;
    private TextView mSubjects;
    private TextView mPublicationCount;
    private TextView mAffiliation;

    public ExpertBookmarkViewHolder(View v) {
        super(v);
        // Get context
        mContext = v.getContext();

        // View container
        mCardView = (CardView) v;

        // Container views
        mFullName = mCardView.findViewById(R.id.text_view_name_card_view_expert_preview_bookmark);
        mIdenticon = mCardView.findViewById(R.id.image_view_identicon_card_view_expert_preview_bookmark);
        mLastActivity = mCardView.findViewById(R.id.text_view_last_activity_card_view_expert_preview_bookmark);
        mPublicationCount = mCardView.findViewById(R.id.text_view_publication_count_card_view_expert_preview_bookmark);
        mSubjects = mCardView.findViewById(R.id.text_view_subjects_card_view_expert_preview_bookmark);
        mAffiliation = mCardView.findViewById(R.id.text_view_affiliation_card_view_expert_preview_bookmark);
    }

    public void bindTo(ExpertBookmark b, OnExpertBookmarkCardViewListener l) {
        // Bind data to view
        mBookmark = b;
        if (mBookmark != null) {
            mFullName.setText(mBookmark.getName());
            mLastActivity.setText(String.format(mContext.getResources().getString(R.string.hint_publication_year), mBookmark.getLastActivity()));
            mIdenticon.setImageBitmap(IdenticonGenerator.generate(String.valueOf(mBookmark.getId()), new HashGenerator()));
            mSubjects.setText(TextUtils.join(mContext.getResources().getString(R.string.subject_delimiter), mBookmark.getSubjectKeywords(Constants.EXPERT_SUBJECT_COUNT)));
            mPublicationCount.setText(String.format(mContext.getResources().getString(R.string.hint_publication_count), mBookmark.getTotalPublicationCount()));
            mAffiliation.setText(mBookmark.getAffiliation());
            mCardView.setOnClickListener(v -> l.onCardClick(mBookmark.getId()));
        }
    }

    public ExpertBookmark getBookmark() {
        return mBookmark;
    }
}
