package de.fzi.dream.ploc.data.structure;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.stream.Collectors;

import de.fzi.dream.ploc.R;
import de.fzi.dream.ploc.data.structure.entity.RecordPreview;

public class RecordDetail {
    @SerializedName("id")
    private Integer mRecordID;
    @SerializedName("title")
    private String mTitle;
    @SerializedName("creators")
    private List<String> mCreators;
    @SerializedName("year")
    private String mYear;
    @SerializedName("abstract")
    private String mAbstract;
    @SerializedName("subjects")
    private List<String> mSubjects;
    @SerializedName("oa")
    private Boolean mOpenAccess;
    @SerializedName("type")
    private int mType;
    @SerializedName("pdf_link")
    private String mPDFLink;
    @SerializedName("repository_link")
    private String mRepositoryLink;
    @SerializedName("doi")
    private String mDOI;
    @SerializedName("comment_count")
    private int mCommentCount = 0;
    @SerializedName("review_count")
    private int mReviewCount = 0;

    public RecordDetail(Integer recordID, String title, List<String> creators, String year, String description, List<String> subjects, Boolean openAccess, int type, String pdfLink, String doi, int commentCount, int reviewCount) {
        mRecordID = recordID;
        mTitle = title;
        mCreators = creators;
        mYear = year;
        mAbstract = description;
        mSubjects = subjects;
        mOpenAccess = openAccess;
        mType = type;
        mPDFLink = pdfLink;
        mDOI = doi;
        mCommentCount = commentCount;
        mReviewCount = reviewCount;
    }

    public Integer getRecordID() {
        return mRecordID;
    }

    public String getTitle() {
        return mTitle;
    }

    public List<String> getCreators() {
        return mCreators;
    }

    public String getYear() {
        return mYear;
    }

    public String getDescription() {
        return mAbstract;
    }

    public List<String> getSubjects() {
        return mSubjects;
    }

    public Boolean getOpenAccess() {
        return mOpenAccess;
    }

    public int getType() {
        return mType;
    }

    public void setType(int type) {
        mType = type;
    }

    public String getPDFLink() {
        return mPDFLink;
    }

    public String getDOI() {
        return mDOI;
    }

    public int getCommentCount() {
        return mCommentCount;
    }

    public int getReviewCount() {
        return mReviewCount;
    }

    public String getRepositoryLink() {
        return mRepositoryLink;
    }

    public void setRepositoryLink(String mRepositoryLink) {
        this.mRepositoryLink = mRepositoryLink;
    }


    public int typeToResourceIcon() {
        switch (mType) {
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
    public String typeToResourceName() {
        switch (mType) {
            case 0:
                return "Article";
            case 1:
                return "Book";
            case 2:
                return "Other";
            case 3:
                return "Paper";
            case 4:
                return "Report";
            case 5:
                return "Thesis";
            default:
                return "Unknown";
        }
    }
    public String getSharingText() {
        return "Title: \n" + mTitle + "\n\n" +
                "Year: \n" + mYear + "\n\n" +
                "Author(s): \n" + getCreatorsAsString() + "\n\n" +
                "Description: \n" + mAbstract;
    }

    public String getCreatorsAsString() {
        return mCreators.stream().collect(Collectors.joining(", "));
    }

    public RecordPreview toPreview() {
        return new RecordPreview(mRecordID, mTitle, Integer.valueOf(mYear), mCreators.toString(), mAbstract, mType, false, mSubjects);
    }
}
