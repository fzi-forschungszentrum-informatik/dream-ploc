package de.fzi.dream.ploc.data.structure.entity;

import androidx.databinding.BaseObservable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Entity(tableName = "expert_preview_cache")
public class ExpertPreview extends BaseObservable {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "order_id")
    private int orderID;

    @ColumnInfo(name = "expert_id")
    @SerializedName("id")
    private int id;

    @ColumnInfo(name = "name")
    @SerializedName("name")
    private String name;

    @ColumnInfo(name = "subjects")
    @SerializedName("subjects")
    private List<String> subjectKeywords;

    @ColumnInfo(name = "last_publication_year")
    @SerializedName("last_publication_year")
    private int lastPublicationYear;

    @ColumnInfo(name = "totalPublicationCount")
    @SerializedName("total_publication_count")
    private int totalPublicationCount;

    @ColumnInfo(name = "affiliation")
    @SerializedName("affiliation")
    private String affiliation;

    @Ignore
    private boolean isBookmark = false;

    public ExpertPreview(int id, String name, List<String> subjectKeywords, int lastPublicationYear, int totalPublicationCount, String affiliation) {
        this.id = id;
        this.name = name;
        this.subjectKeywords = subjectKeywords;
        this.lastPublicationYear = lastPublicationYear;
        this.totalPublicationCount = totalPublicationCount;
        this.affiliation = affiliation;
    }

    public void setOrderID(int orderID) {
        this.orderID = orderID;
    }

    public int getId() {
        return id;
    }

    public int getOrderID() {
        return orderID;
    }

    public String getName() {
        return name;
    }

    public List<String> getSubjectKeywords() {
        return subjectKeywords;
    }

    public int getLastPublicationYear() {
        return lastPublicationYear;
    }

    public List<String> getSubjectKeywords(int count) {
        if (subjectKeywords.size() >= count) {
            return subjectKeywords.subList(0, count);
        }
        return subjectKeywords;
    }

    public boolean isBookmark() {
        return isBookmark;
    }

    public void setIsBookmark(boolean isBookmark){
        this.isBookmark = isBookmark;
    }

    public ExpertBookmark toBookmark() {
        return new ExpertBookmark(id, name, lastPublicationYear, subjectKeywords, totalPublicationCount, affiliation);
    }

    public int getTotalPublicationCount() {
        return totalPublicationCount;
    }

    public void setTotalPublicationCount(int totalPublicationCount) {
        this.totalPublicationCount = totalPublicationCount;
    }

    public String getAffiliation() {
        return affiliation;
    }

    public void setAffiliation(String affiliation) {
        this.affiliation = affiliation;
    }
}
