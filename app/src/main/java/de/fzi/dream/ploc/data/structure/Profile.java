package de.fzi.dream.ploc.data.structure;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import java.util.ArrayList;
import java.util.List;

import de.fzi.dream.ploc.data.structure.entity.Collection;
import de.fzi.dream.ploc.data.structure.entity.ExpertBookmark;
import de.fzi.dream.ploc.data.structure.entity.Interest;
import de.fzi.dream.ploc.data.structure.entity.RecordBookmark;
import de.fzi.dream.ploc.data.structure.entity.RecordPreview;
import de.fzi.dream.ploc.data.structure.entity.User;


public class Profile extends BaseObservable {
    private String mGUID;
    private String mSecret;
    private String mFullName;
    private String mLocationID;
    private List<RecordBookmark> mRecordBookmarks = new ArrayList<>();
    private List<ExpertBookmark> mExpertBookmarks = new ArrayList<>();
    private List<Collection> mCollections = new ArrayList<>();
    private List<Interest> mInterests = new ArrayList<>();

    public Profile(String guid, String secret) {
        mGUID = guid;
        mSecret = secret;
    }

    public Profile(List<Interest> interests, List<RecordBookmark> recordBookmarks , List<ExpertBookmark> expertBookmarks, List<Collection> collections) {
        mRecordBookmarks = (recordBookmarks != null) ? recordBookmarks : new ArrayList<>();
        mExpertBookmarks = (expertBookmarks != null) ? expertBookmarks : new ArrayList<>();
        mCollections = (collections != null) ? collections : new ArrayList<>();
    }

    public Profile(User user, List<Interest> interests, List<RecordBookmark> recordBookmarks , List<ExpertBookmark> expertBookmarks, List<Collection> collections) {
        if(user != null){
            mGUID = user.getGuid();
            mSecret = user.getSecret();
        }
        mRecordBookmarks = (recordBookmarks != null) ? recordBookmarks : new ArrayList<>();
        mExpertBookmarks = (expertBookmarks != null) ? expertBookmarks : new ArrayList<>();
        mCollections = (collections != null) ? collections : new ArrayList<>();
    }

    public List<ExpertBookmark> getExpertBookmarks() {
        return mExpertBookmarks;
    }

    public void setExpertBookmarks(List<ExpertBookmark> expertBookmarks) {
        mExpertBookmarks = expertBookmarks;
    }

    @Bindable
    public String getGuid() {
        return mGUID;
    }

    public void setGuid(String guid) {
        mGUID = guid;
    }

    public String getSecret() {
        return mSecret;
    }

    public void setSecret(String secret) {
        mSecret = secret;
    }

    public String getFullName() {
        return mFullName;
    }

    public void setFullName(String fullName) {
        mFullName = fullName;
    }

    public String getLocationId() {
        return mLocationID;
    }

    public void setLocationId(String locationId) {
        mLocationID = locationId;
    }


    public List<Collection> getCollections() {
        return mCollections;
    }

    public void setCollections(List<Collection> collections) {
        mCollections = collections;
    }

    public void addCollection(Collection collection) {
        if (mCollections == null) {
            mCollections = new ArrayList<>();
        }
        mCollections.add(collection);
    }

    public void removeCollection(int id) {
        for (Collection collection : mCollections) {
            if (collection.getId() == id) {
                mCollections.remove(collection);
                break;
            }
        }
        if(mExpertBookmarks.size() > 0 ){
            for (RecordBookmark b : mRecordBookmarks) {
//                for (Integer collectionID : b.getCollectionIDs())
//                    if (collectionID == id) {
//                        b.getCollectionIDs().remove(collectionID);
//                    }
            }
        }
    }

    public List<RecordBookmark> getBookmarks() {
        return mRecordBookmarks;
    }

    public void setBookmarks(List<RecordBookmark> bookmarks) {
        mRecordBookmarks = bookmarks;
    }

    public void addBookmark(RecordPreview rp) {
        if (mRecordBookmarks == null) {
            mRecordBookmarks = new ArrayList<>();
        }
        mRecordBookmarks.add(new RecordBookmark(rp.getId(), rp.getTitle(), rp.getTeaser(), rp.getYear(), rp.getCreators(), rp.getType(), null, rp.isVisited(), rp.getSubjectKeywords()));
    }

    public void assignCollectionsToBookmark(int assignID, List<Integer> collectionIDs) {
        for (RecordBookmark b : mRecordBookmarks) {
            if (b.getId() == assignID) {
          //      b.setCollectionIDs(collectionIDs);
            }
        }
    }

    public void addExpertBookmark(ExpertBookmark expertPreview) {
        if (mExpertBookmarks == null) {
            mExpertBookmarks = new ArrayList<>();
        }
        mExpertBookmarks.add(expertPreview);
    }



    public List<Interest> getInterests() {
        return mInterests;
    }

    public void setInterests(List<Interest> mInterests) {
        this.mInterests = mInterests;
    }

    public User getUser(){
        return new User(mGUID, mSecret);
    }
}
