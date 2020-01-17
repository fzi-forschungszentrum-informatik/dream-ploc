package de.fzi.dream.ploc.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import de.fzi.dream.ploc.R;
import de.fzi.dream.ploc.ui.fragment.ExpertDetailFragment;

import static de.fzi.dream.ploc.utility.Constants.EXTRA_ID;
import static de.fzi.dream.ploc.utility.Constants.EXTRA_IS_BOOKMARK;

/**
 * The ExpertDetailActivity is responsible for handling the interactions with the
 * DetailExpertFragment.
 *
 * @author Felix Melcher
 */
public class ExpertDetailActivity extends AppCompatActivity
        implements ExpertDetailFragment.OnInteractionListener {

    /**
     * Public class identifier tag
     */
    public static final String TAG = ExpertDetailActivity.class.getSimpleName();


    /*
     * Android Lifecycle Methods
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        setUpToolbar();
        setUpDetailFragment();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    /*
     * Class Methods
     */

    /**
     * Create the detail fragment and pass through the ID for detail data retrieval and the
     * information if the expert is already bookmarked to correctly display bookmark indicators.
     */
    private void setUpDetailFragment() {
        ExpertDetailFragment fragment = (ExpertDetailFragment) getSupportFragmentManager()
                .findFragmentById(R.id.detail_fragment);
        if (fragment == null) {
            fragment = ExpertDetailFragment.newInstance(getIntent().getIntExtra(EXTRA_ID, 0),
                    getIntent().getBooleanExtra(EXTRA_IS_BOOKMARK, false));
        }
        getSupportFragmentManager().beginTransaction()
                .add(R.id.frame_detail_activity, fragment, ExpertDetailFragment.TAG).commit();
    }

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


    /*
     * Interface Methods
     */

    @Override
    public void onExpertRecordClick(int id) {
        startActivity(new Intent(this, RecordDetailActivity.class)
                .putExtra(EXTRA_ID, id));
    }
}
