package de.fzi.dream.ploc.data.remote.response;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import de.fzi.dream.ploc.data.remote.connection.GozerApi;
import de.fzi.dream.ploc.data.repository.ProfileRepository;
import de.fzi.dream.ploc.data.structure.Feedback;

/**
 * Response message for all interest calls, see {@link GozerApi} and
 * {@link ProfileRepository}for usage
 *
 * @author Felix Melcher
 */
public class FeedbackDetailResponse {

    /**
     * Holds all feedbacks given by the experts for this record.
     */
    @SerializedName("feedbacks")
    private List<Feedback> mFeedbacks;


    @Nullable
    public List<Feedback> getFeedbacks(){
        return mFeedbacks;
    }

}