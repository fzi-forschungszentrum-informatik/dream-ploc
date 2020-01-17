package de.fzi.dream.ploc.ui.listener;

import de.fzi.dream.ploc.data.structure.entity.ExpertBookmark;

public interface OnExpertBookmarkCardViewListener {
    void onCardClick(int id);

    void onCardLeftSwipe(ExpertBookmark expertBookmark, int position);
}
