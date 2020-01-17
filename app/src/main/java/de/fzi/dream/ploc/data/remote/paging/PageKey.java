package de.fzi.dream.ploc.data.remote.paging;

import com.google.gson.annotations.SerializedName;

/**
 * The PageKey is the data struct to page the different feeds on the backend and within the android
 * paging library.
 * See the page key creation in the {@link ExpertPreviewRemotePagingSource} and
 * {@link RecordPreviewRemotePagingSource} class.
 *
 * @author Felix Melcher
 */
public class PageKey {

    /** Public class identifier tag for logging */
    public static final String TAG = PageKey.class.getSimpleName();

    @SerializedName("offset")
    private int mOffset;
    @SerializedName("search_term")
    private String mSearchTerm;
    @SerializedName("limit")
    private int mLimit;

    /**
     * Constructor
     *
     * @param offset indicates the position where the response should start
     * @param searchTerm is used to query the feed for a specific term
     */
    PageKey(int offset, String searchTerm, int limit) {
        mOffset = offset;
        mLimit = limit;
        if(searchTerm != null){
            mSearchTerm = searchTerm;
        }
    }

    /**
     * @return int the offset of the current key
     */
    public int getOffset() {
        return mOffset;
    }

    /**
     * @return String the search term of the current key, null if no search term inserted
     */
    public String getSearchTerm() { return mSearchTerm; }

    /**
     * @param offset int value to be add up the current offset
     * @return the next {@link PageKey} with the given offset
     */
    public PageKey getNextPageKey(int offset) {
        return new PageKey(mOffset + offset, mSearchTerm, mLimit);
    }
}
