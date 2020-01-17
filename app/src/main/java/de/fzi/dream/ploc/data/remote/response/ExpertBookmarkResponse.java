package de.fzi.dream.ploc.data.remote.response;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import de.fzi.dream.ploc.data.remote.connection.GozerApi;
import de.fzi.dream.ploc.data.repository.ProfileRepository;
import de.fzi.dream.ploc.data.structure.entity.ExpertBookmark;

/**
 * Response message for the expert bookmark calls, see {@link GozerApi} and
 * {@link ProfileRepository}for usage
 *
 * @author Felix Melcher
 */
public class ExpertBookmarkResponse {

    @SerializedName("bookmarks")
    private List<ExpertBookmark> mBookmarks;

    /**
     * @return the retrieved list of bookmarks
     */
    @Nullable
    public List<ExpertBookmark> getBookmarks(){
        return mBookmarks;
    }
}
