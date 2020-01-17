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
public class ExpertRequest {
    @SerializedName("expert_id")
    private int mExpertID;

    /**
     * Set identifier of the requested expert.
     *
     * @param id of the requested Expert
     * @return ExpertRequest instance of this message
     */
    public ExpertRequest setID(int id){
        mExpertID = id;
        return this;
    }
}
