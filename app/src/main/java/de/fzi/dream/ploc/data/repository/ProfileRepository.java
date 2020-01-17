package de.fzi.dream.ploc.data.repository;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import de.fzi.dream.ploc.data.local.database.CacheDatabase;
import de.fzi.dream.ploc.data.remote.connection.GozerClient;
import de.fzi.dream.ploc.data.remote.request.CollectionRequest;
import de.fzi.dream.ploc.data.remote.request.ExpertProfileRequest;
import de.fzi.dream.ploc.data.remote.request.InterestRequest;
import de.fzi.dream.ploc.data.remote.request.ProfileRequest;
import de.fzi.dream.ploc.data.remote.request.ExpertRequest;
import de.fzi.dream.ploc.data.remote.request.RecordRequest;
import de.fzi.dream.ploc.data.remote.request.CollectionRecordRequest;
import de.fzi.dream.ploc.data.remote.response.GozerResponse;
import de.fzi.dream.ploc.data.remote.response.InterestResponse;
import de.fzi.dream.ploc.data.remote.response.ExpertBookmarkResponse;
import de.fzi.dream.ploc.data.remote.response.RecordBookmarkResponse;
import de.fzi.dream.ploc.data.remote.response.CollectionResponse;
import de.fzi.dream.ploc.data.remote.response.ProfileResponse;
import de.fzi.dream.ploc.data.structure.Subject;
import de.fzi.dream.ploc.data.structure.entity.Collection;
import de.fzi.dream.ploc.data.structure.entity.ExpertBookmark;
import de.fzi.dream.ploc.data.structure.entity.ExpertPreview;
import de.fzi.dream.ploc.data.structure.entity.ExpertProfile;
import de.fzi.dream.ploc.data.structure.entity.Interest;
import de.fzi.dream.ploc.data.structure.entity.RecordBookmark;
import de.fzi.dream.ploc.data.structure.entity.RecordPreview;
import de.fzi.dream.ploc.data.structure.entity.User;
import de.fzi.dream.ploc.data.structure.relation.RecordBookmarkCollection;
import de.fzi.dream.ploc.ui.callback.IOCallback;
import de.fzi.dream.ploc.utility.AppExecutors;
import de.fzi.dream.ploc.utility.Constants;
import de.fzi.dream.ploc.utility.network.NetworkBoundResource;
import de.fzi.dream.ploc.utility.network.Resource;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * The ProfileRepository implementation is the access point for all subject related data manipulation,
 * it returns data directly from the network or if it is cached from the local database.
 *
 * @author Felix Melcher
 */
public class ProfileRepository {
    /** Public class identifier tag for logging */
    public static final String TAG = ProfileRepository.class.getSimpleName();

    private static ProfileRepository INSTANCE = null;
    private final CacheDatabase mDatabase;

    private String preErrorMessage = "A network error occurred: ";

    private MutableLiveData<Integer> mRecordCount = new MutableLiveData<>();

    private ProfileRepository(CacheDatabase database) { mDatabase = database; }

    public static ProfileRepository getInstance(final CacheDatabase database) {
        if (INSTANCE == null) {
            synchronized (ProfileRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ProfileRepository(database);
                }
            }
        }
        return INSTANCE;
    }

    public LiveData<Integer> getRecordCount() {
        return mRecordCount;
    }

    public void authenticateUser(User user){
        GozerClient.createService(user.getGuid(), user.getSecret());
    }

    public LiveData<List<RecordBookmark>> readRecordBookmarksByCollectionID(int id) {
        return mDatabase.cacheDao().selectRecordBookmarksByCollectionIDLive(id);
    }

    public void assignCollectionToBookmark(int assignID, List<Integer> collectionIDs) {
        GozerClient.getAuthenticatedClient().updateBookmarkInCollections(new CollectionRecordRequest().setLink(assignID, collectionIDs.toArray(new Integer[0]))).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    AppExecutors.getInstance().diskIO().execute(() -> {
                        RecordBookmark bookmark = mDatabase.cacheDao().selectRecordBookmark(assignID);
                        bookmark.setCollectionIDs(collectionIDs);
                        mDatabase.cacheDao().insertRecordBookmark(bookmark);
                        collectionIDs.forEach(id -> mDatabase.cacheDao().insertRecordBookmarkCollection(new RecordBookmarkCollection(id, assignID)));

                    });
                } else {
                    onFailure(call, new Throwable(response.code() + " " + response.message()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Log.d(Constants.LOG_TAG, preErrorMessage  + t.getMessage());
            }
        });
    }

    // User CRUD
    public void createUserProfile(){
        String secret = Constants.DEBUG_SECRET;
        GozerClient.createNonAuthenticatedClient().createUserProfile(new ProfileRequest().setSecret(secret)).enqueue(new Callback<ProfileResponse>() {
            @Override
            public void onResponse(@NonNull Call<ProfileResponse> call, @NonNull Response<ProfileResponse> response) {
                if (response.body() != null && response.isSuccessful()) {
                    AppExecutors.getInstance().diskIO().execute(() -> mDatabase.cacheDao().insertUser(new User(response.body().getGuid(), secret)));
                } else {
                    onFailure(call, new Throwable(response.code() + " " + response.message()));
                }
            }
            @Override
            public void onFailure(@NonNull Call<ProfileResponse> call, @NonNull Throwable t) {
                Log.d(Constants.LOG_TAG, preErrorMessage + t.getMessage());
            }
        });
    }

    public LiveData<User> readUserProfile() {
        return mDatabase.cacheDao().selectUser();
    }

    public void deleteUser() {
        mRecordCount.postValue(0);
        AppExecutors.getInstance().diskIO().execute(mDatabase::clearDatabase);
    }

    // Interest CRUD
    public void createInterest(Subject newInterest, IOCallback callback) {
        GozerClient.getAuthenticatedClient().createInterest(new InterestRequest().setID(newInterest.getId())).enqueue(new Callback<InterestResponse>() {
            @Override
            public void onResponse(@NonNull Call<InterestResponse> call, @NonNull Response<InterestResponse> response) {
                if (response.body() != null && response.isSuccessful()) {
                    AppExecutors.getInstance().diskIO().execute(() -> {
                        mRecordCount.postValue(response.body().getRecordCount());
                        mDatabase.cacheDao().insertInterest(new Interest(newInterest.getId(), newInterest.getKeyword()));
                    });
                } else {
                    onFailure(call, new Throwable(response.code() + " " + response.message()));
                }
                callback.onCallback(response.isSuccessful());
            }

            @Override
            public void onFailure(@NonNull Call<InterestResponse> call, @NonNull Throwable t) {
                Log.d(Constants.LOG_TAG, preErrorMessage + t.getMessage());
            }
        });
    }


    public LiveData<Resource<List<Interest>>> readInterests(){
        return new NetworkBoundResource<List<Interest>, InterestResponse>(AppExecutors.getInstance()){
            @Override
            protected void saveCallResult(@NonNull InterestResponse item) {
                if(item.getInterests() != null){
                    mRecordCount.postValue(item.getRecordCount());
                    AppExecutors.getInstance().diskIO().execute(() -> item.getInterests()
                            .forEach(interest -> mDatabase.cacheDao().insertInterest(interest)));
                }
            }

            @Override
            protected boolean shouldFetch(@Nullable List<Interest> data) {
                return true;
            }

            @NonNull
            @Override
            protected LiveData<List<Interest>> loadFromDb() {
                return mDatabase.cacheDao().selectInterests();
            }

            @NonNull
            @Override
            protected LiveData<GozerResponse<InterestResponse>> createCall() {
                return GozerClient.getAuthenticatedClient().readInterests();
            }
        }.getAsLiveData();
    }


    public void deleteInterest(int id, IOCallback callback) {
        GozerClient.getAuthenticatedClient().deleteInterest(new InterestRequest().setID(id)).enqueue(new Callback<InterestResponse>() {
            @Override
            public void onResponse(@NonNull Call<InterestResponse> call, @NonNull Response<InterestResponse> response) {
                if (response.body() != null && response.isSuccessful()) {
                    mRecordCount.postValue(response.body().getRecordCount());
                    AppExecutors.getInstance().diskIO().execute(() -> {
                        mDatabase.cacheDao().deleteInterest(id);
                    });
                    callback.onCallback(true);
                } else {
                    onFailure(call, new Throwable(response.code() + " " + response.message()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<InterestResponse> call, @NonNull Throwable t) {
                Log.d(Constants.LOG_TAG, preErrorMessage + t.getMessage());
            }
        });
    }


    // RecordBookmark CRUD
    public void createRecordBookmark(RecordPreview preview, IOCallback callback) {
        GozerClient.getAuthenticatedClient().createRecordBookmark(new RecordRequest().setID(preview.getId())).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    AppExecutors.getInstance().diskIO().execute(() -> {
                        mDatabase.cacheDao().deleteRecordPreview(preview);
                        mDatabase.cacheDao().insertRecordBookmark(preview.previewToBookmark());
                        callback.onCallback(true);
                    });
                } else {
                    onFailure(call, new Throwable(response.code() + " " + response.message()));
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(Constants.LOG_TAG, preErrorMessage + t.getMessage());
            }
        });
    }

    public LiveData<Resource<List<RecordBookmark>>> readRecordBookmarks(){
        return new NetworkBoundResource<List<RecordBookmark>, RecordBookmarkResponse>(AppExecutors.getInstance()){
            @Override
            protected void saveCallResult(@NonNull RecordBookmarkResponse item) {
                if(item.getBookmarks() != null){
                    AppExecutors.getInstance().diskIO().execute(() -> {
                        item.getBookmarks().forEach(bookmark -> {
                            mDatabase.cacheDao().insertRecordBookmark(bookmark);
                            List<Collection> collections = mDatabase.cacheDao().selectCollections();
                            bookmark.getCollectionIDs().forEach(collectionID -> {
                                if(collections.contains(collectionID)){
                                    mDatabase.cacheDao().insertRecordBookmarkCollection(new RecordBookmarkCollection(collectionID, bookmark.getId()));
                                }
                            });
                        });
                    });
                }
            }

            @Override
            protected boolean shouldFetch(@Nullable List<RecordBookmark> data) {
                return true;
            }

            @NonNull
            @Override
            protected LiveData<List<RecordBookmark>> loadFromDb() {
                return mDatabase.cacheDao().selectRecordBookmarksLive();
            }

            @NonNull
            @Override
            protected LiveData<GozerResponse<RecordBookmarkResponse>> createCall() {
                return GozerClient.getAuthenticatedClient().readRecordBookmarks();
            }
        }.getAsLiveData();
    }



    public void deleteRecordBookmark(int id, IOCallback callback) {
        GozerClient.getAuthenticatedClient().deleteRecordBookmark(new RecordRequest().setID(id)).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    AppExecutors.getInstance().diskIO().execute(() -> {
                        mDatabase.cacheDao().deleteRecordBookmark(id);
                        callback.onCallback(true);
                    });
                } else {
                    callback.onCallback(false);
                    onFailure(call, new Throwable(response.code() + " " + response.message()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                onFailure(call, new Throwable(preErrorMessage));
            }
        });
    }

    public void deleteExpertBookmark(int id, IOCallback callback) {
        GozerClient.getAuthenticatedClient().deleteExpertBookmark(new ExpertRequest().setID(id)).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    AppExecutors.getInstance().diskIO().execute(() -> {
                        mDatabase.cacheDao().deleteExpertBookmarkByID(id);
                        callback.onCallback(true);
                    });

                } else {
                    callback.onCallback(false);
                    onFailure(call, new Throwable(response.code() + " " + response.message()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Log.d(Constants.LOG_TAG, preErrorMessage + t.getMessage());
            }
        });
    }

    // ExpertBookmark CRUD
    public void createExpertBookmark(ExpertPreview expertPreview, IOCallback callback) {
        GozerClient.getAuthenticatedClient().createExpertBookmark(new ExpertRequest().setID(expertPreview.getId())).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    AppExecutors.getInstance().diskIO().execute(() -> {
                        mDatabase.cacheDao().insertExpertBookmark(expertPreview.toBookmark());
                        callback.onCallback(true);
                    });
                } else {
                    onFailure(call, new Throwable(response.code() + " " + response.message()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Log.d(Constants.LOG_TAG, preErrorMessage  + t.getMessage());
            }
        });
    }


    public LiveData<Resource<List<ExpertBookmark>>> readExpertBookmarks(){
        return new NetworkBoundResource<List<ExpertBookmark>, ExpertBookmarkResponse>(AppExecutors.getInstance()){
            @Override
            protected void saveCallResult(@NonNull ExpertBookmarkResponse item) {
                if(item.getBookmarks() != null){
                    AppExecutors.getInstance().diskIO().execute(() -> item.getBookmarks().forEach(bookmark ->   mDatabase.cacheDao().insertExpertBookmark(bookmark)));
                }
            }

            @Override
            protected boolean shouldFetch(@Nullable List<ExpertBookmark> data) {
                return true;
            }

            @NonNull
            @Override
            protected LiveData<List<ExpertBookmark>> loadFromDb() {
                return mDatabase.cacheDao().selectExpertBookmarksLive();
            }

            @NonNull
            @Override
            protected LiveData<GozerResponse<ExpertBookmarkResponse>> createCall() {
                return GozerClient.getAuthenticatedClient().readExpertBookmarks();
            }
        }.getAsLiveData();
    }


    // Disinterest CRUD
    public void createRecordDisinterest(int recordID,  IOCallback callback) {
        GozerClient.getAuthenticatedClient().createDisinterest(new RecordRequest().setID(recordID)).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    AppExecutors.getInstance().diskIO().execute(() -> {
                        mDatabase.cacheDao().deleteRecordPreviewByID(recordID);
                        callback.onCallback(true);
                    });
                } else {
                    onFailure(call, new Throwable(response.code() + " " + response.message()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, Throwable t) {
                Log.d(Constants.LOG_TAG, preErrorMessage  + t.getMessage());
            }
        });
    }


    // Collection CRUD
    public void createCollection(String name) {
        GozerClient.getAuthenticatedClient().createCollection(new CollectionRequest().setName(name)).enqueue(new Callback<CollectionResponse>() {
            @Override
            public void onResponse(@NonNull Call<CollectionResponse> call,@NonNull Response<CollectionResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    AppExecutors.getInstance().diskIO().execute(() ->
                            mDatabase.cacheDao().insertCollection(new Collection(response.body().getCollectionID(), name)));
                } else {
                    onFailure(call, new Throwable(response.code() + " " + response.message()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<CollectionResponse> call, Throwable t) {
                Log.d(Constants.LOG_TAG, preErrorMessage  + t.getMessage());
            }
        });
    }

    public LiveData<Resource<List<Collection>>> readCollections(){
        return new NetworkBoundResource<List<Collection>, CollectionResponse>(AppExecutors.getInstance()){
            @Override
            protected void saveCallResult(@NonNull CollectionResponse item) {
                if(item.getCollections() != null){
                    AppExecutors.getInstance().diskIO().execute(() -> item.getCollections().forEach(collection -> mDatabase.cacheDao().insertCollection(collection)));
                }
            }

            @Override
            protected boolean shouldFetch(@Nullable List<Collection> data) {
                return true;
            }

            @NonNull
            @Override
            protected LiveData<List<Collection>> loadFromDb() {
                return mDatabase.cacheDao().selectCollectionsLive();
            }

            @NonNull
            @Override
            protected LiveData<GozerResponse<CollectionResponse>> createCall() {
                return GozerClient.getAuthenticatedClient().readCollections();
            }
        }.getAsLiveData();
    }


    public void updateCollection(Collection c) {
        GozerClient.getAuthenticatedClient().updateCollection(
                new CollectionRequest().setID(c.getId()).setName(c.getName()))
                .enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    AppExecutors.getInstance().diskIO().execute(() ->   mDatabase.cacheDao().insertCollection(c));
                } else {
                    onFailure(call, new Throwable(preErrorMessage));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Log.d(Constants.LOG_TAG, preErrorMessage + t.getMessage());
            }
        });
    }

    public void deleteCollection(Collection collection) {
        GozerClient.getAuthenticatedClient().deleteCollection(new CollectionRequest().setID(collection.getId())).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call,@NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    AppExecutors.getInstance().diskIO().execute(() -> {
                        mDatabase.cacheDao().deleteCollection(collection);
                    });
                } else {
                    onFailure(call, new Throwable(response.code() + " " + response.message()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, Throwable t) {
                Log.d(Constants.LOG_TAG, preErrorMessage + t.getMessage());
            }
        });
    }

    public void createExpertProfile(String orcid){
        GozerClient.createNonAuthenticatedClient().createExpertProfile(new ExpertProfileRequest().setORCID(orcid)).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.body() != null && response.isSuccessful()) {
                    AppExecutors.getInstance().diskIO().execute(() -> mDatabase.cacheDao().insertExpertProfile(new ExpertProfile(orcid)));
                } else {
                    onFailure(call, new Throwable(response.code() + " " + response.message()));
                }
            }
            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Log.d(Constants.LOG_TAG, preErrorMessage + t.getMessage());
            }
        });
    }

    public LiveData<ExpertProfile> readExpertProfile() {
        return mDatabase.cacheDao().selectExpertProfile();
    }

}
