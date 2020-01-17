package de.fzi.dream.ploc.data.remote.response;

import com.google.gson.annotations.SerializedName;

import de.fzi.dream.ploc.data.remote.connection.GozerApi;
import de.fzi.dream.ploc.data.repository.ProfileRepository;

/**
 * Response message for the createUserProfile call, see {@link GozerApi} and
 * {@link ProfileRepository}for usage
 *
 * @author Felix Melcher
 */
public class ProfileResponse {
    @SerializedName("guid")
    private String mGUID;

    /**
     * @return the GUID of the newly created user
     */
    public String getGuid() {
        return mGUID;
    }
}