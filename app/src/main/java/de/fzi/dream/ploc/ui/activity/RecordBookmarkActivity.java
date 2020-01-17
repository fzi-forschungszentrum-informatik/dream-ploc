package de.fzi.dream.ploc.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import de.fzi.dream.ploc.R;
import de.fzi.dream.ploc.ui.fragment.RecordBookmarkFragment;

import static de.fzi.dream.ploc.utility.Constants.EXTRA_FILTER_ID;
import static de.fzi.dream.ploc.utility.Constants.EXTRA_FILTER_NAME;
import static de.fzi.dream.ploc.utility.Constants.EXTRA_ID;
import static de.fzi.dream.ploc.utility.Constants.EXTRA_IS_BOOKMARK;

/**
 * The RecordBookmarkActivity is responsible for handling the interactions with the
 * BookmarkRecordFragment.
 *
 * @author Felix Melcher
 */
public class RecordBookmarkActivity extends AppCompatActivity implements RecordBookmarkFragment.OnInteractionListener {

    /**
     * Public class identifier tag
     */
    public static final String TAG = RecordBookmarkActivity.class.getSimpleName();

    private String mFilterName;
    private int mFilterID;


    /*
     * Android Lifecycle Methods
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmark);
        mFilterName = getIntent().getStringExtra(EXTRA_FILTER_NAME);
        mFilterID = getIntent().getIntExtra(EXTRA_FILTER_ID, 0);
        getSupportFragmentManager().beginTransaction().add(R.id.frame_bookmark_activity,
                RecordBookmarkFragment.newInstance(mFilterID, mFilterName),
                RecordBookmarkFragment.TAG).commit();
        setUpToolbar();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }


    /*
     * Class Methods
     */

    /**
     * Set up the toolbar and set the title to the collection filter name if this fragment is created
     * in the context of filtering. Also enable the back navigation in the toolbar.
     */
    public void setUpToolbar() {
        Toolbar mToolbar = findViewById(R.id.toolbar_bookmark_activity);
        setSupportActionBar(mToolbar);
        ActionBar mActionBar = getSupportActionBar();
        if (mActionBar != null) {
            mActionBar.setDisplayHomeAsUpEnabled(true);
            if (mFilterName != null) {
                mActionBar.setTitle(String.format(getString(R.string.title_activity_collection_filter), mFilterName));
            } else {
                mActionBar.setTitle(R.string.title_activity_bookmarks);
            }
        }
    }

    /*
     * Interface Methods
     */

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBookmarkClicked(int id) {
        startActivity(new Intent(this, RecordDetailActivity.class)
                .putExtra(EXTRA_ID, id)
                .putExtra(EXTRA_IS_BOOKMARK, true));
    }
}
