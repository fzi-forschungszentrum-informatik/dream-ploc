package de.fzi.dream.ploc.data.structure.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import de.fzi.dream.ploc.R;

@Entity(tableName = "record_bookmark")
public class RecordBookmark {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "record_id")
    @SerializedName("id")
    private int recordID;
    
    @SerializedName("collection_ids")
    @ColumnInfo(name = "collection_ids")
    private List<Integer> collectionIDs;

    @ColumnInfo(name = "title")
    @SerializedName("title")
    private String title;

    @ColumnInfo(name = "year")
    @SerializedName("year")
    private int year;

    @ColumnInfo(name = "creators")
    @SerializedName("creators")
    private String creators;

    @ColumnInfo(name = "subjects")
    @SerializedName("subjects")
    private List<String> subjects;

    @ColumnInfo(name = "type")
    @SerializedName("type")
    private int type;

    @ColumnInfo(name ="abstract")
    @SerializedName("abstract")
    private String abstractText;

    @ColumnInfo(name = "visited")
    @SerializedName("visited")
    private boolean visited;


    public RecordBookmark(int recordID, String title, String abstractText, int year, String creators, int type, List<Integer> collectionIDs, boolean visited, List<String> subjects) {
        this.recordID = recordID;
        this.collectionIDs = (collectionIDs != null) ? collectionIDs : new ArrayList<>();
        this.title = title;
        this.year = year;
        this.creators = creators;
        this.type = type;
        this.abstractText = abstractText;
        this.subjects = subjects;
        this.visited = visited;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getYear() {
        return this.year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getCreators() {
        return this.creators;
    }

    public void setCreators(String creators) {
        this.creators = creators;
    }

    public int getType() {
        return this.type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public List<Integer> getCollectionIDs() {
        return this.collectionIDs;
    }

    public void setCollectionIDs(List<Integer> collectionIDs) {
        this.collectionIDs = collectionIDs;
    }

    public int getId() {
        return this.recordID;
    }

    public void setId(int recordID) {
        this.recordID = recordID;
    }

    public int getRecordID() {
        return this.recordID;
    }

    public void setRecordID(int mRecordID) {
        this.recordID = mRecordID;
    }

    public boolean isVisited() {
        return visited;
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
    }

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

    public String getSharingText() {
        return "Title: \n" + title + "\n\n" +
                "Year: \n" + year + "\n\n" +
                "Author(s): \n" + creators + "\n\n";
    }

    public String getShortedTitle(int maxChars) {
        return title.length() <= maxChars ? title : title.substring(0, maxChars) + "...";
    }

    public List<String> getSubjectKeywords(int count) {
        if (subjects.size() >= count) {
            return subjects.subList(0, count);
        }
        return subjects;
    }

    public String getAbstractText() {
        return abstractText;
    }

    public void setAbstractText(String teaser) {
        this.abstractText = teaser;
    }

    public List<String> getSubjects() {
        return subjects;
    }

    public void setSubjects(List<String> subjects) {
        this.subjects = subjects;
    }

    public RecordPreview toPreview() {
        return new RecordPreview(getId(), getTitle(), getYear(), getCreators(), getAbstractText(), getType(), isVisited(), getSubjects());
    }
}
