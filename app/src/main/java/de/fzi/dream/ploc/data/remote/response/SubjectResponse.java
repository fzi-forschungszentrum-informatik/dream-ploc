package de.fzi.dream.ploc.data.remote.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import de.fzi.dream.ploc.data.structure.Subject;


public class SubjectResponse {
    @SerializedName("subjects")
    private final List<Subject> mSubjects;

    public SubjectResponse(List<Subject> subjects) {
        mSubjects = subjects;
    }

    public List<Subject> getSubjects() {
        return mSubjects;
    }
}
