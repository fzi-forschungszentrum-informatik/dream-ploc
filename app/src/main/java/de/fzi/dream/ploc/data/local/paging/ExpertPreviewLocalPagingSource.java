package de.fzi.dream.ploc.data.local.paging;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.PageKeyedDataSource;

import java.util.Collections;
import java.util.List;

import de.fzi.dream.ploc.data.local.database.CacheDao;
import de.fzi.dream.ploc.utility.network.NetworkState;
import de.fzi.dream.ploc.data.repository.ExpertRepository;
import de.fzi.dream.ploc.data.structure.entity.ExpertPreview;
/**
 * Expert preview source delivers the data to the paging adapter for displaying in a
 * continuous list.
 * See the Listing creation in the {@link ExpertRepository} class.
 *
 * @author Felix Melcher
 */
public class ExpertPreviewLocalPagingSource extends PageKeyedDataSource<String, ExpertPreview> {

    /** Public class identifier tag for logging */
    public static final String TAG = ExpertPreviewLocalPagingSource.class.getSimpleName();

    private final CacheDao mDao;
    private final MutableLiveData<NetworkState> mDatabaseState;

    ExpertPreviewLocalPagingSource(CacheDao dao) {
        mDao = dao;
        mDatabaseState = new MutableLiveData<>();
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams<String> params, @NonNull final LoadInitialCallback<String, ExpertPreview> callback) {
        List<ExpertPreview> experts = mDao.selectExpertPreviews();
        mDatabaseState.postValue(NetworkState.LOADING);
        if (experts.size() != 0) {
            callback.onResult(experts, "0", "1");
        } else {
            callback.onResult(Collections.emptyList(), 0, 0, null, null);
        }
        mDatabaseState.postValue(NetworkState.LOADED);
    }

    @Override
    public void loadAfter(@NonNull LoadParams<String> params, final @NonNull LoadCallback<String, ExpertPreview> callback) {
    }

    @Override
    public void loadBefore(@NonNull LoadParams<String> params, @NonNull LoadCallback<String, ExpertPreview> callback) {
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