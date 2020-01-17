package de.fzi.dream.ploc.ui.callback;

import java.util.List;

import de.fzi.dream.ploc.data.structure.Feedback;

public interface FeedbackDetailCallback {
    void onCallback(List<Feedback> feedbackDetail);
}
