package de.fzi.dream.ploc.data.local.paging;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.PageKeyedDataSource;

import java.util.Collections;
import java.util.List;

import de.fzi.dream.ploc.data.local.database.CacheDao;
import de.fzi.dream.ploc.data.repository.RecordRepository;
import de.fzi.dream.ploc.data.structure.entity.FeedbackPreview;
import de.fzi.dream.ploc.utility.network.NetworkState;

/**
 * Record preview source delivers the data to the paging adapter for displaying in a
 * continuous list.
 * See the Listing creation in the {@link RecordRepository} class.
 *
 * @author Felix Melcher
 */
public class FeedbackPreviewLocalPagingSource extends PageKeyedDataSource<String, FeedbackPreview> {

    /** Public class identifier tag for logging */
    public static final String TAG = FeedbackPreviewLocalPagingSource.class.getSimpleName();

    private final CacheDao mCacheDao;
    private final MutableLiveData<NetworkState> mDatabaseState;

    FeedbackPreviewLocalPagingSource(CacheDao dao) {
        mCacheDao = dao;
        mDatabaseState = new MutableLiveData<>();
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams<String> params, @NonNull final LoadInitialCallback<String, FeedbackPreview> callback) {
        List<FeedbackPreview> records = mCacheDao.selectFeedbackPreviews();
        mDatabaseState.postValue(NetworkState.LOADING);
        if (records.size() != 0) {
            callback.onResult(records, "0", "1");
        } else {
            callback.onResult(Collections.emptyList(), 0, 0, null, null);
        }
        mDatabaseState.postValue(NetworkState.LOADED);
    }

    @Override
    public void loadAfter(@NonNull LoadParams<String> params, final @NonNull LoadCallback<String, FeedbackPreview> callback) {
    }

    @Override
    public void loadBefore(@NonNull LoadParams<String> params, @NonNull LoadCallback<String, FeedbackPreview> callback) {
    }

    /**
     * Returns the status of the current data source
     *
     * @return {@link MutableLiveData} Object containing the current database state.
     */
    public MutableLiveData<NetworkState> getDatabaseState() {
        return mDatabaseState;
    }
}