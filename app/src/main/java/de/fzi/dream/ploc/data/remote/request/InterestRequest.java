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
public class InterestRequest {
    @SerializedName("subject_id")
    private int mInterestSubjectID;

    /**
     * Set subject identifier of the requested interest.
     *
     * @param id of the request interest/subject.
     * @return InterestRequest instance of this message
     */
    public InterestRequest setID(int id){
        mInterestSubjectID = id;
        return this;
    }

}

