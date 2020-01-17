package de.fzi.dream.ploc.data.local.paging;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.DataSource;

import de.fzi.dream.ploc.data.local.database.CacheDao;
import de.fzi.dream.ploc.data.repository.ExpertRepository;
import de.fzi.dream.ploc.data.structure.entity.ExpertPreview;

/**
 * Expert preview data source factory which provides a way to observe the last created local
 * data source. This allows us to channel its database request status and paging requests
 * back to the UI.
 * See the Listing creation in the {@link ExpertRepository} class.
 *
 * @author Felix Melcher
 */
public class ExpertPreviewLocalPagingFactory extends DataSource.Factory<String, ExpertPreview> {

    /** Public class identifier tag for logging */
    public static final String TAG = ExpertPreviewLocalPagingFactory.class.getSimpleName();

    private final CacheDao mDao;
    private ExpertPreviewLocalPagingSource mPagingSource;
    private MutableLiveData<ExpertPreviewLocalPagingSource> mSourceStatus;

    /**
     * Constructor
     *
     * @param dao The database access object to retrieve the experts to page on.
     */
    public ExpertPreviewLocalPagingFactory(CacheDao dao) {
        mDao = dao;
        mPagingSource = new ExpertPreviewLocalPagingSource(dao);
        mSourceStatus = new MutableLiveData<>();
    }

    @NonNull
    @Override
    public DataSource<String, ExpertPreview> create() {
        if (mPagingSource.isInvalid()) {
            mPagingSource = new ExpertPreviewLocalPagingSource(mDao);
        }
       // mSourceStatus.postValue(mPagingSource);
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
     */
    public MutableLiveData<ExpertPreviewLocalPagingSource> getDatabaseStatus() {
        return mSourceStatus;
    }

}