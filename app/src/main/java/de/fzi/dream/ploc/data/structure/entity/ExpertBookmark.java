package de.fzi.dream.ploc.data.structure.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Entity(tableName = "expert_bookmark")
public class ExpertBookmark {
    @PrimaryKey
    @ColumnInfo(name = "expert_id")
    @SerializedName("id")
    private int id;
    @ColumnInfo(name = "name")
    @SerializedName("name")
    private String name;
    @ColumnInfo(name = "lastActivity")
    @SerializedName("last_publication_year")
    private int lastActivity;
    @ColumnInfo(name = "subjects")
    @SerializedName("subjects")
    private List<String> subjects;
    @ColumnInfo(name = "total_publication_count")
    @SerializedName("total_publication_count")
    private int totalPublicationCount;
    @ColumnInfo(name = "affiliation")
    @SerializedName("affiliation")
    private String affiliation;

    public ExpertBookmark(int id, String name, int lastActivity,  List<String>  subjects, int totalPublicationCount, String affiliation) {
        this.id = id;
        this.name = name;
        this.lastActivity = lastActivity;
        this.subjects = subjects;
        this.totalPublicationCount = totalPublicationCount;
        this.affiliation = affiliation;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLastActivity() {
        return lastActivity;
    }

    public void setLastActivity(int lastActivity) {
        this.lastActivity = lastActivity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String>  getSubjects() {
        return subjects;
    }

    public void setSubjects( List<String>  subjects) {
        this.subjects = subjects;
    }

    public int getTotalPublicationCount() {
        return totalPublicationCount;
    }

    public void setTotalPublicationCount(int totalPublicationCount) {
        this.totalPublicationCount = totalPublicationCount;
    }

    public String getSharingText() {
        return "Name: \n" + name + "\n\n" +
                "Last Publication Year: \n" + lastActivity + "\n\n";
    }

    public List<String> getSubjectKeywords(int count) {
        if (subjects.size() >= count) {
            return subjects.subList(0, count);
        }
        return subjects;
    }

    public ExpertPreview toPreview(){
        return new ExpertPreview(id, name, subjects, lastActivity, totalPublicationCount, affiliation);
    }

    public String getAffiliation() {
        return affiliation;
    }

    public void setAffiliation(String affiliation) {
        this.affiliation = affiliation;
    }

}
