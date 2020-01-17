package de.fzi.dream.ploc.data.repository;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;
import androidx.paging.PagedList.BoundaryCallback;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import de.fzi.dream.ploc.data.local.database.CacheDatabase;
import de.fzi.dream.ploc.data.local.paging.ExpertPreviewLocalPagingFactory;
import de.fzi.dream.ploc.data.local.paging.ExpertPreviewLocalPagingSource;
import de.fzi.dream.ploc.ui.callback.ExpertDetailCallback;
import de.fzi.dream.ploc.ui.callback.IOCallback;
import de.fzi.dream.ploc.utility.network.NetworkState;
import de.fzi.dream.ploc.data.remote.connection.GozerClient;
import de.fzi.dream.ploc.data.remote.paging.ExpertPreviewRemotePagingFactory;
import de.fzi.dream.ploc.data.remote.paging.ExpertPreviewRemotePagingSource;
import de.fzi.dream.ploc.data.remote.request.ExpertRequest;
import de.fzi.dream.ploc.data.structure.ExpertDetail;
import de.fzi.dream.ploc.data.structure.entity.ExpertPreview;
import de.fzi.dream.ploc.utility.AppExecutors;
import de.fzi.dream.ploc.utility.Constants;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.schedulers.Schedulers;

/**
 * The ExpertRepository implementation is the access point for all profile related data, it returns
 * data directly from the network or if it is cached from the local database. It also manages the
 * remote paging through the {@link ExpertPreviewRemotePagingFactory}.
 *
 * @author Felix Melcher
 */
public class ExpertRepository {
    /** Public class identifier tag for logging */
    public static final String TAG = ExpertRepository.class.getSimpleName();

    private static ExpertRepository INSTANCE = null;
    private final CacheDatabase mCacheDatabase;

    // Data source states
    private LiveData<NetworkState> mRemotePagingState;
    private LiveData<NetworkState> mLocalPagingState;

    // Paging
    private ExpertPreviewLocalPagingFactory mLocalPagingFactory;
    private ExpertPreviewRemotePagingFactory mRemotePagingFactory;
    private final PagedList.Config mPagedListConfig = (new PagedList.Config.Builder()).setEnablePlaceholders(false)
            .setInitialLoadSizeHint(Constants.FEED_PAGING_INITIAL_RANGE_SIZE).setPageSize(Constants.FEED_PAGING_PAGE_RANGE_SIZE).build();
    private Executor mExecutor = Executors.newFixedThreadPool(Constants.NUMBERS_OF_THREADS);

    // Merging local and remote data stream
    private final MediatorLiveData<PagedList<ExpertPreview>> liveDataMerger;
    private LiveData<PagedList<ExpertPreview>> mLocalPaged;
    private LiveData<PagedList<ExpertPreview>> mRemotePaged;

    // Paging Boundary
    private BoundaryCallback<ExpertPreview> mLocalBoundaryCallback = new BoundaryCallback<ExpertPreview>() {
        @Override
        public void onZeroItemsLoaded() {
            super.onZeroItemsLoaded();
            liveDataMerger.removeSource(mRemotePaged);
            liveDataMerger.addSource(mRemotePaged, liveDataMerger::setValue);
        }
    };
    private BoundaryCallback<ExpertPreview> mRemoteBoundaryCallback = new BoundaryCallback<ExpertPreview>() {
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
    private ExpertRepository(final CacheDatabase cacheDatabase) {
        mCacheDatabase = cacheDatabase;
        liveDataMerger = new MediatorLiveData<>();
        initPaging();
    }

    /**
     * Create a singleton object of the repository
     *
     * @param cacheDatabase Instance of the internal android room database
     */
    public static ExpertRepository getInstance(final CacheDatabase cacheDatabase) {
        if (INSTANCE == null) {
            synchronized (ExpertRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ExpertRepository(cacheDatabase);
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
     * have a {@link BoundaryCallback} to indicate when the sources need to be switched. Also the
     * connection states are initialized and the {@link MediatorLiveData} object to merge the data
     * streams is created.
     */
    private void initPaging() {
        // Factories to create local and remote paging sources
        mLocalPagingFactory = new ExpertPreviewLocalPagingFactory(mCacheDatabase.cacheDao());
        mRemotePagingFactory = new ExpertPreviewRemotePagingFactory(mPagingSearchTerm);

        // LivePager to submit data to the recycler view
        mLocalPaged = (new LivePagedListBuilder<>(mLocalPagingFactory, mPagedListConfig)).setBoundaryCallback(mLocalBoundaryCallback).setFetchExecutor(AppExecutors.getInstance().diskIO()).build();
        mRemotePaged = (new LivePagedListBuilder<>(mRemotePagingFactory, mPagedListConfig)).setBoundaryCallback(mRemoteBoundaryCallback).setFetchExecutor(AppExecutors.getInstance().networkIO()).build();

        // Network and database state
        mRemotePagingState = Transformations.switchMap(mRemotePagingFactory.getNetworkStatus(), ExpertPreviewRemotePagingSource::getNetworkState);
        mLocalPagingState = Transformations.switchMap(mLocalPagingFactory.getDatabaseStatus(), ExpertPreviewLocalPagingSource::getDatabaseState);

        // Add LocalDataSource as primary source
        liveDataMerger.addSource(mLocalPaged, liveDataMerger::setValue);

        // Observe Remote and save to LocalDataSource
        mRemotePagingFactory.getExperts().observeOn(Schedulers.io()).
                subscribe(expert -> {
                    mCacheDatabase.cacheDao().insertExpertPreview(expert);
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
            mCacheDatabase.cacheDao().deleteExpertPreviews();
        });
        mRemotePagingFactory.invalidateDataSource();
        mLocalPagingFactory.invalidateDataSource();
        initPaging();
    }

    /**
     * The local data source can be made invalid through the local paging factory. For example, this
     * is needed after an expert is bookmarked.
     */
    public void invalidateLocalDataSource() {
        mLocalPagingFactory.invalidateDataSource();
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
     * Access the {@link PagedList} created of the expert stream from the local and remote data source.
     *
     * @return LiveData<PagedList<ExpertPreview>>
     */
    public LiveData<PagedList<ExpertPreview>> getExperts() {
        return liveDataMerger;
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
     * Request the detail view of an given expert by its id.
     *
     * @param id The identifier of the expert.
     * @param callback Interface to send the detail information back to the caller .
     */
    public void readExpertDetail(int id, ExpertDetailCallback callback) {
        GozerClient.getAuthenticatedClient().readExpertDetail(new ExpertRequest().setID(id)).enqueue(new Callback<ExpertDetail>() {
            @Override
            public void onResponse(@NonNull Call<ExpertDetail> call, @NonNull Response<ExpertDetail> response) {
                if (response.isSuccessful()) {
                    callback.onCallback(response.body());
                } else {
                    Log.d(Constants.LOG_TAG,  "A network error occurred: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ExpertDetail> call, @NonNull Throwable t) {
                Log.d(Constants.LOG_TAG, "A network error occurred: " + t.getMessage());
            }
        });
    }

    /**
     * Insert an expert into the local cache database.
     *
     * @param expert To be inserted in the local database
     * @param callback Interface to send the detail information back to the caller
     */
    public void insertExpert(ExpertPreview expert, IOCallback callback) {
        AppExecutors.getInstance().diskIO().execute(() -> {
            mCacheDatabase.cacheDao().insertExpertPreview(expert);
            mLocalPagingFactory.invalidateDataSource();
            callback.onCallback(true);
        });
    }

}
