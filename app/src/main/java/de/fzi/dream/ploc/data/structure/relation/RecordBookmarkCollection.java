package de.fzi.dream.ploc.data.structure.relation;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;

import de.fzi.dream.ploc.data.structure.entity.Collection;
import de.fzi.dream.ploc.data.structure.entity.RecordBookmark;

import static androidx.room.ForeignKey.CASCADE;

@Entity(tableName = "record_bookmark_collection",
        primaryKeys = {"collectionID", "recordID"},
        foreignKeys = {
        @ForeignKey(
                onDelete = CASCADE,
                entity = Collection.class,
                parentColumns = "id",
                childColumns = "collectionID"
        ),
        @ForeignKey(
                onDelete = CASCADE,
                entity = RecordBookmark.class,
                parentColumns = "record_id",
                childColumns = "recordID"
        )
})
public class RecordBookmarkCollection {
    private int collectionID;
    @ColumnInfo(name = "recordID", index = true)
    private int recordID;

    public RecordBookmarkCollection(int collectionID, int recordID) {
        this.collectionID = collectionID;
        this.recordID = recordID;
    }

    // Getter
    public int getRecordID() {
        return recordID;
    }

    public int getCollectionID() {
        return collectionID;
    }

    // Setter
    public void setRecordID(int recordID) {
        this.recordID = recordID;
    }

    public void setCollectionID(int collectionID) {
        this.collectionID = collectionID;
    }




}