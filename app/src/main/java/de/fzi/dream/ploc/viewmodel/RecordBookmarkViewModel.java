package de.fzi.dream.ploc.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import de.fzi.dream.ploc.Ploc;
import de.fzi.dream.ploc.R;
import de.fzi.dream.ploc.data.repository.ProfileRepository;
import de.fzi.dream.ploc.data.structure.entity.Collection;
import de.fzi.dream.ploc.data.structure.entity.ExpertBookmark;
import de.fzi.dream.ploc.data.structure.entity.RecordBookmark;
import de.fzi.dream.ploc.ui.callback.IOCallback;
import de.fzi.dream.ploc.utility.network.NetworkBoundResource;
import de.fzi.dream.ploc.utility.network.Resource;

/**
 * Application context aware {@link ViewModel}.
 * The RecordBookmarkViewModel is responsible for preparing and managing the record bookmark data
 * for an Activity or a Fragment. It also handles the communication of the Activity / Fragment with
 * the data sources.
 *
 * @author Felix Melcher
 */
public class RecordBookmarkViewModel extends AndroidViewModel {

    /**
     * Public class identifier tag
     */
    public static final String TAG = RecordBookmarkViewModel.class.getSimpleName();

    // Repository
    private ProfileRepository mProfileRepository;

    // LiveData
    private final MutableLiveData<String> mNotificationMessage = new MutableLiveData<>();

    // Parameters
    private RecordBookmark mLastDeletedBookmark;

    /**
     * Constructor
     *
     * @param application The application context the ViewModel should be aware of.
     */
    public RecordBookmarkViewModel(Application application) {
        super(application);
        mProfileRepository = ((Ploc) application).getProfileRepository();
    }

    /**
     * Get the record bookmarks as LiveData {@link NetworkBoundResource} to switch between
     * remote and local data source if needed.
     *
     * @return LiveData<Resource<List<RecordBookmark>>>
     */
    public LiveData<Resource<List<RecordBookmark>>> getBookmarks() {
        return mProfileRepository.readRecordBookmarks();
    }

    /**
     * Get the record bookmarks filtered by a collection id as LiveData {@link NetworkBoundResource}
     * to switch between remote and local data source if needed.
     *
     * @return LiveData<Resource<List<RecordBookmark>>>
     */
    public LiveData<List<RecordBookmark>> getBookmarksByCollectionID(int id) {
        return mProfileRepository.readRecordBookmarksByCollectionID(id);
    }

    /**
     * Get the collections of the current user as LiveData {@link NetworkBoundResource}
     * to switch between remote and local data source if needed.
     *
     * @return LiveData<Resource<List<Collection>>>
     */
    public LiveData<Resource<List<Collection>>> getCollections() {
        return mProfileRepository.readCollections();
    }

    /**
     * Assign a set of record bookmarks to a set of user defined collections. Save these references
     * to the remote data source and to the local data source.
     *
     * @param recordIDs A list of record identifiers that should be assigned.
     * @param collectionIDs A list of collections identifiers to which the records should be assigned.
     */
    public void assignCollectionToBookmarks(List<Integer> recordIDs, List<Integer> collectionIDs) {
        for (int id : recordIDs) {
            mProfileRepository.assignCollectionToBookmark(id, collectionIDs);
        }
    }

    /**
     * Undo the last delete operation and restore the bookmark.
     */
    public void undoDeleteBookmark() {
        mProfileRepository.createRecordBookmark(mLastDeletedBookmark.toPreview(), success -> {
            if (success)
                mNotificationMessage.postValue(getApplication().getResources().getString(R.string.message_snackbar_bookmark_restored));
        });
    }

    /**
     * Delete the record bookmark in the remote data source and if this succeeded also delete it
     * from the local data source.
     *
     * @param bookmark the {@link ExpertBookmark} object that should be deleted.
     */
    public void deleteBookmark(RecordBookmark bookmark) {
        mLastDeletedBookmark = bookmark;
        mProfileRepository.deleteRecordBookmark(bookmark.getId(), ioFinishedSuccessfully -> mNotificationMessage.postValue(getApplication().getResources().getString(R.string.message_deleted)));

    }

    /**
     * Create a new collection and store it to the remote and local data source.
     *
     * @param name the name of the collection that should be created.
     */
    public void setCollection(String name) {
        mProfileRepository.createCollection(name);
    }

    /**
     * Get a LiveData object to observe messages send from this ViewModel.
     *
     * @return MutableLiveData<String>
     */
    public MutableLiveData<String> getNotificationMessage() {
        return mNotificationMessage;
    }
}
