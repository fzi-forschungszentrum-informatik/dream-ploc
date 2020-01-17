package de.fzi.dream.ploc.data.remote.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import de.fzi.dream.ploc.data.remote.connection.GozerApi;
import de.fzi.dream.ploc.data.repository.RecordRepository;
import de.fzi.dream.ploc.data.structure.entity.RecordPreview;

/**
 * Response message for the record preview feed call, see {@link GozerApi} and
 * {@link RecordRepository}for usage
 *
 * @author Felix Melcher
 */
public class RecordPreviewResponse {
    @SerializedName("offset")
    private int mOffset;
    @SerializedName("limit")
    private int mLimit;
    @SerializedName("records")
    private List<RecordPreview> mRecords;

    /**
     * @return the current offset of this call
     */
    public int getOffset() {
        return mOffset;
    }

    /**
     * @return the current limit of this call
     */
    public int getLimit() {
        return mLimit;
    }

    /**
     * @return a list of experts, matching the limit and offset
     */
    public List<RecordPreview> getRecords() {
        return mRecords;
    }

}
