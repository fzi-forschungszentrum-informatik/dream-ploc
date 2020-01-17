package de.fzi.dream.ploc.ui.holder;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.List;

import de.fzi.dream.ploc.R;
import de.fzi.dream.ploc.data.structure.entity.Collection;
import de.fzi.dream.ploc.data.structure.entity.RecordBookmark;


public class RecordBookmarkViewHolder extends RecyclerView.ViewHolder {

    public CardView mCardView;
    public TextView mTitleTextView;
    public ImageView mTypeView;
    private Context mContext;
    private TextView mYearTextView;
    private TextView mCreatorsTextView;
    private TextView mTeaserTextView;
    private TextView mSubjects;
    private ChipGroup mCollectionsChipGroup;
    private RecordBookmark mBookmark;

    public RecordBookmarkViewHolder(View v) {
        super(v);
        mContext = v.getContext();
        mCardView = (CardView) v;
        mTitleTextView = v.findViewById(R.id.bookmark_title);
        mTeaserTextView = v.findViewById(R.id.bookmark_abstract);
        mCreatorsTextView = v.findViewById(R.id.bookmark_creators);
        mTypeView = v.findViewById(R.id.bookmark_type);
        mYearTextView = v.findViewById(R.id.bookmark_year);
        mSubjects = v.findViewById(R.id.record_subjects);
        mCollectionsChipGroup = v.findViewById(R.id.subject_keywords);
    }

    public void bindTo(RecordBookmark bookmark, List<Collection> collections) {
        mBookmark = bookmark;
        if (mBookmark != null) {
            mCardView.setCardBackgroundColor(Color.WHITE);
            mTitleTextView.setText(mBookmark.getTitle());
            mYearTextView.setText(String.valueOf(mBookmark.getYear()));
            mTeaserTextView.setText(mBookmark.getAbstractText());
            mCreatorsTextView.setText(mBookmark.getCreators());
            mTypeView.setImageResource(mBookmark.typeToResourceIcon());
            mSubjects.setText(TextUtils.join(mContext.getResources().getString(R.string.subject_delimiter), bookmark.getSubjectKeywords(5)));
            mCollectionsChipGroup.removeAllViews();
            if (mBookmark.getCollectionIDs() != null && mBookmark.getCollectionIDs().size() > 0) {
                mCollectionsChipGroup.setVisibility(View.VISIBLE);
                for (Integer id : mBookmark.getCollectionIDs()) {
                    if (collections != null) {
                        for (Collection collection : collections) {
                            if (collection.getId() == id) {
                                mCollectionsChipGroup.addView(createCollectionChip(collection.getName()));
                            }
                        }
                    }
                }
            }
        }
    }

    private Chip createCollectionChip(String name) {
        Chip chip = new Chip(mContext);
        chip.setText(name);
        chip.setClickable(false);
        chip.setChipBackgroundColorResource(R.color.colorAccent);
        chip.setTextColor(mContext.getResources().getColor(R.color.colorWhite, null));
        return chip;
    }

    public RecordBookmark getBookmark() {
        return mBookmark;
    }
}
