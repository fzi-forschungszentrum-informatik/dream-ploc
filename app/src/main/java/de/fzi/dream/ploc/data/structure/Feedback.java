package de.fzi.dream.ploc.data.structure;

import com.google.gson.annotations.SerializedName;

public class Feedback {
    @SerializedName("record_id")
    private int mRecordId;
    @SerializedName("relevance")
    private int mRelevance;
    @SerializedName("presentation")
    private int mPresentation;
    @SerializedName("methodology")
    private int mMethodology;

    public Feedback(int recordId, boolean relevance, boolean presentation, boolean methodology) {
        mRecordId = recordId;
        mRelevance = (relevance) ? 1 : 0;
        mPresentation = (presentation) ? 1 : 0;
        mMethodology = (methodology) ? 1 : 0;
    }

    public int getRecordId() {
        return mRecordId;
    }

    public void setRecordId(int mRecordId) {
        this.mRecordId = mRecordId;
    }

    public int getRelevance() {
        return mRelevance;
    }

    public void setRelevance(int mRelevance) {
        this.mRelevance = mRelevance;
    }

    public int getPresentation() {
        return mPresentation;
    }

    public void setPresentation(int mPresentation) {
        this.mPresentation = mPresentation;
    }

    public int getMethodology() {
        return mMethodology;
    }

    public void setMethodology(int mMethodology) {
        this.mMethodology = mMethodology;
    }
}
