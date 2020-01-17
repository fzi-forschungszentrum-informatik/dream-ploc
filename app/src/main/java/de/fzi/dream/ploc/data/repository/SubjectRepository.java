package de.fzi.dream.ploc.data.repository;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import java.util.List;

import de.fzi.dream.ploc.data.local.database.CacheDatabase;
import de.fzi.dream.ploc.data.remote.connection.GozerClient;
import de.fzi.dream.ploc.data.remote.paging.ExpertPreviewRemotePagingFactory;
import de.fzi.dream.ploc.data.remote.response.GozerResponse;
import de.fzi.dream.ploc.data.remote.response.SubjectResponse;
import de.fzi.dream.ploc.data.structure.Subject;
import de.fzi.dream.ploc.utility.AppExecutors;
import de.fzi.dream.ploc.utility.network.NetworkBoundResource;
import de.fzi.dream.ploc.utility.network.Resource;

/**
 * The SubjectRepository implementation is the access point for all subject related data manipulation,
 * it returns data directly from the network or if it is cached from the local database.
 *
 * @author Felix Melcher
 */
public class SubjectRepository {
    /** Public class identifier tag for logging */
    public static final String TAG = SubjectRepository.class.getSimpleName();

    private static SubjectRepository INSTANCE = null;
    private final CacheDatabase mCacheDatabase;

    /**
     * Constructor
     *
     * @param cacheDatabase Instance of the internal android room database
     */
    private SubjectRepository(final CacheDatabase cacheDatabase) {
        mCacheDatabase = cacheDatabase;
    }

    public static SubjectRepository getInstance(final CacheDatabase cacheDatabase) {
        if (INSTANCE == null) {
            synchronized (SubjectRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new SubjectRepository(cacheDatabase);
                }
            }
        }
        return INSTANCE;
    }

    /**
     * The readSubjects() method return an overridden {@link NetworkBoundResource} to manage
     * the remote loading, the insertion to the local cache database and the reload.
     *
     * @return LiveData<Resource<List<Subject>>> LiveData of a {@link Resource} object containing
     * the requested data and state.
     */
    public LiveData<Resource<List<Subject>>> readSubjects(){
        return new NetworkBoundResource<List<Subject>, SubjectResponse>(AppExecutors.getInstance()){
            @Override
            protected void saveCallResult(@NonNull SubjectResponse item) {
                if(item.getSubjects() != null){
                    AppExecutors.getInstance().diskIO().execute(() -> mCacheDatabase.cacheDao().insertSubjects(item.getSubjects()));
                }
            }

            @Override
            protected boolean shouldFetch(@Nullable List<Subject> data) {
                if (data != null) {
                    return data.isEmpty();
                } else{ return true; }
            }

            @NonNull
            @Override
            protected LiveData<List<Subject>> loadFromDb() {
                return mCacheDatabase.cacheDao().selectSubjects();
            }

            @NonNull
            @Override
            protected LiveData<GozerResponse<SubjectResponse>> createCall() {
                return GozerClient.getAuthenticatedClient().readSubjects();
            }
        }.getAsLiveData();
    }
}
