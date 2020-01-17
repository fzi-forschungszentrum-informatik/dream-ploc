package de.fzi.dream.ploc.ui.holder;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import de.fzi.dream.ploc.R;
import de.fzi.dream.ploc.ui.listener.OnExpertPreviewCardViewListener;

public class GroupedRecordListHeaderViewHolder {

    // Context
    private Context mContext;

    // Data
    private String mHeader;

    // View
    private TextView mTitleTextView;

    public GroupedRecordListHeaderViewHolder(View view) {
        mContext = view.getContext();
        mTitleTextView = view.findViewById(R.id.text_view_title_grouped_list);
    }

    public void bindTo(String header, OnExpertPreviewCardViewListener l) {
        mHeader = header;
        if (mHeader != null) {
            mTitleTextView.setText(mHeader);
        }
    }

    public String getHeader() {
        return mHeader;
    }
}
