package de.fzi.dream.ploc.data.remote.response;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import de.fzi.dream.ploc.data.remote.connection.GozerApi;
import de.fzi.dream.ploc.data.repository.ProfileRepository;
import de.fzi.dream.ploc.data.structure.entity.RecordBookmark;
/**
 * Response message for the record bookmark calls, see {@link GozerApi} and
 * {@link ProfileRepository}for usage
 *
 * @author Felix Melcher
 */
public class RecordBookmarkResponse {

    @SerializedName("bookmarks")
    private List<RecordBookmark> mBookmarks;

    /**
     * @return the retrieved list of bookmarks
     */
    @Nullable
    public List<RecordBookmark> getBookmarks(){
        return mBookmarks;
    }

}