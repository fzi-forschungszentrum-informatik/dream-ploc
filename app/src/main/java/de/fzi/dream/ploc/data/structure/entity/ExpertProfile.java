package de.fzi.dream.ploc.data.structure.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "profile_expert")
public class ExpertProfile {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "orcid")
    private String orcid;

    public ExpertProfile(@NonNull String orcid) {
        this.orcid = orcid;
    }

    @NonNull
    public String getOrcid() {
        return orcid;
    }

    public void setOrcid(@NonNull String orcid) {
        this.orcid = orcid;
    }
}
