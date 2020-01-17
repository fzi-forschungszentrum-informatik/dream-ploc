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
public class ProfileRequest {
    @SerializedName("secret")
    private String mSecret;

    /**
     * Set the secret used for the current user to create a profile.
     *
     * @param secret of the current user.
     * @return ProfileRequest instance of this message
     */
    public ProfileRequest setSecret(String secret){
        mSecret = secret;
        return this;
    }

}

