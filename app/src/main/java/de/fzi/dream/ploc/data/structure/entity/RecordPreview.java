package de.fzi.dream.ploc.data.structure.entity;

import androidx.annotation.NonNull;
import androidx.databinding.BaseObservable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import de.fzi.dream.ploc.R;

@Entity(tableName = "record_preview_cache")
public class RecordPreview extends BaseObservable {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "record_id")
    @SerializedName("id")
    private int id;
    @ColumnInfo(name = "title")
    @SerializedName("title")
    private String title;
    @ColumnInfo(name = "year")
    @SerializedName("year")
    private int year;
    @ColumnInfo(name = "creators")
    @SerializedName("creators")
    private String creators;
    @ColumnInfo(name = "abstract")
    @SerializedName("abstract")
    private String teaser;
    @ColumnInfo(name = "type")
    @SerializedName("type")
    private int type;
    @ColumnInfo(name = "visited")
    @SerializedName("visited")
    private boolean visited;
    @ColumnInfo(name = "subjects")
    @SerializedName("subjects")
    private List<String> subjectKeywords;


    public RecordPreview(int id, String title, int year, String creators, String teaser, int type, boolean visited, List<String> subjectKeywords) {
        this.id = id;
        this.title = title;
        this.year = year;
        this.creators = creators;
        this.teaser = teaser;
        this.type = type;
        this.visited = visited;
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

    public String getCreators() {
        return creators;
    }

    public void setCreators(String creators) {
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

    public boolean isVisited() {
        return visited;
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
    }

    public List<String> getSubjectKeywords() {
        return subjectKeywords;
    }

    public void setSubjectKeywords(List<String> subjectKeywords) {
        this.subjectKeywords = subjectKeywords;
    }

    public List<String> getSubjectKeywords(int count) {
        if (subjectKeywords.size() >= count) {
            return subjectKeywords.subList(0, count);
        }
        return subjectKeywords;
    }

    // TODO export to database
    public int typeToResourceIcon() {
        switch (type) {
            case 0:
                return R.drawable.ic_short_text_black_24dp;
            case 1:
                return R.drawable.ic_menu_book_black_24dp;
            case 2:
                return R.drawable.ic_more_horiz_black_24dp;
            case 3:
                return R.drawable.ic_notes_black_24dp;
            case 4:
                return R.drawable.ic_featured_play_list_black_24dp;
            case 5:
                return R.drawable.ic_school_black_24dp;
            default:
                return R.drawable.ic_help_black_24dp;
        }
    }

    public RecordBookmark previewToBookmark() {
        return new RecordBookmark(id, title, teaser, year, creators, type, null, visited, subjectKeywords);
    }

    @Override
    public String toString(){
        return getTitle() + " (" + getYear() + ")";
    }
}
