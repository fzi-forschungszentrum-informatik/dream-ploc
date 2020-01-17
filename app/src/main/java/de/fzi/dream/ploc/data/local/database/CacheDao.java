/*
 * Copyright 2019 FZI Research Center for Information Technology
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.fzi.dream.ploc.data.local.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import de.fzi.dream.ploc.data.structure.Subject;
import de.fzi.dream.ploc.data.structure.entity.Collection;
import de.fzi.dream.ploc.data.structure.entity.ExpertBookmark;
import de.fzi.dream.ploc.data.structure.entity.ExpertPreview;
import de.fzi.dream.ploc.data.structure.entity.ExpertProfile;
import de.fzi.dream.ploc.data.structure.entity.FeedbackPreview;
import de.fzi.dream.ploc.data.structure.entity.Interest;
import de.fzi.dream.ploc.data.structure.entity.RecordBookmark;
import de.fzi.dream.ploc.data.structure.entity.RecordPreview;
import de.fzi.dream.ploc.data.structure.entity.User;
import de.fzi.dream.ploc.data.structure.relation.RecordBookmarkCollection;
import de.fzi.dream.ploc.utility.Constants;

/**
 * Database Access Object interface to access the internal SQLite cache database on the phone
 *
 * @author Felix Melcher
 */
@Dao
public interface CacheDao {

    /*
     *  Record Methods
     */

    /**
     * Inserts a new {@link RecordPreview} object to the internal cache database.
     *
     * @param record The new {@link RecordPreview}.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertRecordPreview(RecordPreview record);

    /**
     * Selects all {@link RecordPreview} objects from the internal cache database.
     *
     * @return List of {@link RecordPreview}.
     */
    @Query(Constants.QUERY_SELECT_RECORD_FEED)
    List<RecordPreview> selectRecordPreviews();

    /**
     * Deletes all matching {@link RecordPreview} objects from the internal cache database.
     *
     * @param records The {@link RecordPreview}s to be deleted.
     */
    @Delete
    void deleteRecordPreview(RecordPreview... records);

    /**
     * Deletes a {@link RecordPreview} object from the internal cache database by its identifier.
     *
     * @param id The identifier of the {@link RecordPreview} to be deleted.
     */
    @Query(Constants.QUERY_DELETE_RECORD_PREVIEW_BY_ID)
    void deleteRecordPreviewByID(int id);

    /**
     * Deletes all {@link RecordPreview} objects from the internal cache database.
     */
    @Query(Constants.QUERY_DELETE_RECORD_FEED)
    void deleteRecordPreviews();

    /**
     * Adds a examined flag to a {@link RecordPreview} object in the internal cache database by its identifier.
     *
     * @param id The identifier of the {@link RecordPreview} to be marked as examined.
     */
    @Query(Constants.QUERY_UPDATE_RECORD_EXAMINED)
    void createRecordExamined(int id);


    /*
     *  Expert Methods
     */

    /**
     * Inserts a new {@link ExpertPreview} object to the internal cache database.
     *
     * @param expert The new {@link ExpertPreview}.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertExpertPreview(ExpertPreview expert);

    /**
     * Selects all {@link ExpertPreview} objects from the internal cache database.
     *
     * @return List of {@link ExpertPreview}.
     */
    @Query(Constants.QUERY_SELECT_EXPERT_FEED_EXCEPT_BOOKMARKS)
    List<ExpertPreview> selectExpertPreviews();

    /**
     * Deletes all {@link ExpertPreview} objects from the internal cache database.
     */
    @Query(Constants.QUERY_DELETE_EXPERT_FEED)
    void deleteExpertPreviews();

    /**
     * Deletes a {@link ExpertPreview} object from the internal cache database by its identifier.
     *
     * @param id The identifier of the {@link ExpertPreview} to be deleted.
     */
    @Query(Constants.QUERY_DELETE_EXPERT_PREVIEW_BY_ID)
    void deleteExpertPreviewByID(int id);

    /*
     *  Interest Methods
     */

    /**
     * Inserts a new {@link Interest} object to the internal cache database.
     *
     * @param interest The new {@link Interest}.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertInterest(Interest interest);

    /**
     * Selects an {@link Interest} object from the internal cache database by its name.
     *
     * @return The matching {@link Interest} object.
     */
    @Query(Constants.QUERY_SELECT_INTERESTS_BY_NAME)
    Interest selectInterest(String name);

    /**
     * Selects all {@link Interest} objects as {@link LiveData} from the internal cache database.
     *
     * @return {@link LiveData} List of {@link Interest}s.
     */
    @Query(Constants.QUERY_SELECT_INTERESTS)
    LiveData<List<Interest>> selectInterests();

    /**
     * Deletes all matching {@link Interest} objects from the internal cache database.
     *
     * @param interests The {@link RecordPreview}s to be deleted.
     */
    @Delete
    void deleteInterest(Interest... interests);

    /**
     * Deletes a {@link Interest} object from the internal cache database by its identifier.
     *
     * @param id The identifier of the {@link Interest} to be deleted.
     */
    @Query(Constants.QUERY_DELETE_INTEREST_BY_ID)
    void deleteInterest(int id);


    /*
     *  Subject Methods
     */

    /**
     * Inserts a new {@link Subject} object to the internal cache database.
     *
     * @param subjects The new {@link Subject}.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertSubjects(List<Subject> subjects);

    /**
     * Selects all {@link Interest} objects as {@link LiveData} from the internal cache database.
     *
     * @return {@link LiveData} List of {@link Interest}s.
     */
    @Query(Constants.QUERY_SELECT_SUBJECTS)
    LiveData<List<Subject>> selectSubjects();


    /*
     *  RecordBookmark Methods
     */

    /**
     * Inserts a new {@link RecordBookmark} object to the internal cache database.
     *
     * @param bookmark The new {@link RecordBookmark}.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertRecordBookmark(RecordBookmark bookmark);

    /**
     * Selects a {@link RecordBookmark} object from the internal cache database by its identifier.
     *
     * @param id The identifier of the {@link RecordBookmark} to be selected.
     */
    @Query(Constants.QUERY_SELECT_RECORD_BOOKMARK_BY_ID)
    RecordBookmark selectRecordBookmark(int id);

    /**
     * Selects all {@link RecordBookmark} objects from the internal cache database.
     *
     * @return List of {@link RecordBookmark}s.
     */
    @Query(Constants.QUERY_SELECT_RECORD_BOOKMARKS)
    List<RecordBookmark> selectRecordBookmarks();

    /**
     * Selects all {@link RecordBookmark} objects as {@link LiveData} from the internal
     * cache database.
     *
     * @return {@link LiveData} List of {@link RecordBookmark}s.
     */
    @Query(Constants.QUERY_SELECT_RECORD_BOOKMARKS)
    LiveData<List<RecordBookmark>> selectRecordBookmarksLive();

    /**
     * Deletes a {@link RecordBookmark} object from the internal cache database by its identifier.
     *
     * @param id The identifier of the {@link RecordBookmark} to be deleted.
     */
    @Query(Constants.QUERY_DELETE_RECORD_BOOKMARK_BY_ID)
    void deleteRecordBookmark(int id);

    /*
     *  ExpertBookmark Methods
     */

    /**
     * Inserts a new {@link ExpertBookmark} object to the internal cache database.
     *
     * @param bookmark The new {@link ExpertBookmark}.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertExpertBookmark(ExpertBookmark bookmark);

    /**
     * Selects all {@link ExpertBookmark} objects from the internal cache database.
     *
     * @return List of {@link ExpertBookmark}s.
     */
    @Query(Constants.QUERY_SELECT_EXPERT_BOOKMARKS)
    List<ExpertBookmark> selectExpertBookmarks();

    /**
     * Selects all {@link ExpertBookmark} objects as {@link LiveData} from the internal
     * cache database.
     *
     * @return {@link LiveData} List of {@link ExpertBookmark}s.
     */
    @Query(Constants.QUERY_SELECT_EXPERT_BOOKMARKS)
    LiveData<List<ExpertBookmark>> selectExpertBookmarksLive();

    /**
     * Deletes the matching {@link ExpertBookmark} object from the internal
     * cache database.
     *
     * @param bookmark The object of the {@link ExpertBookmark} to be deleted.
     */
    @Delete
    void deleteExpertBookmark(ExpertBookmark bookmark);

    /**
     * Deletes a {@link ExpertBookmark} object from the internal cache database by its identifier.
     *
     * @param id The identifier of the {@link ExpertBookmark} to be deleted.
     */
    @Query(Constants.QUERY_DELETE_EXPERT_BOOKMARK_BY_ID)
    void deleteExpertBookmarkByID(int id);


    /*
     *  User Methods
     */

    /**
     * Inserts a new {@link User} object to the internal cache database.
     *
     * @param user The new {@link User}.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertUser(User user);

    /**
     * Selects the {@link User} object as {@link LiveData} from the internal
     * cache database.
     *
     * @return {@link LiveData} object of the {@link User}.
     */
    @Query(Constants.QUERY_SELECT_USER)
    LiveData<User> selectUser();


    /*
     *  Collection Methods
     */

    /**
     * Inserts a new {@link Collection} object to the internal cache database.
     *
     * @param collection The new {@link Collection}.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCollection(Collection collection);

    /**
     * Deletes the matching {@link Collection} objects from the internal
     * cache database.
     *
     * @param collection The {@link Collection} objects to be deleted.
     */
    @Delete
    void deleteCollection(Collection... collection);

    /**
     * Selects all {@link Collection} objects as {@link LiveData} from the internal
     * cache database.
     *
     * @return {@link LiveData} list of the {@link Collection}s.
     */
    @Query(Constants.QUERY_SELECT_COLLECTIONS)
    LiveData<List<Collection>> selectCollectionsLive();

    /**
     * Selects all {@link Collection} objects the internal
     * cache database.
     *
     * @return list of the {@link Collection}s.
     */
    @Query(Constants.QUERY_SELECT_COLLECTIONS)
    List<Collection> selectCollections();


    /*
     *  RecordBookmarkCollectionLink Methods
     */

    /**
     * Inserts a new {@link RecordBookmarkCollection} link object to the internal cache database.
     *
     * @param recordBookmarkCollection The new {@link RecordBookmarkCollection}.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertRecordBookmarkCollection(RecordBookmarkCollection recordBookmarkCollection);

    /**
     * Selects all matching {@link RecordBookmark} objects as {@link LiveData} from the
     * internal cache database by the collection identifier.
     *
     * @param id The identifier of the {@link Collection} to be selected.
     * @return {@link LiveData} List of {@link RecordBookmark}s.
     */
    @Query(Constants.QUERY_SELECT_RECORD_BOOKMARK_BY_COLLECTIONS)
    LiveData<List<RecordBookmark>> selectRecordBookmarksByCollectionIDLive(int id);

    /**
     * Selects all {@link RecordBookmarkCollection} objects from the internal
     * cache database.
     *
     * @return List of {@link RecordBookmarkCollection}s.
     */
    @Query(Constants.QUERY_SELECT_ALL_COLLECTION_LINK)
    List<RecordBookmarkCollection> selectCollectionBookmarkLink();

    /**
     * Selects all {@link RecordBookmarkCollection} objects from the internal
     * cache database.
     *
     * @param id The identifier of the {@link RecordBookmarkCollection} to be selected.
     */
    @Query(Constants.QUERY_SELECT_COLLECTION_BY_ID)
    List<RecordBookmarkCollection> selectCollectionBookmarkLinkByID(int id);

    /**
     * Deletes a {@link RecordBookmarkCollection} objects from the internal
     * cache database.
     *
     * @param ids The {@link RecordBookmarkCollection} identifiers for this record bookmark.
     * @param id  The identifier of the {@link RecordBookmark}.
     */
    @Query(Constants.QUERY_UPDATE_BOOKMARK_COLLECTION)
    void updateRecordBookmarkCollection(List<Integer> ids, int id);

    /**
     * Count all {@link RecordBookmarkCollection} objects from the internal
     * cache database.
     *
     * @return Count of {@link RecordBookmarkCollection}s.
     */
    @Query(Constants.QUERY_COUNT_COLLECTION_LINK)
    int selectCollectionBookmarkLinkCount();


    /*
     *  Feedback Methods
     */

    /**
     * Inserts a new {@link FeedbackPreview} object to the internal cache database.
     *
     * @param feedback The new {@link FeedbackPreview}.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertFeedbackPreview(FeedbackPreview feedback);

    /**
     * Selects all {@link FeedbackPreview} objects from the internal cache database.
     *
     * @return List of {@link FeedbackPreview}.
     */
    @Query(Constants.QUERY_SELECT_FEEDBACK_FEED)
    List<FeedbackPreview> selectFeedbackPreviews();

    /**
     * Deletes all {@link ExpertPreview} objects from the internal cache database.
     */
    @Query(Constants.QUERY_DELETE_FEEDBACK_FEED)
    void deleteFeedbackPreviews();


    /*
     *  User Methods
     */

    /**
     * Inserts a new {@link ExpertProfile} object to the internal cache database.
     *
     * @param expertProfile The new {@link ExpertProfile}.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertExpertProfile(ExpertProfile expertProfile);

    /**
     * Selects the {@link ExpertProfile} object as {@link LiveData} from the internal
     * cache database.
     *
     * @return {@link LiveData} object of the {@link ExpertProfile}.
     */
    @Query(Constants.QUERY_SELECT_EXPERT_PROFILE)
    LiveData<ExpertProfile> selectExpertProfile();
}