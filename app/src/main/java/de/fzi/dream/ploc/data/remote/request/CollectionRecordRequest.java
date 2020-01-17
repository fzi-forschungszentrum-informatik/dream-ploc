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
public class CollectionRecordRequest {
    @SerializedName("record_id")
    private int mRecordID;
    @SerializedName("collection_ids")
    private Integer[] mCollectionIDs;

    /**
     * Set a link between one record and n collections.
     *
     * @param recordID Record to be added to collection
     * @param collectionIDs Collections to which the record should be added
     * @return CollectionRecordRequest instance of this message
     */
    public CollectionRecordRequest setLink(int recordID, Integer[] collectionIDs){
        mRecordID = recordID;
        mCollectionIDs = collectionIDs;
        return this;
    }

    public int getRecordID() {
        return mRecordID;
    }

    public void setRecordID(int mRecordID) {
        this.mRecordID = mRecordID;
    }

    public Integer[] getCollectionIDs() {
        return mCollectionIDs;
    }

    public void setCollectionIDs(Integer[] mCollectionIDs) {
        this.mCollectionIDs = mCollectionIDs;
    }
}
