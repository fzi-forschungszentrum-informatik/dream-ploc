package de.fzi.dream.ploc.data.structure;

import androidx.annotation.NonNull;
import androidx.databinding.BaseObservable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import de.fzi.dream.ploc.R;

//@Entity(tableName = "record_preview_cache")
public class FeedbackGiveRecord extends BaseObservable {
   // @PrimaryKey
    @NonNull
  //  @ColumnInfo(name = "record_id")
    @SerializedName("id")
    private int id;
   // @ColumnInfo(name = "title")
    @SerializedName("title")
    private String title;
   // @ColumnInfo(name = "year")
    @SerializedName("year")
    private int year;
   // @ColumnInfo(name = "creators")
    @SerializedName("creators")
    private List<String> creators;
   // @ColumnInfo(name = "teaser")
    @SerializedName("teaser")
    private String teaser;
   // @ColumnInfo(name = "type")
    @SerializedName("type")
    private int type;
   // @ColumnInfo(name = "is_new")
    @SerializedName("is_new")
    private boolean isNew;
   // @ColumnInfo(name = "subject_keywords")
    @SerializedName("subject_keywords")
    private List<String> subjectKeywords;


    public FeedbackGiveRecord(String title, String status){
        this.title = title;
        this.teaser = status;
    }

    public FeedbackGiveRecord(int id, String title, int year, List<String> creators, String teaser, int type, boolean isNew, List<String> subjectKeywords) {
        this.id = id;
        this.title = title;
        this.year = year;
        this.creators = creators;
        this.teaser = teaser;
        this.type = type;
        this.isNew = isNew;
        this.subjectKeywords = subjectKeywords;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public List<String> getCreators() {
        return creators;
    }

    public void setCreators(List<String> creators) {
        this.creators = creators;
    }

    public String getTeaser() {
        return teaser;
    }

    public void setTeaser(String teaser) {
        this.teaser = teaser;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean aNew) {
        isNew = aNew;
    }

    public void setMarkedAsRead(boolean markedAsRead) {
        this.isNew = isNew;
    }

    public List<String> getSubjectKeywords() {
        return subjectKeywords;
    }

    public void setSubjectKeywords(List<String> subjectKeywords) {
        this.subjectKeywords = subjectKeywords;
    }

    public List<String> getSubjectKeywords(int count) {
        if (subjectKeywords.size() >= count) {
            return subjectKeywords.subList(0, count - 1);
        }
        return subjectKeywords;
    }

    public int typeToResource() {
        switch (type) {
            case 0:
                return R.drawable.ic_format_align_left_black_24dp;
            case 1:
                return R.drawable.ic_class_black_24dp;
            default:
                return R.drawable.ic_library_books_black_24dp;
        }
    }
}
