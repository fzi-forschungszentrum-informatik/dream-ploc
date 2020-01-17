package de.fzi.dream.ploc.data.structure.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;


@Entity(tableName = "profile_user")
public class User {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "id")
    private String guid;
    @ColumnInfo(name = "secret")
    private String secret;
    @ColumnInfo(name = "record_feed_count")
    private int recordFeedCount;

    public User(String guid, String secret) {
        this.guid = guid;
        this.secret = secret;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public int getRecordFeedCount() {
        return recordFeedCount;
    }

    public void setRecordFeedCount(int recordFeedCount) {
        this.recordFeedCount = recordFeedCount;
    }
}
