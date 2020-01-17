package de.fzi.dream.ploc.ui.listener;

import de.fzi.dream.ploc.data.structure.entity.RecordPreview;

public interface OnRecordPreviewCardInteraction {
    void onCardLeftSwipe(RecordPreview preview, int position);

    void onCardRightSwipe(RecordPreview preview, int position);

    void onDetailButtonClick(int id, boolean isExamined);

    void onFeedbackButtonClick(int id, boolean isExamined);
}
