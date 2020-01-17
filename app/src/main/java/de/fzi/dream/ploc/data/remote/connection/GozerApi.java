package de.fzi.dream.ploc.data.remote.connection;

import androidx.lifecycle.LiveData;

import de.fzi.dream.ploc.data.remote.paging.PageKey;
import de.fzi.dream.ploc.data.remote.request.CollectionRequest;
import de.fzi.dream.ploc.data.remote.request.ExpertProfileRequest;
import de.fzi.dream.ploc.data.remote.request.FeedbackRequest;
import de.fzi.dream.ploc.data.remote.request.InterestRequest;
import de.fzi.dream.ploc.data.remote.request.ProfileRequest;
import de.fzi.dream.ploc.data.remote.request.ExpertRequest;
import de.fzi.dream.ploc.data.remote.request.RecordRequest;
import de.fzi.dream.ploc.data.remote.request.CollectionRecordRequest;
import de.fzi.dream.ploc.data.remote.response.FeedbackDetailResponse;
import de.fzi.dream.ploc.data.remote.response.FeedbackPreviewResponse;
import de.fzi.dream.ploc.data.remote.response.GozerResponse;
import de.fzi.dream.ploc.data.remote.response.InterestResponse;
import de.fzi.dream.ploc.data.remote.response.ExpertBookmarkResponse;
import de.fzi.dream.ploc.data.remote.response.RecordBookmarkResponse;
import de.fzi.dream.ploc.data.remote.response.CollectionResponse;
import de.fzi.dream.ploc.data.remote.response.ProfileResponse;
import de.fzi.dream.ploc.data.remote.response.ExpertPreviewResponse;
import de.fzi.dream.ploc.data.remote.response.RecordPreviewResponse;
import de.fzi.dream.ploc.data.remote.response.SubjectResponse;
import de.fzi.dream.ploc.data.structure.ExpertDetail;
import de.fzi.dream.ploc.data.structure.Feedback;
import de.fzi.dream.ploc.data.structure.RecordDetail;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

import static de.fzi.dream.ploc.utility.Constants.API_ROUTE_CREATE_BOOKMARK_EXPERT;
import static de.fzi.dream.ploc.utility.Constants.API_ROUTE_CREATE_BOOKMARK_RECORD;
import static de.fzi.dream.ploc.utility.Constants.API_ROUTE_CREATE_COLLECTION;
import static de.fzi.dream.ploc.utility.Constants.API_ROUTE_CREATE_DISINTEREST;
import static de.fzi.dream.ploc.utility.Constants.API_ROUTE_CREATE_EXPERT_PROFILE;
import static de.fzi.dream.ploc.utility.Constants.API_ROUTE_CREATE_FEEDBACK;
import static de.fzi.dream.ploc.utility.Constants.API_ROUTE_CREATE_INTEREST;
import static de.fzi.dream.ploc.utility.Constants.API_ROUTE_CREATE_USER;
import static de.fzi.dream.ploc.utility.Constants.API_ROUTE_DELETE_BOOKMARK_EXPERT;
import static de.fzi.dream.ploc.utility.Constants.API_ROUTE_DELETE_BOOKMARK_RECORD;
import static de.fzi.dream.ploc.utility.Constants.API_ROUTE_DELETE_COLLECTION;
import static de.fzi.dream.ploc.utility.Constants.API_ROUTE_DELETE_EXPERT_PROFILE;
import static de.fzi.dream.ploc.utility.Constants.API_ROUTE_DELETE_INTEREST;
import static de.fzi.dream.ploc.utility.Constants.API_ROUTE_DELETE_USER;
import static de.fzi.dream.ploc.utility.Constants.API_ROUTE_READ_BOOKMARK_EXPERTS;
import static de.fzi.dream.ploc.utility.Constants.API_ROUTE_READ_BOOKMARK_RECORDS;
import static de.fzi.dream.ploc.utility.Constants.API_ROUTE_READ_COLLECTIONS;
import static de.fzi.dream.ploc.utility.Constants.API_ROUTE_READ_EXPERTS;
import static de.fzi.dream.ploc.utility.Constants.API_ROUTE_READ_EXPERT_DETAIL;
import static de.fzi.dream.ploc.utility.Constants.API_ROUTE_READ_EXPERT_PROFILE;
import static de.fzi.dream.ploc.utility.Constants.API_ROUTE_READ_FEEDBACK_DETAIL;
import static de.fzi.dream.ploc.utility.Constants.API_ROUTE_READ_FEEDBACK_FEED;
import static de.fzi.dream.ploc.utility.Constants.API_ROUTE_READ_INTERESTS;
import static de.fzi.dream.ploc.utility.Constants.API_ROUTE_READ_RECORD_DETAIL;
import static de.fzi.dream.ploc.utility.Constants.API_ROUTE_READ_RECORD_FEED;
import static de.fzi.dream.ploc.utility.Constants.API_ROUTE_READ_SUBJECTS;
import static de.fzi.dream.ploc.utility.Constants.API_ROUTE_SEARCH_EXPERT_FEED;
import static de.fzi.dream.ploc.utility.Constants.API_ROUTE_SEARCH_RECORD_FEED;
import static de.fzi.dream.ploc.utility.Constants.API_ROUTE_UPDATE_BOOKMARK_COLLECTIONS;
import static de.fzi.dream.ploc.utility.Constants.API_ROUTE_UPDATE_COLLECTION;

/**
 * The GozerApi defines the interface methods to communicate via http
 * with the gozer backend service.
 *
 * @author Felix Melcher
 */
public interface GozerApi {

    /*
     * Profile Methods
     */
    @Headers({"Content-type: application/json",
            "Accept: */*"})
    @POST(API_ROUTE_CREATE_USER)
    Call<ProfileResponse> createUserProfile(@Body ProfileRequest body);

    @Headers({"Content-type: application/json",
            "Accept: */*"})
    @POST(API_ROUTE_DELETE_USER)
    Call<ResponseBody> deleteProfile();


    /*
     * Subject Methods
     */
    @Headers({"Content-type: application/json",
            "Accept: */*"})
    @POST(API_ROUTE_READ_SUBJECTS)
    LiveData<GozerResponse<SubjectResponse>> readSubjects();


    /*
     * Interest Methods
     */
    @Headers({"Content-type: application/json",
            "Accept: */*"})
    @POST(API_ROUTE_CREATE_INTEREST)
    Call<InterestResponse> createInterest(@Body InterestRequest body);

    @Headers({"Content-type: application/json",
            "Accept: */*"})
    @POST(API_ROUTE_READ_INTERESTS)
    LiveData<GozerResponse<InterestResponse>> readInterests();

    @Headers({"Content-type: application/json",
            "Accept: */*"})
    @POST(API_ROUTE_DELETE_INTEREST)
    Call<InterestResponse> deleteInterest(@Body InterestRequest body);


    /*
     * Record Methods
     */
    @Headers({"Content-type: application/json",
            "Accept: */*"})
    @POST(API_ROUTE_READ_RECORD_FEED)
    Call<RecordPreviewResponse> readRecordPreview(@Body PageKey body);

    @Headers({"Content-type: application/json",
            "Accept: */*"})
    @POST(API_ROUTE_SEARCH_RECORD_FEED)
    Call<RecordPreviewResponse> searchRecordPreview(@Body PageKey body);

    @Headers({"Content-type: application/json",
            "Accept: */*"})
    @POST(API_ROUTE_READ_RECORD_DETAIL)
    Call<RecordDetail> readRecordDetail(@Body RecordRequest body);

    @Headers({"Content-type: application/json",
            "Accept: */*"})
    @POST(API_ROUTE_CREATE_DISINTEREST)
    Call<ResponseBody> createDisinterest(@Body RecordRequest body);


    /*
     * Expert Methods
     */
    @Headers({"Content-type: application/json",
            "Accept: */*"})
    @POST(API_ROUTE_READ_EXPERTS)
    Call<ExpertPreviewResponse> readExpertPreview(@Body PageKey body);

    @Headers({"Content-type: application/json",
            "Accept: */*"})
    @POST(API_ROUTE_READ_EXPERT_DETAIL)
    Call<ExpertDetail> readExpertDetail(@Body ExpertRequest body);

    @Headers({"Content-type: application/json",
            "Accept: */*"})
    @POST(API_ROUTE_SEARCH_EXPERT_FEED)
    Call<ExpertPreviewResponse> searchExpertPreview(@Body PageKey body);


    /*
     * Feedback Methods
     */
    @Headers({"Content-type: application/json",
            "Accept: */*"})
    @POST(API_ROUTE_CREATE_FEEDBACK)
    Call<ResponseBody> createFeedback(@Body Feedback body);

    @Headers({"Content-type: application/json",
            "Accept: */*"})
    @POST(API_ROUTE_READ_FEEDBACK_FEED)
    Call<FeedbackPreviewResponse> readFeedbackPreview(@Body PageKey body);

    @Headers({"Content-type: application/json",
            "Accept: */*"})
    @POST(API_ROUTE_READ_FEEDBACK_DETAIL)
    Call<FeedbackDetailResponse> readFeedbackDetail(@Body RecordRequest body);


    /*
     * Bookmarks (Record and Expert) Methods
     */
    @Headers({"Content-type: application/json",
            "Accept: */*"})
    @POST(API_ROUTE_CREATE_BOOKMARK_RECORD)
    Call<ResponseBody> createRecordBookmark(@Body RecordRequest body);

    @Headers({"Content-type: application/json",
            "Accept: */*"})
    @POST(API_ROUTE_CREATE_BOOKMARK_EXPERT)
    Call<ResponseBody> createExpertBookmark(@Body ExpertRequest body);

    @Headers({"Content-type: application/json",
            "Accept: */*"})
    @POST(API_ROUTE_READ_BOOKMARK_RECORDS)
    LiveData<GozerResponse<RecordBookmarkResponse>> readRecordBookmarks();

    @Headers({"Content-type: application/json",
            "Accept: */*"})
    @POST(API_ROUTE_READ_BOOKMARK_EXPERTS)
    LiveData<GozerResponse<ExpertBookmarkResponse>> readExpertBookmarks();

    @Headers({"Content-type: application/json",
            "Accept: */*"})
    @POST(API_ROUTE_DELETE_BOOKMARK_RECORD)
    Call<ResponseBody> deleteRecordBookmark(@Body RecordRequest body);

    @Headers({"Content-type: application/json",
            "Accept: */*"})
    @POST(API_ROUTE_DELETE_BOOKMARK_EXPERT)
    Call<ResponseBody> deleteExpertBookmark(@Body ExpertRequest body);


    /*
    * Collection Methods
    */
    @Headers({"Content-type: application/json",
            "Accept: */*"})
    @POST(API_ROUTE_CREATE_COLLECTION)
    Call<CollectionResponse> createCollection(@Body CollectionRequest body);

    @Headers({"Content-type: application/json",
            "Accept: */*"})
    @POST(API_ROUTE_READ_COLLECTIONS)
    LiveData<GozerResponse<CollectionResponse>> readCollections();

    @Headers({"Content-type: application/json",
            "Accept: */*"})
    @POST(API_ROUTE_UPDATE_COLLECTION)
    Call<ResponseBody> updateCollection(@Body CollectionRequest requestUpdateCollection);

    @Headers({"Content-type: application/json",
            "Accept: */*"})
    @POST(API_ROUTE_UPDATE_BOOKMARK_COLLECTIONS)
    Call<ResponseBody> updateBookmarkInCollections(@Body CollectionRecordRequest body);

    @Headers({"Content-type: application/json",
            "Accept: */*"})
    @POST(API_ROUTE_DELETE_COLLECTION)
    Call<ResponseBody> deleteCollection(@Body CollectionRequest body);

    /*
     * Expert Profile Methods
     */
    @Headers({"Content-type: application/json",
            "Accept: */*"})
    @POST(API_ROUTE_CREATE_EXPERT_PROFILE)
    Call<ResponseBody> createExpertProfile(@Body ExpertProfileRequest body);

    @Headers({"Content-type: application/json",
            "Accept: */*"})
    @POST(API_ROUTE_READ_EXPERT_PROFILE)
    LiveData<ResponseBody> readExpertProfile();

    @Headers({"Content-type: application/json",
            "Accept: */*"})
    @POST(API_ROUTE_DELETE_EXPERT_PROFILE)
    Call<ResponseBody> deleteExpertProfile();

}