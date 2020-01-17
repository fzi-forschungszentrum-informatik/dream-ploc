package de.fzi.dream.ploc.data.remote.paging;

import androidx.lifecycle.MutableLiveData;
import androidx.paging.DataSource;

import de.fzi.dream.ploc.data.repository.ExpertRepository;
import de.fzi.dream.ploc.data.structure.entity.FeedbackPreview;
import rx.subjects.ReplaySubject;

/**
 * The RecordPreviewDataSourceFactory provides a way to observe the remote
 * data source. This allows us to query the remote backend database status and paging requests
 * back to the UI.
 * See the Listing creation in the {@link ExpertRepository} class.
 *
 * @author Felix Melcher
 */
public class FeedbackPreviewRemotePagingFactory extends DataSource.Factory<PageKey, FeedbackPreview> {

    /** Public class identifier tag for logging */
    public static final String TAG = FeedbackPreviewRemotePagingFactory.class.getSimpleName();

    private MutableLiveData<FeedbackPreviewRemotePagingSource> mNetworkStatus;
    private FeedbackPreviewRemotePagingSource mPagingSource;
    private final MutableLiveData<String> mSearchTerm;

    /**
     * Constructor
     *
     * @param searchTerm the search term used to query the database for the current session.
     */
    public FeedbackPreviewRemotePagingFactory(MutableLiveData<String> searchTerm) {
        mSearchTerm = searchTerm;
        mNetworkStatus = new MutableLiveData<>();
        mPagingSource = new FeedbackPreviewRemotePagingSource(mSearchTerm);
    }

    /**
     * Create a DataSource.
     * <p>
     * If the current data source is not available or invalid create a new one with (or without) a
     * given search term, else return the still valid data source.
     * <p>
     * Post an update to the network status.
     */
    @Override
    public DataSource<PageKey, FeedbackPreview> create() {
        if (mPagingSource == null || mPagingSource.isInvalid()) {
            mPagingSource = new FeedbackPreviewRemotePagingSource(mSearchTerm);
        }
        mNetworkStatus.postValue(mPagingSource);
        return mPagingSource;
    }

    /**
     * Signal the current data source to stop loading, and notify its callback.
     * <p>
     * If invalidate has already been called, this method does nothing.
     * <p>
     * An invalidated data source never becomes valid again
     */
    public void invalidateDataSource() {
        mPagingSource.invalidate();
    }

    /**
     * Get the current network status as a {@link MutableLiveData} object from the data source
     * that is valid at this moment
     *
     * @return MutableLiveData object of the current data source
     */
    public MutableLiveData<FeedbackPreviewRemotePagingSource> getNetworkStatus() { return mNetworkStatus; }

    /**
     * Get a JavaRX {@link ReplaySubject} object from the data source to obtain a observable binding
     * object from the current valid data source
     *
     * @return ReplaySubject observable list of records
     */
    public ReplaySubject<FeedbackPreview> getFeedbacks() { return mPagingSource.getFeedbacks(); }

}