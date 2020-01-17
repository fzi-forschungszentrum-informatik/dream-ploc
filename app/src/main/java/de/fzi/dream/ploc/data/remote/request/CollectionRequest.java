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
public class CollectionRequest {
    @SerializedName("collection_id")
    private int mCollectionID;
    @SerializedName("name")
    private String mCollectionName;

    /**
     * Set identifier of the requested collection.
     *
     * @param id of the request record.
     * @return CollectionRequest instance of this message
     */
    public CollectionRequest setID(int id){
        mCollectionID = id;
        return this;
    }

    /**
     * Set name of the requested collection.
     *
     * @param name of the request record.
     * @return CollectionRequest instance of this message
     */
    public CollectionRequest setName(String name){
        mCollectionName = name;
        return this;
    }

    public int getCollectionID() {
        return mCollectionID;
    }

    public void setCollectionID(int mCollectionID) {
        this.mCollectionID = mCollectionID;
    }

    public String getCollectionName() {
        return mCollectionName;
    }

    public void setCollectionName(String mCollectionName) {
        this.mCollectionName = mCollectionName;
    }
}
