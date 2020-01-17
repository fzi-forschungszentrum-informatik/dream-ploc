package de.fzi.dream.ploc.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import de.fzi.dream.ploc.Ploc;
import de.fzi.dream.ploc.R;
import de.fzi.dream.ploc.data.repository.FeedbackRepository;
import de.fzi.dream.ploc.data.repository.ProfileRepository;
import de.fzi.dream.ploc.data.repository.RecordRepository;
import de.fzi.dream.ploc.data.structure.ExpertDetail;
import de.fzi.dream.ploc.data.structure.Feedback;
import de.fzi.dream.ploc.data.structure.RecordDetail;
import de.fzi.dream.ploc.ui.callback.FeedbackDetailCallback;
import de.fzi.dream.ploc.ui.callback.IOCallback;
import de.fzi.dream.ploc.ui.callback.RecordDetailCallback;
import de.fzi.dream.ploc.utility.Notification;

/**
 * Application context aware {@link ViewModel}.
 * The RecordDetailViewModel is responsible for preparing and managing the record detail data
 * for an Activity or a Fragment. It also handles the communication of the Activity / Fragment with
 * the data sources.
 *
 * @author Felix Melcher
 */
public class RecordDetailViewModel extends AndroidViewModel {

    /**
     * Public class identifier tag
     */
    public static final String TAG = RecordDetailViewModel.class.getSimpleName();

    // Repositories
    private final RecordRepository mRecordRepository;
    private final ProfileRepository mProfileRepository;
    private final FeedbackRepository mFeedbackRepository;

    // LiveData
    private final MutableLiveData<RecordDetail> mRecordDetail = new MutableLiveData<>();
    private final MutableLiveData<List<Feedback>> mFeedbackDetail = new MutableLiveData<>();
    private final MutableLiveData<Notification> mNotificationMessage = new MutableLiveData<>();

    /**
     * Constructor
     *
     * @param application The application context the ViewModel should be aware of.
     */
    public RecordDetailViewModel(Application application) {
        super(application);
        mRecordRepository = ((Ploc) application).getRecordRepository();
        mProfileRepository = ((Ploc) application).getProfileRepository();
        mFeedbackRepository = ((Ploc) application).getFeedbackRepository();
        mRecordDetail.setValue(null);
    }

    /**
     * Get the record details as LiveData object.
     *
     * @return LiveData<RecordDetail>
     */
    public LiveData<RecordDetail> getDetails(int id) {
        mRecordRepository.readRecordDetail(id, mRecordDetail::postValue);
        return mRecordDetail;
    }

    /**
     * Get a LiveData list of all feedbacks related to the current record from the remote data source.
     *
     * @param id the identifier of the record for which the feedbacks should be retrieved.
     * @return LiveData<List<Feedback>> the list of all feedbacks related to given record identifier.
     */
    public LiveData<List<Feedback>> getFeedbacks(int id) {
        mFeedbackRepository.readFeedbackDetail(id, mFeedbackDetail::postValue);
        return mFeedbackDetail;
    }

    /**
     * Set the current delivered record as bookmark in the remote data source and if this succeeded
     * also set the bookmark in the local data source.
     *
     * @param detail the {@link RecordDetail} object of the record that should be bookmarked.
     */
    public void setBookmark(RecordDetail detail) {
        mProfileRepository.createRecordBookmark(detail.toPreview(), success -> {
            if (success) {
                mRecordRepository.invalidateLocalDataSource();
                mNotificationMessage.postValue(new Notification(getApplication().getResources().getString(R.string.message_bookmarked), false, false, false));
            }
        });
    }

    /**
     * Delete the record bookmark in the remote data source and if this succeeded also delete it
     * from the local data source.
     *
     * @param detail the {@link RecordDetail} object of the record that should be bookmarked.
     */
    public void deleteBookmark(RecordDetail detail) {
        mProfileRepository.deleteRecordBookmark(detail.getRecordID(), success -> {
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
