package de.fzi.dream.ploc;

import android.app.Application;

import de.fzi.dream.ploc.data.local.database.CacheDatabase;
import de.fzi.dream.ploc.data.repository.ExpertRepository;
import de.fzi.dream.ploc.data.repository.FeedbackRepository;
import de.fzi.dream.ploc.data.repository.ProfileRepository;
import de.fzi.dream.ploc.data.repository.RecordRepository;
import de.fzi.dream.ploc.data.repository.SubjectRepository;

/**
 * Android Application class. Used for accessing singletons.
 *
 * @author Felix Melcher
 */
public class Ploc extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    /**
     * Get a singleton object of the local SQLite room database.
     *
     * @return CacheDatabase Singleton object of the local database.
     */
    private CacheDatabase getCacheRoomDatabase() {
        return CacheDatabase.getInstance(getApplicationContext());
    }

    /**
     * Get a singleton object of the ProfileRepository.
     *
     * @return ProfileRepository Singleton object of the ProfileRepository.
     */
    public ProfileRepository getProfileRepository() {
        return ProfileRepository.getInstance(getCacheRoomDatabase());
    }

    /**
     * Get a singleton object of the SubjectRepository.
     *
     * @return SubjectRepository Singleton object of the SubjectRepository.
     */
    public SubjectRepository getSubjectRepository() {
        return SubjectRepository.getInstance(getCacheRoomDatabase());
    }

    /**
     * Get a singleton object of the RecordRepository.
     *
     * @return RecordRepository Singleton object of the RecordRepository.
     */
    public RecordRepository getRecordRepository() {
        return RecordRepository.getInstance(getCacheRoomDatabase());
    }

    /**
     * Get a singleton object of the ExpertRepository.
     *
     * @return ExpertRepository Singleton object of the ExpertRepository.
     */
    public ExpertRepository getExpertRepository() {
        return ExpertRepository.getInstance(getCacheRoomDatabase());
    }

    /**
     * Get a singleton object of the FeedbackRepository.
     *
     * @return FeedbackRepository Singleton object of the FeedbackRepository.
     */
    public FeedbackRepository getFeedbackRepository() {
        return FeedbackRepository.getInstance(getCacheRoomDatabase());
    }

}