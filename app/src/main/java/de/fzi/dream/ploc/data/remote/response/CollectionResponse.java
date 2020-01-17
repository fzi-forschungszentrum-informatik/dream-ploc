package de.fzi.dream.ploc.data.remote.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import de.fzi.dream.ploc.data.remote.connection.GozerApi;
import de.fzi.dream.ploc.data.repository.ProfileRepository;
import de.fzi.dream.ploc.data.structure.entity.Collection;

/**
 * Response message for all collection calls, see {@link GozerApi} and
 * {@link ProfileRepository}for usage
 *
 * @author Felix Melcher
 */
public class CollectionResponse {

    /**
     * Holds the backend collection id of the newly created collection.
     * Responded in the createCollection(...) call.
     */
    @SerializedName("collection_id")
    private int mCollectionID;

    /**
     * Holds all user collections.
     * Responded in the readCollection() call.
     */
    @SerializedName("collections")
    private List<Collection> mCollections;

    /**
     * @return the retrieved collection ID
     */
    public int getCollectionID() {
        return mCollectionID;
    }

    /**
     * @return the retrieved collections
     */
    public List<Collection> getCollections(){
        return mCollections;
    }
}