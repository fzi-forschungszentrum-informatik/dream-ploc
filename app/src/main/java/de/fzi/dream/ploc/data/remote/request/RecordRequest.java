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
public class RecordRequest {
    @SerializedName("record_id")
    private int mRecordID;

    /**
     * Set the identifier for the requested record.
     *
     * @param id of the requested record.
     * @return RecordRequest instance of this message
     */
    public RecordRequest setID(int id){
        mRecordID = id;
        return this;
    }

}
