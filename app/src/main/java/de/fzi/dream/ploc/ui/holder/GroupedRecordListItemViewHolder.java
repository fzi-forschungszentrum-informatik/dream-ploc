package de.fzi.dream.ploc.ui.holder;

import android.view.View;
import android.widget.TextView;

import de.fzi.dream.ploc.R;
import de.fzi.dream.ploc.data.structure.TinyRecord;
import de.fzi.dream.ploc.ui.adapter.GroupedRecordListAdapter.OnInteractionListener;

public class GroupedRecordListItemViewHolder {

    // View
    private TextView mTitleTextView;
    private TextView mSubTitleTextView;

    public GroupedRecordListItemViewHolder() {
    }

    public View initItem(View v, OnInteractionListener mListener, TinyRecord itemByPosition) {
        mTitleTextView = v.findViewById(R.id.text_view_title_grouped_list);
        mSubTitleTextView = v.findViewById(R.id.text_view_creators_grouped_list);
        v.setOnClickListener(i -> mListener.onRecordListItemClicked(itemByPosition.getId(), false));
        return v;
    }

    public View initHeader(View v) {
        mTitleTextView = v.findViewById(R.id.text_view_header_grouped_list);
        return v;
    }

    public void bindTo(String title, String subtitle) {
        mTitleTextView.setText(title);
        if (mSubTitleTextView != null) {
            mSubTitleTextView.setText(subtitle);
        }
    }
}
