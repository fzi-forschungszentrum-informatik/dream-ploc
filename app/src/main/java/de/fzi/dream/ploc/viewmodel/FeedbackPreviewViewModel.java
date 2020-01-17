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
import de.fzi.dream.ploc.data.repository.FeedbackRepository;
import de.fzi.dream.ploc.data.repository.ProfileRepository;
import de.fzi.dream.ploc.data.structure.Feedback;
import de.fzi.dream.ploc.data.structure.entity.FeedbackPreview;
import de.fzi.dream.ploc.data.structure.entity.Interest;
import de.fzi.dream.ploc.ui.callback.IOCallback;
import de.fzi.dream.ploc.utility.Notification;
import de.fzi.dream.ploc.utility.network.NetworkBoundResource;
import de.fzi.dream.ploc.utility.network.NetworkState;
import de.fzi.dream.ploc.utility.network.Resource;
/**
 * Application context aware {@link ViewModel}.
 * The FeedbackPreviewViewModel is responsible for preparing and managing the feedback preview list
 * for an Activity or a Fragment. It also handles the communication of the Activity / Fragment with
 * the data sources.
 *
 * @author Felix Melcher
 */
public class FeedbackPreviewViewModel extends AndroidViewModel {

    /**
     * Public class identifier tag
     */
    public static final String TAG = FeedbackPreviewViewModel.class.getSimpleName();

    // LiveData
    private final MutableLiveData<Notification> mNotificationMessage = new MutableLiveData<>();

    // Repositories
    private ProfileRepository mProfileRepository;
    private FeedbackRepository mFeedbackRepository;

    // Parameters
    private Set<Integer> mCurrentInterests = new HashSet<>();

    /**
     * Constructor
     *
     * @param application The application context the ViewModel should be aware of.
     */
    public FeedbackPreviewViewModel(Application application) {
        super(application);
        mFeedbackRepository = ((Ploc) application).getFeedbackRepository();
        mProfileRepository = ((Ploc) application).getProfileRepository();
    }

    /**
     * Get the feedback preview list as a as LiveData PagedList object. This Paged list is build from
     * the remote source, saved into the local data source and then displayed from the local source.
     *
     * @return LiveData<PagedList<FeedbackPreview>>
     */
    public LiveData<PagedList<FeedbackPreview>> getPreviews() {
        return mFeedbackRepository.getFeedbacks();
    }

    /**
     * Get the current network state from the repository as a live data object to observe updates.
     *
     * @return LiveData<NetworkState>
     */
    public LiveData<NetworkState> getNetworkState() {
        return mFeedbackRepository.getNetworkState();
    }

    /**
     * Get a LiveData object to observe messages send from this ViewModel.
     *
     * @return MutableLiveData<Notification>
     */
    public MutableLiveData<Notification> getNotificationMessage() {
        return mNotificationMessage;
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
     * Create a new feedback in the remote data source, if it is successfully created, show a
     * snackbar notification.
     *
     * @param feedback The feedback object that should be created.
     */
    public void createFeedback(Feedback feedback) {
        mFeedbackRepository.createFeedback(feedback, ioFinishedSuccessfully -> {
            if (ioFinishedSuccessfully) {
                mNotificationMessage.postValue(new Notification(getApplication().getResources().getString(R.string.message_feedbacked), false, true, false));
            }
        });
    }

    /**
     * Create a expert profile in the remote data source for the current user, with the
     * identifier provided by the user.
     *
     * @param identifier The expert identifier provided by the user.
     */
    public void setExpertProfile(String identifier){
        mProfileRepository.createExpertProfile(identifier);
    }

    /**
     * Reset the {@link RecordPreviewRemotePagingSource} and rebuild the preview input stream.
     */
    public void resetPaging() {
        mFeedbackRepository.resetPaging();
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
