package de.fzi.dream.ploc.data.repository;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import de.fzi.dream.ploc.data.local.database.CacheDatabase;
import de.fzi.dream.ploc.data.local.paging.FeedbackPreviewLocalPagingFactory;
import de.fzi.dream.ploc.data.local.paging.FeedbackPreviewLocalPagingSource;
import de.fzi.dream.ploc.data.remote.connection.GozerClient;
import de.fzi.dream.ploc.data.remote.paging.ExpertPreviewRemotePagingFactory;
import de.fzi.dream.ploc.data.remote.paging.FeedbackPreviewRemotePagingFactory;
import de.fzi.dream.ploc.data.remote.paging.FeedbackPreviewRemotePagingSource;
import de.fzi.dream.ploc.data.remote.request.RecordRequest;
import de.fzi.dream.ploc.data.remote.response.FeedbackDetailResponse;
import de.fzi.dream.ploc.data.structure.Feedback;
import de.fzi.dream.ploc.data.structure.entity.FeedbackPreview;
import de.fzi.dream.ploc.ui.callback.FeedbackDetailCallback;
import de.fzi.dream.ploc.ui.callback.IOCallback;
import de.fzi.dream.ploc.utility.AppExecutors;
import de.fzi.dream.ploc.utility.Constants;
import de.fzi.dream.ploc.utility.network.NetworkState;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.schedulers.Schedulers;

import static de.fzi.dream.ploc.utility.Constants.FEED_PAGING_PREFETCH_RANGE_SIZE;

/**
 * The RecordRepository implementation is the access point for all record related data manipulation,
 * it returns data directly from the network or if it is cached from the local database.
 * It also manages the remote paging through the {@link ExpertPreviewRemotePagingFactory}.
 *
 * @author Felix Melcher
 */
public class FeedbackRepository {
    /** Public class identifier tag for logging */
    public static final String TAG = FeedbackRepository.class.getSimpleName();

    private static FeedbackRepository INSTANCE = null;
    private final CacheDatabase mCacheDatabase;

    // Data source states
    private LiveData<NetworkState> mRemotePagingState;
    private LiveData<NetworkState> mLocalPagingState;

    // Paging
    private FeedbackPreviewLocalPagingFactory mLocalPagingFactory;
    private FeedbackPreviewRemotePagingFactory mRemoteDataSourceFactory;
    private final PagedList.Config mPagedListConfig = (new PagedList.Config.Builder()).setEnablePlaceholders(true)
            .setInitialLoadSizeHint(Constants.FEED_PAGING_INITIAL_RANGE_SIZE).setPageSize(Constants.FEED_PAGING_PAGE_RANGE_SIZE).setPrefetchDistance(FEED_PAGING_PREFETCH_RANGE_SIZE).build();
    private Executor mExecutor = Executors.newFixedThreadPool(Constants.NUMBERS_OF_THREADS);

    // Merging local and remote data stream
    private final MediatorLiveData<PagedList<FeedbackPreview>> liveDataMerger;
    private LiveData<PagedList<FeedbackPreview>> mLocalPaged;
    private LiveData<PagedList<FeedbackPreview>> mRemotePaged;

    // Paging Boundaries
    private PagedList.BoundaryCallback<FeedbackPreview> mLocalBoundaryCallback = new PagedList.BoundaryCallback<FeedbackPreview>() {
        @Override
        public void onZeroItemsLoaded() {
            super.onZeroItemsLoaded();
            liveDataMerger.removeSource(mRemotePaged);
            liveDataMerger.addSource(mRemotePaged, liveDataMerger::setValue);
        }
    };

    private PagedList.BoundaryCallback<FeedbackPreview> mRemoteBoundaryCallback = new PagedList.BoundaryCallback<FeedbackPreview>() {
        @Override
        public void onZeroItemsLoaded() {
            super.onZeroItemsLoaded();
            liveDataMerger.removeSource(mRemotePaged);
        }
    };

    // Current search term
    private final MutableLiveData<String> mPagingSearchTerm = new MutableLiveData<>();

    /**
     * Constructor
     *
     * @param cacheDatabase Instance of the internal android room database
     */
    private FeedbackRepository(final CacheDatabase cacheDatabase) {
        mCacheDatabase = cacheDatabase;
        liveDataMerger = new MediatorLiveData<>();
        initPaging();
    }

    /**
     * Create a singleton object of the repository
     *
     * @param cacheDatabase Instance of the internal android room database
     */
    public static FeedbackRepository getInstance(final CacheDatabase cacheDatabase) {
        if (INSTANCE == null) {
            synchronized (FeedbackRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new FeedbackRepository(cacheDatabase);
                }
            }
        }
        return INSTANCE;
    }

    /**
     * The initPaging() method is called at the creation of the repository and every time there
     * is a reset of the paging
     * <p>
     * The method constructs a local and a remote factory, these factories are responsible for the
     * creation of data sources which are capable of filling list in parts. These {@link PagedList}s
     * have a {@link PagedList.BoundaryCallback} to indicate when the sources need to be switched. Also the
     * connection states are initialized and the {@link MediatorLiveData} object to merge the data
     * streams is created.
     */
    private void initPaging() {
        // Factories to create local and remote paging sources
        mLocalPagingFactory = new FeedbackPreviewLocalPagingFactory(mCacheDatabase.cacheDao());
        mRemoteDataSourceFactory = new FeedbackPreviewRemotePagingFactory(mPagingSearchTerm);

        // LivePager to submit data to the recycler view
        mLocalPaged = (new LivePagedListBuilder<>(mLocalPagingFactory, mPagedListConfig)).setBoundaryCallback(mLocalBoundaryCallback).setFetchExecutor(mExecutor).build();
        mRemotePaged = (new LivePagedListBuilder<>(mRemoteDataSourceFactory, mPagedListConfig)).setBoundaryCallback(mRemoteBoundaryCallback).setFetchExecutor(mExecutor).build();

        // Network and database state
        mRemotePagingState = Transformations.switchMap(mRemoteDataSourceFactory.getNetworkStatus(), FeedbackPreviewRemotePagingSource::getNetworkState);
        mLocalPagingState = Transformations.switchMap(mLocalPagingFactory.getDatabaseState(), FeedbackPreviewLocalPagingSource::getDatabaseState);

        // Add LocalDataSource as primary source
        liveDataMerger.addSource(mLocalPaged, liveDataMerger::setValue);

        // Observe Remote and save to LocalDataSource
        mRemoteDataSourceFactory.getFeedbacks().observeOn(Schedulers.io()).
                subscribe(record -> {
                    mCacheDatabase.cacheDao().insertFeedbackPreview(record);
                });
    }

    /**
     * The resetPaging() method is called when the list needs to be reloaded, the remote
     * {@link PagedList} is removed from the {@link MediatorLiveData}, the table of experts
     * in the internal cache database is cleared and the local and remote data sources are made
     * invalid. After that, the paging is started again with the initPaging() method
     *
     */
    public void resetPaging(){
        liveDataMerger.removeSource(mRemotePaged);
        AppExecutors.getInstance().diskIO().execute(() -> {
            mCacheDatabase.cacheDao().deleteFeedbackPreviews();
        });
        mRemoteDataSourceFactory.invalidateDataSource();
        mLocalPagingFactory.invalidateDataSource();
        initPaging();
    }

    /**
     * Get the current network state as a live data object to observe updates.
     *
     * @return LiveData<NetworkState>
     */
    public LiveData<NetworkState> getNetworkState() {
        return mRemotePagingState;
    }

    /**
     * Update the current search term, the term is pushed to the factory through a {@link LiveData}
     * object
     *
     * @param term The new term to be updated.
     */
    public void setPagingSearchTerm(String term) {
        mPagingSearchTerm.setValue(term);
    }

    /**
     * The local data source can be made invalid through the local paging factory. For example, this
     * is needed after an record is bookmarked.
     */
    public void invalidateLocalDataSource() {
        mLocalPagingFactory.invalidateDataSource();
    }

    /**
     * Access the {@link PagedList} created of the record stream from the local and remote data source.
     *
     * @return LiveData<PagedList<ExpertPreview>>
     */
    public LiveData<PagedList<FeedbackPreview>> getFeedbacks() {
        return liveDataMerger;
    }

    /**
     * Request the detail view of an given record by its id.
     *
     * @param id The identifier of the record.
     * @param callback Interface to send the detail information back to the caller .
     */
    public void readFeedbackDetail(int id, FeedbackDetailCallback callback) {
        Call<FeedbackDetailResponse> call = GozerClient.getAuthenticatedClient().readFeedbackDetail(new RecordRequest().setID(id));
        call.enqueue(new Callback<FeedbackDetailResponse>() {
            @Override
            public void onResponse(@NonNull Call<FeedbackDetailResponse> call,@NonNull Response<FeedbackDetailResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onCallback(response.body().getFeedbacks());
                } else {
                    Log.d(Constants.LOG_TAG,  "A network error occurred: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<FeedbackDetailResponse> call,@NonNull Throwable t) {
                Log.d(Constants.LOG_TAG, "A network error occurred: " + t.getMessage());
            }
        });
    }


    /**
     * Insert an record into the local cache database.
     *
     * @param feedbackPreview To be inserted in the local database
     * @param callback Interface to send the detail information back to the caller
     */
    public void insertFeedback(FeedbackPreview feedbackPreview, IOCallback callback) {
        AppExecutors.getInstance().diskIO().execute(() -> {
            mCacheDatabase.cacheDao().insertFeedbackPreview(feedbackPreview);
            callback.onCallback(true);
        });
    }

    /**
     * Insert an record into the local cache database.
     *
     * @param feedback To be inserted in the local database
     * @param callback Interface to send the detail information back to the caller
     */
    public void createFeedback(Feedback feedback, IOCallback callback) {
        Call<ResponseBody> call = GozerClient.getAuthenticatedClient().createFeedback(feedback);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call,@NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    callback.onCallback(true);
                } else {
                    Log.d(Constants.LOG_TAG,  "A network error occurred: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call,@NonNull Throwable t) {
                Log.d(Constants.LOG_TAG, "A network error occurred: " + t.getMessage());
            }
        });
    }
}
