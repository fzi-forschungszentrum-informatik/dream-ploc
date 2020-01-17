package de.fzi.dream.ploc.data.remote.response;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import de.fzi.dream.ploc.data.remote.connection.GozerApi;

import de.fzi.dream.ploc.data.repository.ProfileRepository;
import de.fzi.dream.ploc.data.structure.entity.Interest;

/**
 * Response message for all interest calls, see {@link GozerApi} and
 * {@link ProfileRepository}for usage
 *
 * @author Felix Melcher
 */
public class InterestResponse {

    /**
     * Holds the current record count of the users feed after adding or deleting an interest
     * Responded in the readInterest() and createInterest(...) call.
     */
    @SerializedName("record_count")
    private int mRecordCount;

    /**
     * Holds all interests defined by the user.
     * Only responded in the readInterest() call.
     */
    @SerializedName("subjects")
    private List<Interest> mInterests;


    @Nullable
    public List<Interest> getInterests(){
        return mInterests;
    }

    public int getRecordCount() {
        return mRecordCount;
    }

}