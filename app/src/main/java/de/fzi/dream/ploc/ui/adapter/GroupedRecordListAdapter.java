package de.fzi.dream.ploc.ui.adapter;


import android.content.Context;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.TreeSet;

import de.fzi.dream.ploc.R;
import de.fzi.dream.ploc.data.structure.TinyRecord;
import de.fzi.dream.ploc.ui.holder.GroupedRecordListItemViewHolder;

/**
 * The GroupedRecordListAdapter provides a binding from data set to the {@link GroupedRecordListItemViewHolder}
 * which is displayed within a {@link ListView}. The adapter groups the data in two sections for
 * the section header and the section item.
 *
 * @author Felix Melcher
 */
public class GroupedRecordListAdapter extends BaseAdapter {

    /**
     * Public class identifier tag
     */
    public static final String TAG = GroupedRecordListAdapter.class.getSimpleName();

    // Section Types
    private static final int TYPE_ITEM = 0;
    private static final int TYPE_HEADER = 1;

    // Data
    private ArrayList<String> mFullData = new ArrayList<>();
    private SparseArray<TinyRecord> mItemTitleData = new SparseArray<>();
    private ArrayList<String> mItemSubTitleData = new ArrayList<>();
    private TreeSet<Integer> sectionHeader = new TreeSet<>();

    // Listener
    private OnInteractionListener mListener;

    /**
     * Constructor
     *
     * @param listener the event listener waiting for interactions within this adapter.
     */
    public GroupedRecordListAdapter(OnInteractionListener listener) {
        mListener = listener;
    }

    /**
     * Add an item section to this adapter.
     *
     * @param position The position where the item shall be inserted .
     * @param item     The data of the item.
     */
    public void addItem(int position, final TinyRecord item) {
        mFullData.add(item.getTitle());
        mItemTitleData.put(position, item);
        mItemSubTitleData.add(TextUtils.join(" , ", item.getCreators()));
        notifyDataSetChanged();
    }

    /**
     * Add a header section to this adapter.
     *
     * @param header The data string of the header.
     */
    public void addHeader(final String header) {
        mFullData.add(header);
        mItemSubTitleData.add(header);
        sectionHeader.add(mFullData.size() - 1);
        notifyDataSetChanged();
    }

    /**
     * Transform the standard view of the ListView in our {@link GroupedRecordListItemViewHolder}
     * section ViewHolder based on the given type.
     *
     * @param position    The position of the view in the ListView.
     * @param convertView The view to be converted to the {@link GroupedRecordListItemViewHolder}.
     * @param parent      The parent view the ListView is embedded in.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater mInflater = (LayoutInflater) parent.getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        GroupedRecordListItemViewHolder vh = new GroupedRecordListItemViewHolder();
        int rowType = getItemViewType(position);
        if (convertView == null) {
            switch (rowType) {
                case TYPE_ITEM:
                    convertView = vh.initItem(mInflater.inflate(R.layout.list_view_grouped_item, parent, false),
                            mListener, getItemByPosition(position));
                    break;
                case TYPE_HEADER:
                    convertView = vh.initHeader(mInflater.inflate(R.layout.list_view_grouped_header, parent, false));
                    break;
            }
            if (convertView != null) {
                convertView.setTag(vh);
            }
        } else {
            vh = (GroupedRecordListItemViewHolder) convertView.getTag();
        }
        vh.bindTo(mFullData.get(position), rowType == TYPE_ITEM ? mItemSubTitleData.get(position) : null);
        return convertView;
    }

    @Override
    public int getItemViewType(int position) {
        return sectionHeader.contains(position) ? TYPE_HEADER : TYPE_ITEM;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getCount() {
        return mFullData.size();
    }

    @Override
    public String getItem(int position) {
        return mFullData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private TinyRecord getItemByPosition(int i) {
        return mItemTitleData.get(i);
    }

    public interface OnInteractionListener {
        void onRecordListItemClicked(int id, boolean isBookmark);
    }

}