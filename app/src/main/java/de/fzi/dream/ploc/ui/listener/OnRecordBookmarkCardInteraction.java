package de.fzi.dream.ploc.ui.listener;

import java.util.ArrayList;

import de.fzi.dream.ploc.data.structure.entity.RecordBookmark;

public interface OnRecordBookmarkCardInteraction {
    void onCardClick(int id, boolean isBookmark);

    void onMultiSelectFinished(ArrayList<RecordBookmark> selectedItems);

    void onCardLeftSwipe(RecordBookmark recordBookmark, int position);
}
