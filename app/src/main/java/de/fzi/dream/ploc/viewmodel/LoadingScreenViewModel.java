package de.fzi.dream.ploc.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import de.fzi.dream.ploc.Ploc;
import de.fzi.dream.ploc.data.repository.ProfileRepository;
import de.fzi.dream.ploc.data.repository.SubjectRepository;
import de.fzi.dream.ploc.data.structure.Subject;
import de.fzi.dream.ploc.data.structure.entity.User;
import de.fzi.dream.ploc.utility.network.NetworkBoundResource;
import de.fzi.dream.ploc.utility.network.Resource;
/**
 * Application context aware {@link ViewModel}.
 * The LoadingScreenViewModel is responsible for preparing and managing the initial loading process
 * of the application for an Activity or a Fragment. It also handles the communication of the
 * Activity / Fragment with the data sources.
 *
 * @author Felix Melcher
 */
public class LoadingScreenViewModel extends AndroidViewModel {

    /**
     * Public class identifier tag
     */
    public static final String TAG = LoadingScreenViewModel.class.getSimpleName();

    // Repositories
    private final ProfileRepository mProfileRepository;
    private final SubjectRepository mSubjectRepository;

    /**
     * Constructor
     *
     * @param application The application context the ViewModel should be aware of.
     */
    public LoadingScreenViewModel(Application application) {
        super(application);
        mProfileRepository = ((Ploc) getApplication()).getProfileRepository();
        mSubjectRepository = ((Ploc) getApplication()).getSubjectRepository();
    }

    /**
     * Create a user profile in the remote data source for the current user.
     */
    public void setUserProfile() {
        mProfileRepository.createUserProfile();
    }

    /**
     * Get a LiveData object of the user profile object from the local data source .
     *
     * @return LiveData<User>
     */
    public LiveData<User> getUserProfile() {
        return mProfileRepository.readUserProfile();
    }

    /**
     * Get a LiveData object from the local data source of the user profile object.
     *
     * @param user The user object to be authenticated to the server.
     */
    public void authenticateUser(User user) {
        mProfileRepository.authenticateUser(user);
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



}
