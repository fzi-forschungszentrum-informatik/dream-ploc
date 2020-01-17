package de.fzi.dream.ploc.ui.activity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import de.fzi.dream.ploc.R;
import de.fzi.dream.ploc.ui.fragment.RecordBookmarkFragment;
import de.fzi.dream.ploc.ui.fragment.RecordDetailFragment;

import static de.fzi.dream.ploc.utility.Constants.EXTRA_ID;
import static de.fzi.dream.ploc.utility.Constants.EXTRA_IS_BOOKMARK;

/**
 * The RecordDetailActivity is the container for the DetailRecordFragment and
 * responsible for handling the interactions with the DetailRecordFragment.
 *
 * @author Felix Melcher
 */
public class RecordDetailActivity extends AppCompatActivity {
    /**
     * Public class identifier tag
     */
    public static final String TAG = RecordDetailActivity.class.getSimpleName();


    /*
     * Android Lifecycle Methods
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        setUpToolbar();
        if (savedInstanceState == null) {
            setUpDetailFragment();
        }
    }


    /*
     * Class Methods
     */

    /**
     * Set up the toolbar and enable the back navigation.
     */
    private void setUpToolbar() {
        setSupportActionBar(findViewById(R.id.toolbar_detail_activity));
        ActionBar mActionBar = getSupportActionBar();
        if (mActionBar != null) {
            mActionBar.setDisplayHomeAsUpEnabled(true);
            mActionBar.setTitle(R.string.title_activity_details);
        }
    }

    /**
     * Create the detail fragment and pass through the ID for detail data retrieval and the
     * information if the record is already bookmarked to correctly display bookmark indicators.
     */
    private void setUpDetailFragment() {
        RecordDetailFragment fragment = (RecordDetailFragment) getSupportFragmentManager()
                .findFragmentById(R.id.detail_fragment);
        if (fragment == null) {
            fragment = RecordDetailFragment.newInstance(getIntent().getIntExtra(EXTRA_ID, 0),
                    getIntent().getBooleanExtra(EXTRA_IS_BOOKMARK, false));
        }
        getSupportFragmentManager().beginTransaction()
                .add(R.id.frame_detail_activity, fragment, RecordBookmarkFragment.TAG).commit();
    }

    /*
     * Interface Methods
     */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
