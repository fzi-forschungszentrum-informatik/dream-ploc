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
import de.fzi.dream.ploc.data.structure.entity.ExpertBookmark;
import de.fzi.dream.ploc.utility.network.NetworkBoundResource;
import de.fzi.dream.ploc.utility.network.Resource;

/**
 * Application context aware {@link ViewModel}.
 * The ExpertBookmarkViewModel is responsible for preparing and managing the expert bookmark data
 * for an Activity or a Fragment. It also handles the communication of the Activity / Fragment with
 * the data sources.
 *
 * @author Felix Melcher
 */
public class ExpertBookmarkViewModel extends AndroidViewModel {

    /**
     * Public class identifier tag
     */
    public static final String TAG = ExpertBookmarkViewModel.class.getSimpleName();

    // Repository
    private ProfileRepository mRepository;

    // LiveData
    private final MutableLiveData<String> mNotificationMessage = new MutableLiveData<>();

    // Parameters
    private ExpertBookmark mLastDeletedBookmark;

    /**
     * Constructor
     *
     * @param application The application context the ViewModel should be aware of.
     */
    public ExpertBookmarkViewModel(Application application) {
        super(application);
        mRepository = ((Ploc) application).getProfileRepository();
    }

    /**
     * Get the expert bookmarks as LiveData {@link NetworkBoundResource} to switch between
     * remote and local data source if needed.
     *
     * @return LiveData<Resource<List<ExpertBookmark>>>
     */
    public LiveData<Resource<List<ExpertBookmark>>> getBookmarks() {
        return mRepository.readExpertBookmarks();
    }

    /**
     * Delete the expert bookmark in the remote data source and if this succeeded also delete it
     * from the local data source.
     *
     * @param bookmark the {@link ExpertBookmark} object that should be deleted.
     */
    public void deleteBookmark(ExpertBookmark bookmark) {
        mLastDeletedBookmark = bookmark;
        mRepository.deleteExpertBookmark(bookmark.getId(), success -> {

            if (success)
                mNotificationMessage.postValue(String.format(getApplication().getResources().getString(R.string.message_snackbar_delete_bookmark), bookmark.getName()));
        });
    }

    /**
     * Undo the last delete operation and restore the bookmark.
     */
    public void undoDeleteBookmark() {
        mRepository.createExpertBookmark(mLastDeletedBookmark.toPreview(), success -> {
            if (success)
                mNotificationMessage.postValue(getApplication().getResources().getString(R.string.message_snackbar_bookmark_restored));
        });
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
