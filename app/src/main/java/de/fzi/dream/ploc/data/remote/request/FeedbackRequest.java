package de.fzi.dream.ploc.data.remote.request;

import com.google.gson.annotations.SerializedName;

import de.fzi.dream.ploc.data.remote.connection.GozerApi;

/**
 * Message structure for the communication with the gozer backend.
 * <p>
 * See usage in api interface {@link GozerApi} class
 *
 * @author Felix Melcher
 */
public class FeedbackRequest {

    @SerializedName("record_id")
    private int mRecordID;
    @SerializedName("relevance")
    private int mRelevance;
    @SerializedName("presentation")
    private int mPresentation;
    @SerializedName("methodology")
    private int mMethodology;

    public FeedbackRequest setFeedback(int recordID, int relevance, int presentation, int methodology) {
        mRecordID = recordID;
        mRelevance = relevance;
        mPresentation = presentation;
        mMethodology = methodology;
        return this;
    }
}
