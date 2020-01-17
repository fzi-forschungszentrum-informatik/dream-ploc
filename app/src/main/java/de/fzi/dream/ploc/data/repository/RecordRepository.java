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
import de.fzi.dream.ploc.data.local.paging.RecordPreviewLocalPagingFactory;
import de.fzi.dream.ploc.data.local.paging.RecordPreviewLocalPagingSource;
import de.fzi.dream.ploc.data.remote.paging.ExpertPreviewRemotePagingFactory;
import de.fzi.dream.ploc.data.remote.paging.RecordPreviewRemotePagingSource;
import de.fzi.dream.ploc.ui.callback.IOCallback;
import de.fzi.dream.ploc.ui.callback.RecordDetailCallback;
import de.fzi.dream.ploc.utility.network.NetworkState;
import de.fzi.dream.ploc.data.remote.connection.GozerClient;
import de.fzi.dream.ploc.data.remote.paging.RecordPreviewRemotePagingFactory;
import de.fzi.dream.ploc.data.remote.request.RecordRequest;
import de.fzi.dream.ploc.data.structure.RecordDetail;
import de.fzi.dream.ploc.data.structure.entity.RecordPreview;
import de.fzi.dream.ploc.utility.AppExecutors;
import de.fzi.dream.ploc.utility.Constants;
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
public class RecordRepository {
    /** Public class identifier tag for logging */
    public static final String TAG = RecordRepository.class.getSimpleName();

    private static RecordRepository INSTANCE = null;
    private final CacheDatabase mCacheDatabase;

    // Data source states
    private LiveData<NetworkState> mRemotePagingState;
    private LiveData<NetworkState> mLocalPagingState;

    // Paging
    private RecordPreviewLocalPagingFactory mLocalPagingFactory;
    private RecordPreviewRemotePagingFactory mRemoteDataSourceFactory;
    private final PagedList.Config mPagedListConfig = (new PagedList.Config.Builder()).setEnablePlaceholders(true)
            .setInitialLoadSizeHint(Constants.FEED_PAGING_INITIAL_RANGE_SIZE).setPageSize(Constants.FEED_PAGING_PAGE_RANGE_SIZE).setPrefetchDistance(FEED_PAGING_PREFETCH_RANGE_SIZE).build();
    private Executor mExecutor = Executors.newFixedThreadPool(Constants.NUMBERS_OF_THREADS);

    // Merging local and remote data stream
    private final MediatorLiveData<PagedList<RecordPreview>> liveDataMerger;
    private LiveData<PagedList<RecordPreview>> mLocalPaged;
    private LiveData<PagedList<RecordPreview>> mRemotePaged;

    // Paging Boundaries
    private PagedList.BoundaryCallback<RecordPreview> mLocalBoundaryCallback = new PagedList.BoundaryCallback<RecordPreview>() {
        @Override
        public void onZeroItemsLoaded() {
            super.onZeroItemsLoaded();
            liveDataMerger.removeSource(mRemotePaged);
            liveDataMerger.addSource(mRemotePaged, liveDataMerger::setValue);
        }

//        @Override
//        public void onItemAtFrontLoaded(@NonNull RecordPreview itemAtFront) {
//            Log.d(LOG_TAG + TAG, "ITEM AT FRONT");
//            liveDataMerger.removeSource(mRemotePaged);
//        }
//
//        @Override
//        public void onItemAtEndLoaded(@NonNull RecordPreview itemAtFront) {
//            Log.d(LOG_TAG + TAG, "ITEM AT END");
//            liveDataMerger.addSource(mRemotePaged, value -> {
//                liveDataMerger.setValue(value);
//            });
//        }
    };

    private PagedList.BoundaryCallback<RecordPreview> mRemoteBoundaryCallback = new PagedList.BoundaryCallback<RecordPreview>() {
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
    private RecordRepository(final CacheDatabase cacheDatabase) {
        mCacheDatabase = cacheDatabase;
        liveDataMerger = new MediatorLiveData<>();
        initPaging();
    }

    /**
     * Create a singleton object of the repository
     *
     * @param cacheDatabase Instance of the internal android room database
     */
    public static RecordRepository getInstance(final CacheDatabase cacheDatabase) {
        if (INSTANCE == null) {
            synchronized (RecordRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new RecordRepository(cacheDatabase);
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
        mLocalPagingFactory = new RecordPreviewLocalPagingFactory(mCacheDatabase.cacheDao());
        mRemoteDataSourceFactory = new RecordPreviewRemotePagingFactory(mPagingSearchTerm);

        // LivePager to submit data to the recycler view
        mLocalPaged = (new LivePagedListBuilder<>(mLocalPagingFactory, mPagedListConfig)).setBoundaryCallback(mLocalBoundaryCallback).setFetchExecutor(mExecutor).build();
        mRemotePaged = (new LivePagedListBuilder<>(mRemoteDataSourceFactory, mPagedListConfig)).setBoundaryCallback(mRemoteBoundaryCallback).setFetchExecutor(mExecutor).build();

        // Network and database state
        mRemotePagingState = Transformations.switchMap(mRemoteDataSourceFactory.getNetworkStatus(), RecordPreviewRemotePagingSource::getNetworkState);
        mLocalPagingState = Transformations.switchMap(mLocalPagingFactory.getDatabaseState(), RecordPreviewLocalPagingSource::getDatabaseState);

        // Add LocalDataSource as primary source
        liveDataMerger.addSource(mLocalPaged, liveDataMerger::setValue);

        // Observe Remote and save to LocalDataSource
        mRemoteDataSourceFactory.getRecords().observeOn(Schedulers.io()).
                subscribe(record -> {
                    mCacheDatabase.cacheDao().insertRecordPreview(record);
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
            mCacheDatabase.cacheDao().deleteRecordPreviews();
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
    public LiveData<PagedList<RecordPreview>> getRecords() {
        return liveDataMerger;
    }

    /**
     * Request the detail view of an given record by its id.
     *
     * @param id The identifier of the record.
     * @param callback Interface to send the detail information back to the caller .
     */
    public void readRecordDetail(int id, RecordDetailCallback callback) {
        Call<RecordDetail> call = GozerClient.getAuthenticatedClient().readRecordDetail(new RecordRequest().setID(id));
        call.enqueue(new Callback<RecordDetail>() {
            @Override
            public void onResponse(@NonNull Call<RecordDetail> call,@NonNull Response<RecordDetail> response) {
                if (response.isSuccessful()) {
                    callback.onCallback(response.body());
                } else {
                    Log.d(Constants.LOG_TAG,  "A network error occurred: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<RecordDetail> call,@NonNull Throwable t) {
                Log.d(Constants.LOG_TAG, "A network error occurred: " + t.getMessage());
            }
        });
    }


    /**
     * Insert an record into the local cache database.
     *
     * @param record To be inserted in the local database
     * @param callback Interface to send the detail information back to the caller
     */
    public void insertRecord(RecordPreview record, IOCallback callback) {
        AppExecutors.getInstance().diskIO().execute(() ->
        {
            mCacheDatabase.cacheDao().insertRecordPreview(record);
            callback.onCallback(true);
        });
    }
}
