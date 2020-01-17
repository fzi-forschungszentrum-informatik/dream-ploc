package de.fzi.dream.ploc.ui.listener;

import de.fzi.dream.ploc.data.structure.entity.ExpertPreview;

public interface OnExpertPreviewCardViewListener {
    void onCardRightSwipe(ExpertPreview expertPreview);

    void onCardClick(int id, boolean isBookmark);
}
