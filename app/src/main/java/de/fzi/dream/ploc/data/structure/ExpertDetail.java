package de.fzi.dream.ploc.data.structure;

import com.google.gson.annotations.SerializedName;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import de.fzi.dream.ploc.data.structure.entity.ExpertPreview;

public class ExpertDetail {
    @SerializedName("id")
    private int mExpertID;
    @SerializedName("name")
    private String mFullName;
    @SerializedName("subjects")
    private List<String> mSubjects;
    @SerializedName("orcid")
    private String mOrcidID;
    @SerializedName("last_publication_year")
    private int mLastPublicationYear;
    @SerializedName("records")
    private List<TinyRecord> mRecords;
    @SerializedName("affiliation")
    private String mAffiliation;

    public ExpertDetail(int expertID, String fullName, List<String> subjects, String orcidID, int lastPublicationYear,  List<TinyRecord> records, String affiliation) {
        mExpertID = expertID;
        mFullName = fullName;
        mSubjects = subjects;
        mOrcidID = orcidID;
        mLastPublicationYear = lastPublicationYear;
        mRecords = records;
        mAffiliation = affiliation;
    }

    public int getExpertID() {
        return mExpertID;
    }

    public void setExpertID(int mExpertID) {
        this.mExpertID = mExpertID;
    }

    public String getFullName() {
        return mFullName;
    }

    public void setFullName(String mFullName) {
        this.mFullName = mFullName;
    }

    public List<String> getSubjects() {
        return mSubjects;
    }

    public void setSubjects(List<String> mSubjects) {
        this.mSubjects = mSubjects;
    }

    public String getOrcidID() {
        return mOrcidID;
    }

    public void setOrcidID(String mOrcidID) {
        this.mOrcidID = mOrcidID;
    }

    public int getLastPublicationYear() {
        return mLastPublicationYear;
    }

    public void setLastPublicationYear(int mLastPublicationYear) {
        this.mLastPublicationYear = mLastPublicationYear;
    }

    public List<TinyRecord> getRecords() {
        return mRecords;
    }

    public void setRecords(List<TinyRecord> mRecords) {
        this.mRecords = mRecords;
    }

    public String getSharingText() {
        StringBuilder text = new StringBuilder();
        text.append(getFullName());
        text.append("\n \n");
        if(getOrcidID() != null) {
            text.append("ORCID-ID: ");
            text.append(getOrcidID());
            text.append("\n \n");
        }
        for(TinyRecord record : getRecords()){
            text.append(record.getTitle() + " (" + getLastPublicationYear() + ") \n \n");
        }

        return text.toString();
    }

    public ExpertPreview toPreview() {
        return new ExpertPreview(mExpertID, mFullName, mSubjects, mLastPublicationYear, mLastPublicationYear, mAffiliation);
    }

    public String getAffiliation() {
        return mAffiliation;
    }

    public void setAffiliation(String affiliation) {
        this.mAffiliation = affiliation;
    }
}
