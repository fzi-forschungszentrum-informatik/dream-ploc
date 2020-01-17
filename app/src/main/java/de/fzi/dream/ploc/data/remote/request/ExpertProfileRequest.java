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
public class ExpertProfileRequest {
    @SerializedName("orcid")
    private String mORCID;

    /**
     * Set an ORCID to register the user as an expert.
     *
     * @param orcid of the current user.
     * @return ExpertProfileRequest instance of this message
     */
    public ExpertProfileRequest setORCID(String orcid){
        mORCID = orcid;
        return this;
    }

    public String getORCID() {
        return mORCID;
    }
}

