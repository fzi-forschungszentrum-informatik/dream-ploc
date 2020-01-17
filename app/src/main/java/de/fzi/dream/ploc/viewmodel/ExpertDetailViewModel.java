package de.fzi.dream.ploc.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import de.fzi.dream.ploc.Ploc;
import de.fzi.dream.ploc.R;
import de.fzi.dream.ploc.data.repository.ExpertRepository;
import de.fzi.dream.ploc.data.repository.ProfileRepository;
import de.fzi.dream.ploc.data.structure.ExpertDetail;
import de.fzi.dream.ploc.data.structure.entity.ExpertBookmark;
import de.fzi.dream.ploc.utility.Notification;
import de.fzi.dream.ploc.utility.network.NetworkBoundResource;

/**
 * Application context aware {@link ViewModel}.
 * The ExpertDetailViewModel is responsible for preparing and managing the expert detail data
 * for an Activity or a Fragment. It also handles the communication of the Activity / Fragment with
 * the data sources.
 *
 * @author Felix Melcher
 */
public class ExpertDetailViewModel extends AndroidViewModel {

    /**
     * Public class identifier tag
     */
    public static final String TAG = ExpertDetailViewModel.class.getSimpleName();

    // Repositories
    private final ExpertRepository mExpertRepository;
    private final ProfileRepository mProfileRepository;

    // LiveData
    private final MutableLiveData<ExpertDetail> mExpertDetail = new MutableLiveData<>();
    private final MutableLiveData<Notification> mNotificationMessage = new MutableLiveData<>();

    /**
     * Constructor
     *
     * @param application The application context the ViewModel should be aware of.
     */
    public ExpertDetailViewModel(Application application) {
        super(application);
        mExpertRepository = ((Ploc) application).getExpertRepository();
        mProfileRepository = ((Ploc) application).getProfileRepository();
        mExpertDetail.setValue(null);
    }

    /**
     * Get the expert details as LiveData object.
     *
     * @return LiveData<ExpertDetail>
     */
    public LiveData<ExpertDetail> getDetails(int id) {
        mExpertRepository.readExpertDetail(id, mExpertDetail::postValue);
        return mExpertDetail;
    }

    /**
     * Set the current delivered expert as bookmark in the remote data source and if this succeeded
     * also set the bookmark in the local data source.
     *
     * @param detail the {@link ExpertDetail} object of the expert that should be bookmarked.
     */
    public void setBookmark(ExpertDetail detail) {
        mProfileRepository.createExpertBookmark(detail.toPreview(), success -> {
            if (success) {
                mExpertRepository.invalidateLocalDataSource();
                mNotificationMessage.postValue(new Notification(getApplication().getResources().getString(R.string.message_bookmarked), false, false, false));
            }
        });
    }

    /**
     * Delete the expert bookmark in the remote data source and if this succeeded also delete it
     * from the local data source.
     *
     * @param detail the {@link ExpertDetail} object of the expert that should be bookmarked.
     */
    public void deleteBookmark(ExpertDetail detail) {
        mProfileRepository.deleteExpertBookmark(detail.getExpertID(), success -> {
            if (success) {
                mNotificationMessage.postValue(new Notification(getApplication().getResources().getString(R.string.message_unbookmarked), false, false, false));
            }
        });
    }

    /**
     * Get a LiveData object to observe messages send from this ViewModel.
     *
     * @return MutableLiveData<Notification>
     */
    public MutableLiveData<Notification> getNotificationMessage() {
        return mNotificationMessage;
    }
}
