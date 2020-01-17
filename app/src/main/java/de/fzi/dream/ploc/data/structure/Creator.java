package de.fzi.dream.ploc.data.structure;

import com.google.gson.annotations.SerializedName;

public class Creator {
    @SerializedName("id")
    private int mID;
    @SerializedName("full_name")
    private String mFullName;
    @SerializedName("location_id")
    private int mLocationId;
    @SerializedName("expert_id")
    private int mExpertID;

    public Creator(int id, String fullName, int locationId, int expertId) {
        mID = id;
        mFullName = fullName;
        mLocationId = locationId;
        mExpertID = expertId;

    }

    public int getId() {
        return mID;
    }

    public String getFullName() {
        return mFullName;
    }

    public int getLocationId() {
        return mLocationId;
    }

    public int getExpertId() {
        return mExpertID;
    }
}
