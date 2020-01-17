package de.fzi.dream.ploc.utility.network;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import de.fzi.dream.ploc.data.remote.response.GozerResponse;
import de.fzi.dream.ploc.utility.AppExecutors;

public abstract class NetworkBoundResource<CacheObject, RequestObject> {

    public static final String TAG = "NetworkBoundResource";

    private AppExecutors mAppExecutors;
    private MediatorLiveData<Resource<CacheObject>> mResults = new MediatorLiveData<>();

    protected NetworkBoundResource(AppExecutors appExecutors) {
        this.mAppExecutors = appExecutors;
        init();
    }

    private void init() {
        mResults.setValue(Resource.loading(null));
        final LiveData<CacheObject> dbSource = loadFromDb();
        mResults.addSource(dbSource, cacheObject -> {
            mResults.removeSource(dbSource);
            if (shouldFetch(cacheObject)) {
                fetchFromNetwork(dbSource);
            } else {
                mResults.addSource(dbSource, cacheObject1 -> setValue(Resource.success(cacheObject1)));
            }
        });
    }

    private void fetchFromNetwork(final LiveData<CacheObject> dataSource) {
        mResults.addSource(dataSource, cacheObject -> setValue(Resource.loading(cacheObject)));
        final LiveData<GozerResponse<RequestObject>> apiResponse = createCall();
        mResults.addSource(apiResponse, requestObjectApiResponse -> {
            mResults.removeSource(dataSource);
            mResults.removeSource(apiResponse);
            if (requestObjectApiResponse instanceof GozerResponse.ApiSuccessResponse) {
                mAppExecutors.diskIO().execute(() -> {
                    saveCallResult((RequestObject) processResponse((GozerResponse.ApiSuccessResponse) requestObjectApiResponse));
                    mAppExecutors.mainThread().execute(() -> mResults.addSource(loadFromDb(), cacheObject -> setValue(Resource.success(cacheObject))));
                });
            } else if (requestObjectApiResponse instanceof GozerResponse.ApiEmptyResponse) {
                mAppExecutors.mainThread().execute(() -> mResults.addSource(loadFromDb(), cacheObject -> setValue(Resource.success(cacheObject))));
            } else if (requestObjectApiResponse instanceof GozerResponse.ApiErrorResponse) {
                mResults.addSource(dataSource, cacheObject -> setValue(
                        Resource.error(((GozerResponse.ApiErrorResponse) requestObjectApiResponse).getErrorMessage(), cacheObject)
                ));
            }
        });
    }

    private CacheObject processResponse(GozerResponse.ApiSuccessResponse response) {
        return (CacheObject) response.getBody();
    }

    private void setValue(Resource<CacheObject> newValue) {
        if (mResults.getValue() != newValue) {
            mResults.setValue(newValue);
        }
    }

    @WorkerThread
    protected abstract void saveCallResult(@NonNull RequestObject item);

    @MainThread
    protected abstract boolean shouldFetch(@Nullable CacheObject data);

    @NonNull
    @MainThread
    protected abstract LiveData<CacheObject> loadFromDb();

    @NonNull
    @MainThread
    protected abstract LiveData<GozerResponse<RequestObject>> createCall();

    public final LiveData<Resource<CacheObject>> getAsLiveData() {
        return mResults;
    }
}
