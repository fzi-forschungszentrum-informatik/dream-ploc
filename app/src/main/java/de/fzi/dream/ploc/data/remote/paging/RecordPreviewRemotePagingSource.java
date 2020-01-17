package de.fzi.dream.ploc.data.remote.paging;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.PageKeyedDataSource;

import java.util.List;

import de.fzi.dream.ploc.utility.network.NetworkState;
import de.fzi.dream.ploc.data.remote.connection.GozerApi;
import de.fzi.dream.ploc.data.remote.connection.GozerClient;
import de.fzi.dream.ploc.data.remote.response.RecordPreviewResponse;
import de.fzi.dream.ploc.data.repository.RecordRepository;
import de.fzi.dream.ploc.data.structure.entity.RecordPreview;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.subjects.ReplaySubject;

import static de.fzi.dream.ploc.utility.Constants.LOG_TAG;

/**
 * Incremental data loader for page-keyed content, where requests return keys for next/previous
 * pages.
 *
 * See the Listing creation in the {@link RecordRepository} class.
 *
 * @author Felix Melcher
 */
public class RecordPreviewRemotePagingSource extends PageKeyedDataSource<PageKey, RecordPreview> {

    /** Public class identifier tag for logging */
    public static final String TAG = RecordPreviewRemotePagingSource.class.getSimpleName();

    private final MutableLiveData<NetworkState> mNetworkState = new MutableLiveData<>();
    private final ReplaySubject<RecordPreview> mObservableReplayExperts = ReplaySubject.create();
    private final GozerApi mApiClient;
    private String mSearchTerm;

    /**
     * Constructor
     *
     * @param searchTerm the search term used to query the database for the current session.
     */
    RecordPreviewRemotePagingSource(MutableLiveData<String> searchTerm) {
        mApiClient = GozerClient.getAuthenticatedClient();
        mSearchTerm = searchTerm.getValue();
    }


    @Override
    public void loadInitial(@NonNull LoadInitialParams<PageKey> params, @NonNull LoadInitialCallback<PageKey, RecordPreview> callback) {
        // Create initial key with an offset of 0, with or without a given search term as well as a
        // value for the size of the requested page
        PageKey initialPageKey = new PageKey(0, mSearchTerm, params.requestedLoadSize);
        // Create read or search call depending on the search term
        Call<RecordPreviewResponse> call = (mSearchTerm == null) ? mApiClient.readRecordPreview(initialPageKey) : mApiClient.searchRecordPreview(initialPageKey);
        // Update network state through observed live data object
        mNetworkState.postValue(NetworkState.LOADING);
        // Enqueue the request to the retrofit queue
        call.enqueue(new Callback<RecordPreviewResponse>() {
            @Override
            public void onResponse(@NonNull Call<RecordPreviewResponse> call, @NonNull Response<RecordPreviewResponse> response) {
                if(response.isSuccessful() && response.body() != null){
                    List<RecordPreview> records = response.body().getRecords();
                    if(records.isEmpty()){
                        // Case if response has no records
                        callback.onResult(records, null,null);
                        // Update network state through observed live data object
                        mNetworkState.postValue(NetworkState.ZERO);
                    } else {
                        // If response has records, create the next page key an call loadAfter(...)
                        callback.onResult(records, initialPageKey, initialPageKey.getNextPageKey(params.requestedLoadSize));
                        // Add all loaded records to the observable JavaRX replay object
                        records.forEach(mObservableReplayExperts::onNext);
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
            public void onFailure(@NonNull Call<RecordPreviewResponse> call,@NonNull Throwable t) {
                Log.d(LOG_TAG + TAG, t.getMessage()); mNetworkState.postValue(new NetworkState(NetworkState.Status.FAILED, t.getMessage()));
            }
        });
    }


    @Override
    public void loadAfter(@NonNull LoadParams<PageKey> params, @NonNull LoadCallback<PageKey, RecordPreview> callback) {
        // The loadAfter(...) method works similar to the loadInitial(...) method
        mNetworkState.postValue(NetworkState.LOADING);
        Call<RecordPreviewResponse> call;
        call = (mSearchTerm == null) ? mApiClient.readRecordPreview(params.key) : mApiClient.searchRecordPreview(params.key);
        call.enqueue(new Callback<RecordPreviewResponse>() {
            @Override
            public void onResponse(@NonNull Call<RecordPreviewResponse> call, @NonNull Response<RecordPreviewResponse> response) {
                if(response.isSuccessful() && response.body() != null){
                    List<RecordPreview> records = response.body().getRecords();
                    callback.onResult(records, params.key.getNextPageKey(params.requestedLoadSize));
                    records.forEach(mObservableReplayExperts::onNext);
                    mNetworkState.postValue(NetworkState.LOADED);
                } else {
                    mNetworkState.postValue(new NetworkState(NetworkState.Status.FAILED, response.message()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<RecordPreviewResponse> call, @NonNull Throwable t) {
                Log.d(LOG_TAG + TAG, t.getMessage()); mNetworkState.postValue(new NetworkState(NetworkState.Status.FAILED, t.getMessage()));
            }
        });
    }

    /**
     * Not needed in our implementation
     */
    @Override
    public void loadBefore(@NonNull LoadParams<PageKey> params, @NonNull LoadCallback<PageKey, RecordPreview> callback) {

    }

    /**
     * @return MutableLiveData<NetworkState> returns the current data source network state.
     */
    public MutableLiveData<NetworkState> getNetworkState() { return mNetworkState; }

    /**
     * @return ReplaySubject<RecordPreview> returns an observable list of records.
     */
    ReplaySubject<RecordPreview> getRecords() { return mObservableReplayExperts; }
}
