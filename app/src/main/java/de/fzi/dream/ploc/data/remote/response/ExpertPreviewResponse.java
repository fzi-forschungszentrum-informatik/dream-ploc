package de.fzi.dream.ploc.data.remote.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import de.fzi.dream.ploc.data.remote.connection.GozerApi;
import de.fzi.dream.ploc.data.repository.ExpertRepository;
import de.fzi.dream.ploc.data.structure.entity.ExpertPreview;

/**
 * Response message for the expert preview feed call, see {@link GozerApi} and
 * {@link ExpertRepository}for usage
 *
 * @author Felix Melcher
 */
public class ExpertPreviewResponse {
    @SerializedName("offset")
    private int mOffset;
    @SerializedName("limit")
    private int mLimit;
    @SerializedName("experts")
    private List<ExpertPreview> mExperts;

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
    public List<ExpertPreview> getExperts() {
        return mExperts;
    }

}
