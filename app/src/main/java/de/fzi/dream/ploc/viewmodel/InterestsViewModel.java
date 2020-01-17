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
import de.fzi.dream.ploc.data.repository.SubjectRepository;
import de.fzi.dream.ploc.data.structure.Subject;
import de.fzi.dream.ploc.data.structure.entity.ExpertBookmark;
import de.fzi.dream.ploc.data.structure.entity.Interest;
import de.fzi.dream.ploc.utility.Notification;
import de.fzi.dream.ploc.utility.network.NetworkBoundResource;
import de.fzi.dream.ploc.utility.network.Resource;
/**
 * Application context aware {@link ViewModel}.
 * The InterestsViewModel is responsible for preparing and managing the interest profile
 * for an Activity or a Fragment. It also handles the communication of the Activity / Fragment with
 * the data sources.
 *
 * @author Felix Melcher
 */
public class InterestsViewModel extends AndroidViewModel {

    /**
     * Public class identifier tag
     */
    public static final String TAG = InterestsViewModel.class.getSimpleName();

    // Repositories
    private final SubjectRepository mSubjectRepository;
    private final ProfileRepository mProfileRepository;

    // LiveData
    private final MutableLiveData<Notification> mNotificationMessage = new MutableLiveData<>();

    /**
     * Constructor
     *
     * @param application The application context the ViewModel should be aware of.
     */
    public InterestsViewModel(Application application) {
        super(application);
        mSubjectRepository = ((Ploc) application).getSubjectRepository();
        mProfileRepository = ((Ploc) application).getProfileRepository();
    }

    /**
     * Get a LiveData {@link NetworkBoundResource} object that represents the current set of
     * subjects defined in the remote data source and stored in the local data source.
     *
     * @return LiveData<Resource<List<Subject>>>
     */
    public LiveData<Resource<List<Subject>>> getSubjects() {
        return mSubjectRepository.readSubjects();
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

    /**
     * Get a LiveData object of the current record count that matches the user defined interest profile.
     *
     * @return LiveData<Integer>
     */
    public LiveData<Integer> getRecordCount(){
        return mProfileRepository.getRecordCount();
    }

    /**
     * Add a new interest to the users interest profile, if the interest is successfully saved to
     * the remote and local data source display a snackbar message.
     *
     * @param interest the {@link Interest} object to be added.
     */
    public void setInterest(Subject interest) {
        mProfileRepository.createInterest(interest, success -> {
            if(success){
                mNotificationMessage
                        .postValue(new Notification(getApplication().getResources()
                                .getString(R.string.message_interests_added),
                                false, false, false));
            }
        });
    }

    /**
     * Delete the interest in the remote data source and if this succeeded also delete it
     * from the local data source.
     *
     * @param id the identifier of the interest to be deleted.
     */
    public void deleteInterest(int id) {
        mProfileRepository.deleteInterest(id, success -> {
            if(success){
                mNotificationMessage
                        .postValue(new Notification(getApplication().getResources()
                                .getString(R.string.message_interests_deleted),
                                false, false, false));
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
