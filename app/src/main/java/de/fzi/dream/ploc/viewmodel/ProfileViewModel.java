package de.fzi.dream.ploc.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import de.fzi.dream.ploc.Ploc;
import de.fzi.dream.ploc.data.repository.ProfileRepository;
import de.fzi.dream.ploc.data.structure.entity.Collection;
import de.fzi.dream.ploc.data.structure.entity.ExpertProfile;
import de.fzi.dream.ploc.data.structure.entity.User;
import de.fzi.dream.ploc.utility.network.Resource;

public class ProfileViewModel extends AndroidViewModel {

    // Class
    public static final String TAG = ProfileViewModel.class.getSimpleName();

    // Repository
    private final ProfileRepository mRepository;

    // LiveData
    private final MutableLiveData<Boolean> mIsExpert = new MutableLiveData<>();
    private final MutableLiveData<Boolean> mProfileDeleted = new MutableLiveData<>();

    // Constructor
    public ProfileViewModel(Application application) {
        super(application);
        mRepository = ((Ploc) application).getProfileRepository();
        mIsExpert.postValue(false);
        mProfileDeleted.postValue(false);
    }

    // Getter
    public LiveData<Resource<List<Collection>>> getCollections() {
        return mRepository.readCollections();
    }

    public LiveData<Boolean> getProfileLoaded() {
        return mIsExpert;
    }

    public LiveData<User> readUserProfile() { return mRepository.readUserProfile(); }

    // Setter
    public void setCollection(String name) {
        mRepository.createCollection(name);
    }

    // Data manipulation
    public void deleteProfile() {
        mRepository.deleteUser();
    }

    public void deleteCollection(Collection collection) {
        mRepository.deleteCollection(collection);
    }

    public void editCollection(Collection collection) {
        mRepository.updateCollection(collection);
    }

    public LiveData<ExpertProfile> readExpertProfile() {
        return mRepository.readExpertProfile();
    }
}
