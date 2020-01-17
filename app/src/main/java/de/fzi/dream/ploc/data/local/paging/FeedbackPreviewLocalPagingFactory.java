package de.fzi.dream.ploc.data.local.paging;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.DataSource;

import de.fzi.dream.ploc.data.local.database.CacheDao;
import de.fzi.dream.ploc.data.repository.RecordRepository;
import de.fzi.dream.ploc.data.structure.entity.FeedbackPreview;

/**
 * Expert preview data source factory which provides a way to observe the last created local
 * data source. This allows us to channel its database request status and paging requests
 * back to the UI.
 * See the Listing creation in the {@link RecordRepository} class.
 *
 * @author Felix Melcher
 */
public class FeedbackPreviewLocalPagingFactory extends DataSource.Factory<String, FeedbackPreview> {

    /** Public class identifier tag for logging */
    public static final String TAG = FeedbackPreviewLocalPagingFactory.class.getSimpleName();

    private final CacheDao mDao;
    private FeedbackPreviewLocalPagingSource mPagingSource;
    private MutableLiveData<FeedbackPreviewLocalPagingSource> mSourceStatus;

    public FeedbackPreviewLocalPagingFactory(CacheDao dao) {
        mDao = dao;
        mPagingSource = new FeedbackPreviewLocalPagingSource(mDao);
        mSourceStatus = new MutableLiveData<>();
    }

    @NonNull
    @Override
    public DataSource<String, FeedbackPreview> create() {
        if (mPagingSource.isInvalid()) {
            mPagingSource = new FeedbackPreviewLocalPagingSource(mDao);
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
    public MutableLiveData<FeedbackPreviewLocalPagingSource> getDatabaseState() {
        return mSourceStatus;
    }
}