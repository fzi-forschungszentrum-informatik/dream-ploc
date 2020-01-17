package de.fzi.dream.ploc.data.structure;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TinyRecord {
    @SerializedName("id")
    private int id;
    @SerializedName("title")
    private String title;
    @SerializedName("year")
    private int year;
    @SerializedName("creators")
    private List<String> creators;

    public TinyRecord(int id, String title, int year, List<String> creators) {
        this.id = id;
        this.title = title;
        this.year = year;
        this.creators = creators;
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

    @Override
    public String toString(){
        return getTitle() + " (" + getYear() + ")";
    }
}
