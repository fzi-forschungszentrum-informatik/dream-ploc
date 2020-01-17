package de.fzi.dream.ploc.utility;
import de.fzi.dream.ploc.BuildConfig;

/**
 * Final class defining app-wide static variables
 *
 * @author Felix Melcher
 */
public final class Constants {


    /*
     *   Misc parameters
     */
    // Gozer API connection variables
    public static String SERVER_ADDRESS = BuildConfig.GOZER_ADDRESS;

    // Pre tag for all logging cases
    public static final String LOG_TAG = "PLOC_LOG_";

    // Which algorithm should the identicon generator use?
    public static final String IDENTICON_HASH_ALGORITHM = "MD5";

    // Placeholder PDF link for all publications that do not provide a valid link
    public static final String PLACEHOLDER_PDF = "https://zenodo.org/record/1468906/files/Burmeister_pocket_library_for_open_content_Poster_2018-10.pdf";

    // ORCID base URL for redirecting ORCID user links
    public static final String ORCID_URL = "https://orcid.org/";

    // Shared preferences file for on boarding
    public static final String PREF_USER_FIRST_TIME = "user_first_time";

    // Standard secret for the prototype //TODO Has to be encrypted in a productive version.
    public static final String DEBUG_SECRET = "abcdefghijk12345";

    // Subjects shown per expert preview
    public static final int EXPERT_SUBJECT_COUNT = 5;

    // Subjects shown per record preview
    public static final int RECORD_SUBJECT_COUNT = 5;


    /*
     *  Expandable TextView parameters
     */
    public static final int COLLAPSED_TEXT_LENGTH = 300;
    public static final int EXPERT_DETAILS_COLLAPSED_MAX_LINES = 10;
    public static final int RECORD_DETAILS_COLLAPSED_MAX_LINE = 10;
    public static final int EXPERT_DETAILS_COLLAPSING_TIME = 400;
    public static final int RECORD_DETAILS_COLLAPSING_TIME = 400;
    public static final String COLLAPSED_ELLIPSIS = " ...";


    /*
     *  Paged lists parameters
     */
    public static final int FEED_PAGING_INITIAL_RANGE_SIZE = 40;
    public static final int FEED_PAGING_PAGE_RANGE_SIZE = 10;
    public static final int FEED_PAGING_PREFETCH_RANGE_SIZE = 20;


    /*
     *  Room database parameters
     */
    public static final int NUMBERS_OF_THREADS = 1;
    public static final String CACHE_DATABASE_NAME = "cache_database";


    /*
     *  Intent extras and fragment arguments
     */
    public static final String EXTRA_NO_INTERESTS = "no_interests";
    public static final String EXTRA_ID = "identifier";
    public static final String EXTRA_IS_BOOKMARK = "is_bookmark";
    public static final String EXTRA_FILTER_ID = "filter_id";
    public static final String EXTRA_FILTER_NAME = "filter_name";
    public static final String EXTRA_ONBOARDING_SECTION = "onboarding_section";


    /*
     *  Sharing intent
     */
    public static final String INTENT_SHARE_TYPE = "text/plain";
    public static final String INTENT_SHARE_SUBJECT_EXPERT = "My Experts from Ploc for you";
    public static final String INTENT_SHARE_SUBJECT_RECORD = "My Publications from Ploc for you";


    /*
     *   Gozer REST API routes
     */
    // User-Profile
    public static final String API_ROUTE_CREATE_USER = "/plocapi/v1/user-profile/create";
    public static final String API_ROUTE_DELETE_USER = "/plocapi/v1/user-profile/delete";

    // Expert-Profile
    public static final String API_ROUTE_CREATE_EXPERT_PROFILE = "/plocapi/v1/expert-profile/create";
    public static final String API_ROUTE_DELETE_EXPERT_PROFILE = "/plocapi/v1/expert-profile/delete";
    public static final String API_ROUTE_READ_EXPERT_PROFILE = "/plocapi/v1/expert-profile/read";

    // Interests
    public static final String API_ROUTE_CREATE_INTEREST = "/plocapi/v1/interest/create";
    public static final String API_ROUTE_READ_INTERESTS = "/plocapi/v1/interests/read";
    public static final String API_ROUTE_DELETE_INTEREST = "/plocapi/v1/interest/delete";

    // Record-Feed
    public static final String API_ROUTE_READ_RECORD_FEED = "/plocapi/v1/record-feed/read";
    public static final String API_ROUTE_SEARCH_RECORD_FEED = "/plocapi/v1/record-feed/search";

    // Expert-Feed
    public static final String API_ROUTE_READ_EXPERTS = "/plocapi/v1/expert-feed/read";
    public static final String API_ROUTE_SEARCH_EXPERT_FEED = "/plocapi/v1/expert-feed/search";

    // Record-Bookmarks
    public static final String API_ROUTE_READ_BOOKMARK_RECORDS = "/plocapi/v1/record-bookmarks/read";
    public static final String API_ROUTE_CREATE_BOOKMARK_RECORD = "/plocapi/v1/record-bookmark/create";
    public static final String API_ROUTE_DELETE_BOOKMARK_RECORD = "/plocapi/v1/record-bookmark/delete";
    public static final String API_ROUTE_UPDATE_BOOKMARK_COLLECTIONS = "/plocapi/v1/record-bookmark/collections/update";

    // Expert-Bookmarks
    public static final String API_ROUTE_CREATE_BOOKMARK_EXPERT = "/plocapi/v1/expert-bookmark/create";
    public static final String API_ROUTE_DELETE_BOOKMARK_EXPERT = "/plocapi/v1/expert-bookmark/delete";
    public static final String API_ROUTE_READ_BOOKMARK_EXPERTS = "/plocapi/v1/expert-bookmarks/read";

    // Collections
    public static final String API_ROUTE_CREATE_COLLECTION = "/plocapi/v1/collection/create";
    public static final String API_ROUTE_UPDATE_COLLECTION = "/plocapi/v1/collection/update";
    public static final String API_ROUTE_DELETE_COLLECTION = "/plocapi/v1/collection/delete";
    public static final String API_ROUTE_READ_COLLECTIONS = "/plocapi/v1/collections/read";

    // Feedback
    public static final String API_ROUTE_READ_FEEDBACK_FEED = "/plocapi/v1/feedback-feed/read";
    public static final String API_ROUTE_CREATE_FEEDBACK = "/plocapi/v1/feedback/create";
    public static final String API_ROUTE_READ_FEEDBACK_DETAIL = "/plocapi/v1/feedback/read";

    // Details
    public static final String API_ROUTE_READ_RECORD_DETAIL = "/plocapi/v1/record-details/read";
    public static final String API_ROUTE_READ_EXPERT_DETAIL = "/plocapi/v1/expert-details/read";

    // Misc
    public static final String API_ROUTE_CREATE_DISINTEREST = "/plocapi/v1/record-dislike/create";
    public static final String API_ROUTE_READ_SUBJECTS = "/plocapi/v1/subjects/read";


    /*
     *  Room SQLite queries
     */
    // Records
    public static final String QUERY_SELECT_RECORD_FEED = "SELECT * FROM record_preview_cache ORDER BY year DESC";
    public static final String QUERY_SELECT_RECORD_FEED_EXCEPT_BOOKMARKS = "SELECT * FROM record_preview_cache WHERE record_id NOT IN (SELECT record_id FROM record_bookmark) ORDER BY year DESC";
    public static final String QUERY_SELECT_RECORD_BOOKMARKS = "SELECT * FROM record_bookmark ORDER BY year ASC";
    public static final String QUERY_SELECT_RECORD_BOOKMARK_BY_ID = "SELECT * FROM record_bookmark WHERE record_id = :id";
    public static final String QUERY_DELETE_RECORD_FEED = "DELETE FROM record_preview_cache";
    public static final String QUERY_SELECT_RECORD_BOOKMARK_BY_COLLECTIONS = "SELECT b.record_id, b.collection_ids, b.title, b.year, b.creators, b.type, b.abstract, b.subjects, b.visited FROM record_bookmark AS b, record_bookmark_collection AS rbc WHERE rbc.collectionID = :id AND b.record_id = rbc.recordID";
    public static final String QUERY_DELETE_RECORD_BOOKMARK_BY_ID = "DELETE FROM record_bookmark WHERE record_id = :id";
    public static final String QUERY_DELETE_RECORD_PREVIEW_BY_ID = "DELETE FROM record_preview_cache WHERE record_id = :id";
    public static final String QUERY_UPDATE_RECORD_EXAMINED = "UPDATE record_preview_cache SET visited = 1 WHERE record_id = :id";
    public static final String QUERY_UPDATE_BOOKMARK_COLLECTION ="UPDATE record_bookmark SET collection_ids = :ids WHERE record_id = :id";

    // Experts
    public static final String QUERY_SELECT_EXPERT_BOOKMARKS = "SELECT * FROM expert_bookmark ORDER BY lastActivity ASC";
    public static final String QUERY_DELETE_EXPERT_FEED = "DELETE FROM expert_preview_cache";
    public static final String QUERY_DELETE_EXPERT_BOOKMARK_BY_ID = "DELETE FROM expert_bookmark WHERE expert_id = :id";
    public static final String QUERY_DELETE_EXPERT_PREVIEW_BY_ID = "DELETE FROM expert_preview_cache WHERE expert_id = :id";
    public static final String QUERY_SELECT_EXPERT_FEED_EXCEPT_BOOKMARKS = "SELECT * FROM expert_preview_cache WHERE expert_id NOT IN (SELECT expert_id FROM expert_bookmark) ORDER BY order_id ASC";

    // Feedback
    public static final String QUERY_DELETE_FEEDBACK_FEED = "DELETE FROM feedback_preview_cache";
    public static final String QUERY_SELECT_FEEDBACK_FEED = "SELECT * FROM feedback_preview_cache ORDER BY year DESC";

    // Interests
    public static final String QUERY_SELECT_INTERESTS_BY_NAME = "SELECT * FROM profile_interest WHERE keyword = :name";
    public static final String QUERY_SELECT_INTERESTS = "SELECT * FROM profile_interest";
    public static final String QUERY_DELETE_INTEREST_BY_ID = "DELETE FROM profile_interest WHERE id = :id";

    // Misc
    public static final String QUERY_SELECT_USER = "SELECT * FROM profile_user LIMIT 1";
    public static final String QUERY_SELECT_EXPERT_PROFILE = "SELECT * FROM profile_expert LIMIT 1";
    public static final String QUERY_SELECT_SUBJECTS = "SELECT * FROM subjects";

    // Collections
    public static final String QUERY_COUNT_COLLECTION_LINK = "SELECT COUNT(*) FROM record_bookmark_collection";
    public static final String QUERY_SELECT_COLLECTIONS = "SELECT * FROM profile_collection";
    public static final String QUERY_SELECT_ALL_COLLECTION_LINK = "SELECT * FROM record_bookmark_collection";
    public static final String QUERY_SELECT_COLLECTION_BY_ID = "SELECT * FROM record_bookmark_collection WHERE collectionID = :id";
}
