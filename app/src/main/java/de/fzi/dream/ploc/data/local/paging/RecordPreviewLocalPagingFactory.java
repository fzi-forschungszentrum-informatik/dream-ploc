package de.fzi.dream.ploc.data.local.paging;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.DataSource;

import de.fzi.dream.ploc.data.local.database.CacheDao;
import de.fzi.dream.ploc.data.repository.RecordRepository;
import de.fzi.dream.ploc.data.structure.entity.RecordPreview;

/**
 * Expert preview data source factory which provides a way to observe the last created local
 * data source. This allows us to channel its database request status and paging requests
 * back to the UI.
 * See the Listing creation in the {@link RecordRepository} class.
 *
 * @author Felix Melcher
 */
public class RecordPreviewLocalPagingFactory extends DataSource.Factory<String, RecordPreview> {

    /** Public class identifier tag for logging */
    public static final String TAG = ExpertPreviewLocalPagingFactory.class.getSimpleName();

    private final CacheDao mDao;
    private RecordPreviewLocalPagingSource mPagingSource;
    private MutableLiveData<RecordPreviewLocalPagingSource> mSourceStatus;

    public RecordPreviewLocalPagingFactory(CacheDao dao) {
        mDao = dao;
        mPagingSource = new RecordPreviewLocalPagingSource(mDao);
        mSourceStatus = new MutableLiveData<>();
    }

    @NonNull
    @Override
    public DataSource<String, RecordPreview> create() {
        if (mPagingSource.isInvalid()) {
            mPagingSource = new RecordPreviewLocalPagingSource(mDao);
        }
        return mPagingSource;
    }

    /**
     * Invalidates the current data source
     */
    public void invalidateDataSource() {
        mPagingSource.invalidate();
    }


    /**
     * Returns the status of the current data source
     *
     * @return {@link MutableLiveData} Object containing the current database state.
     */
    public MutableLiveData<RecordPreviewLocalPagingSource> getDatabaseState() {
        return mSourceStatus;
    }
}