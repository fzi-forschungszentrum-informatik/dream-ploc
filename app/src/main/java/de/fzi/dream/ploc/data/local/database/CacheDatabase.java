package de.fzi.dream.ploc.data.local.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import de.fzi.dream.ploc.data.local.paging.ExpertPreviewLocalPagingFactory;
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
import de.fzi.dream.ploc.utility.Converters;

/**
 * Database class, containing all cached information used by the application. The entities are
 * representing the database scheme. For each entity a table is created in the database.
 * <p>
 * The TypeConverter is responsible for mapping Lists of Strings and Integers to a SQLite savable
 * String.
 *
 * @author Felix Melcher
 */
@Database(entities = {RecordPreview.class, ExpertPreview.class, FeedbackPreview.class, Interest.class, RecordBookmark.class,
        ExpertBookmark.class, Collection.class, User.class, ExpertProfile.class, RecordBookmarkCollection.class, Subject.class},
        version = 1, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class CacheDatabase extends RoomDatabase {
    /**
     * Public class identifier tag for logging
     */
    public static final String TAG = ExpertPreviewLocalPagingFactory.class.getSimpleName();

    private static volatile CacheDatabase INSTANCE;

    /**
     * Instantiate or returns a {@link CacheDatabase} singleton through the {@link Room}
     * Database builder. It is created with its defined name from {@link Constants}.
     *
     * @return {@link CacheDatabase} singleton.
     */
    public static CacheDatabase getInstance(final Context context) {
        if (INSTANCE == null) {
            synchronized (CacheDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            CacheDatabase.class, Constants.CACHE_DATABASE_NAME)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * Abstract method to retrieve the database access object.
     *
     * @return {@link CacheDao} object.
     */
    public abstract CacheDao cacheDao();

    /**
     * Clears all tables from the database singleton.
     */
    public void clearDatabase() {
        INSTANCE.clearAllTables();
    }
}
