package de.fzi.dream.ploc.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.paging.PagedList;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import de.fzi.dream.ploc.Ploc;
import de.fzi.dream.ploc.R;
import de.fzi.dream.ploc.data.remote.paging.RecordPreviewRemotePagingFactory;
import de.fzi.dream.ploc.data.remote.paging.RecordPreviewRemotePagingSource;
import de.fzi.dream.ploc.data.structure.entity.ExpertPreview;
import de.fzi.dream.ploc.utility.network.NetworkBoundResource;
import de.fzi.dream.ploc.utility.network.NetworkState;
import de.fzi.dream.ploc.data.repository.ProfileRepository;
import de.fzi.dream.ploc.data.repository.RecordRepository;
import de.fzi.dream.ploc.data.structure.entity.Interest;
import de.fzi.dream.ploc.data.structure.entity.RecordPreview;
import de.fzi.dream.ploc.utility.Notification;
import de.fzi.dream.ploc.utility.network.Resource;

/**
 * Application context aware {@link ViewModel}.
 * The RecordPreviewViewModel is responsible for preparing and managing the record preview list
 * for an Activity or a Fragment. It also handles the communication of the Activity / Fragment with
 * the data sources.
 *
 * @author Felix Melcher
 */
public class RecordPreviewViewModel extends AndroidViewModel {

    /**
     * Public class identifier tag
     */
    public static final String TAG = RecordPreviewViewModel.class.getSimpleName();

    // Repositories
    private ProfileRepository mProfileRepository;
    private RecordRepository mRecordRepository;

    // LiveData
    private final MutableLiveData<Notification> mNotificationMessage = new MutableLiveData<>();

    // Parameters
    private Set<Integer> mCurrentInterests = new HashSet<>();
    private RecordPreview mLastSwipedPreview;

    /**
     * Constructor
     *
     * @param application The application context the ViewModel should be aware of.
     */
    public RecordPreviewViewModel(Application application) {
        super(application);
        mRecordRepository = ((Ploc) application).getRecordRepository();
        mProfileRepository = ((Ploc) application).getProfileRepository();
    }

    /**
     * Get the record preview list as a as LiveData PagedList object. This Paged list is build from
     * the remote source, saved into the local data source and then displayed from the local source.
     *
     * @return LiveData<PagedList<RecordPreview>>
     */
    public LiveData<PagedList<RecordPreview>> getPreviews() {
        return mRecordRepository.getRecords();
    }

    /**
     * Get a LiveData {@link NetworkBoundResource} object that represents the current state of
     * the users interest profile.
     *
     * @return LiveData<Resource<List<Interest>>>
     */
    public LiveData<Resource<List<Interest>>> getInterests(){
        return mProfileRepository.readInterests();
    }

    public LiveData<NetworkState> getNetworkState() {
        return mRecordRepository.getNetworkState();
    }


    public MutableLiveData<Notification> getNotificationMessage() {
        return mNotificationMessage;
    }


    /**
     * Set an record preview as bookmark in the remote data source and if this succeeded
     * also set the bookmark in the local data source. After finishing this process, reload the
     * local data source to remove this preview from the list and show a snackbar message.
     *
     * @param preview the {@link RecordPreview} object of the expert that should be bookmarked.
     */
    public void setBookmark(RecordPreview preview) {
        mLastSwipedPreview = preview;
        mProfileRepository.createRecordBookmark(preview, ioFinishedSuccessfully -> {
            if (ioFinishedSuccessfully) {
                mRecordRepository.invalidateLocalDataSource();
                mNotificationMessage.postValue(new Notification(getApplication().getResources().getString(R.string.message_bookmarked), false, true, false));
            }
        });
    }

    /**
     * Set the current search term and send it to the repository to update the
     * {@link RecordPreviewRemotePagingFactory} and the preview input stream. Reset the list to
     * show the new input stream of previews.
     *
     * @param term The search term to be searched within the users preview feed.
     */
    public void setPagingSearchTerm(String term) {
        mRecordRepository.setPagingSearchTerm(term);
        resetPaging();
    }

    /**
     * Mark an record preview as disliked in the remote data source, to show less of these
     * type of records. After finishing this process, reload/invalidate the
     * local data source to remove this preview from the list and show a snackbar message.
     *
     * @param preview the {@link RecordPreview} object of the expert that should marked as disliked.
     */
    public void setDisinterest(RecordPreview preview) {
        mLastSwipedPreview = preview;
        mProfileRepository.createRecordDisinterest(preview.getId(), ioFinishedSuccessfully -> {
            if (ioFinishedSuccessfully) {
                mRecordRepository.invalidateLocalDataSource();
                mNotificationMessage.postValue(new Notification(getApplication().getResources().getString(R.string.message_dislike),
                        false, true, false));
            }
        });
    }

    /**
     * Reset the {@link RecordPreviewRemotePagingSource} and rebuild the preview input stream.
     */
    public void resetPaging() {
        mRecordRepository.resetPaging();
    }

    /**
     * Undo the last bookmark operation and reload/invalidate the preview list. Show a success
     * message through the snackbar.
     */
    public void undoLastBookmark() {
        mProfileRepository.deleteRecordBookmark(mLastSwipedPreview.getId(), success -> mRecordRepository.insertRecord(mLastSwipedPreview, success1 -> {
            mRecordRepository.invalidateLocalDataSource();
        }));
    }

    /**
     * Undo the last dislike operation and reload/invalidate the preview list. Show a success
     * message through the snackbar.
     */
    public void undoLastDisinterest() {
        mRecordRepository.insertRecord(mLastSwipedPreview, success -> {
            mRecordRepository.invalidateLocalDataSource();
        });
    }

    /**
     * Compare the given (possible) new interest list with the users current interest set to
     * display a snackbar notification to inform about a reload of the preview list or not.
     *
     * @param interests The (possible) updated interest set of the user.
     */
    public void compareInterestsSet(List<Interest> interests) {
        Set<Integer> newInterests = interests.stream().map(Interest::getId).collect(Collectors.toSet());
        if(mCurrentInterests.size() > 0){
            if (!newInterests.equals(mCurrentInterests)) {
                mNotificationMessage.postValue(new Notification(getApplication().getResources().getString(R.string.snackbar_message_new_interests),
                        true, false, false));
            }
        }
        mCurrentInterests = newInterests;
    }


}
