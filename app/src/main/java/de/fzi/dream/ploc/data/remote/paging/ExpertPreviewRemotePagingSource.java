package de.fzi.dream.ploc.data.remote.paging;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.PageKeyedDataSource;

import java.util.List;

import de.fzi.dream.ploc.utility.network.NetworkState;
import de.fzi.dream.ploc.data.remote.connection.GozerApi;
import de.fzi.dream.ploc.data.remote.connection.GozerClient;
import de.fzi.dream.ploc.data.remote.response.ExpertPreviewResponse;
import de.fzi.dream.ploc.data.structure.entity.ExpertPreview;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.subjects.ReplaySubject;

import static de.fzi.dream.ploc.utility.Constants.LOG_TAG;

public class ExpertPreviewRemotePagingSource extends PageKeyedDataSource<PageKey, ExpertPreview> {

    /** Public class identifier tag for logging */
    public static final String TAG = ExpertPreviewRemotePagingSource.class.getSimpleName();

    private final GozerApi mApiClient;
    private final MutableLiveData<NetworkState> mNetworkState = new MutableLiveData<>();
    private final ReplaySubject<ExpertPreview> mObservableExperts = ReplaySubject.create();

    private String mSearchTerm;

    ExpertPreviewRemotePagingSource(MutableLiveData<String> searchTerm) {
        mSearchTerm = searchTerm.getValue();
        mApiClient = GozerClient.getAuthenticatedClient();
    }

    ReplaySubject<ExpertPreview> getExperts() {
        return mObservableExperts;
    }

    public MutableLiveData<NetworkState> getNetworkState() {
        return mNetworkState;
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams<PageKey> params, @NonNull LoadInitialCallback<PageKey, ExpertPreview> callback) {
        // Create initial key with an offset of 0, with or without a given search term as well as a
        // value for the size of the requested page
        PageKey initialPageKey = new PageKey(0, mSearchTerm, params.requestedLoadSize);
        // Create read or search call depending on the search term
        Call<ExpertPreviewResponse> call = (mSearchTerm == null) ? mApiClient.readExpertPreview(initialPageKey) : mApiClient.searchExpertPreview(initialPageKey);
        // Update network state through observed live data object
        mNetworkState.postValue(NetworkState.LOADING);
        // Enqueue the request to the retrofit queue
        call.enqueue(new Callback<ExpertPreviewResponse>() {
            @Override
            public void onResponse(@NonNull Call<ExpertPreviewResponse> call, @NonNull Response<ExpertPreviewResponse> response) {
                if(response.isSuccessful() && response.body() != null){
                    List<ExpertPreview> experts = response.body().getExperts();
                    if(experts.isEmpty()){
                        // case if response has no records
                        callback.onResult(experts, null, null);
                        // Update network state through observed live data object
                        mNetworkState.postValue(NetworkState.ZERO);
                    } else {
                        // If response has experts, create the next page key an call loadAfter(...)
                        callback.onResult(experts, initialPageKey, initialPageKey.getNextPageKey(params.requestedLoadSize));
                        // Add all loaded experts to the observable JavaRX replay object
                        experts.forEach(mObservableExperts::onNext);
                        // Update network state through observed live data object
                        mNetworkState.postValue(NetworkState.LOADED);
                    }
                } else {
                    // Case if the response is not successful
                    // Update network state through observed live data object
                    mNetworkState.postValue(new NetworkState(NetworkState.Status.FAILED, response.message()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ExpertPreviewResponse> call, @NonNull Throwable t) {
                Log.d(LOG_TAG + TAG, t.getMessage()); mNetworkState.postValue(new NetworkState(NetworkState.Status.FAILED, t.getMessage()));
            }
        });
    }

    @Override
    public void loadAfter(@NonNull LoadParams<PageKey> params, @NonNull LoadCallback<PageKey, ExpertPreview> callback) {
        // The loadAfter(...) method works similar to the loadInitial(...) method
        Call<ExpertPreviewResponse> call = (mSearchTerm == null) ? mApiClient.readExpertPreview(params.key) : mApiClient.searchExpertPreview(params.key);
        mNetworkState.postValue(NetworkState.LOADING);
        call.enqueue(new Callback<ExpertPreviewResponse>() {
            @Override
            public void onResponse(@NonNull Call<ExpertPreviewResponse> call, @NonNull Response<ExpertPreviewResponse> response) {
                if(response.isSuccessful() && response.body() != null){
                    List<ExpertPreview> records = response.body().getExperts();
                    callback.onResult(records, params.key.getNextPageKey(params.requestedLoadSize));
                    mNetworkState.postValue(NetworkState.LOADED);
                    records.forEach(mObservableExperts::onNext);
                } else {
                    mNetworkState.postValue(new NetworkState(NetworkState.Status.FAILED, response.message()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ExpertPreviewResponse> call, @NonNull Throwable t) {
                Log.d(LOG_TAG + TAG, t.getMessage());
            }
        });
    }

    @Override
    public void loadBefore(@NonNull LoadParams<PageKey> params, @NonNull LoadCallback<PageKey, ExpertPreview> callback) {

    }
}
