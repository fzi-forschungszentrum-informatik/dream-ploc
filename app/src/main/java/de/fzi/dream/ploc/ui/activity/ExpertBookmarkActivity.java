package de.fzi.dream.ploc.ui.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import de.fzi.dream.ploc.R;
import de.fzi.dream.ploc.ui.fragment.ExpertBookmarkFragment;

import static de.fzi.dream.ploc.utility.Constants.EXTRA_ID;
import static de.fzi.dream.ploc.utility.Constants.EXTRA_IS_BOOKMARK;

/**
 * The ExpertBookmarkActivity is responsible for handling the interactions with the
 * BookmarkExpertFragment.
 *
 * @author Felix Melcher
 */
public class ExpertBookmarkActivity extends AppCompatActivity implements ExpertBookmarkFragment.OnInteractionListener {

    /**
     * Public class identifier tag
     */
    public static final String TAG = ExpertBookmarkActivity.class.getSimpleName();

    /*
     * Android Lifecycle Methods
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmark);
        setUpToolbar();
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.frame_bookmark_activity, new ExpertBookmarkFragment(), ExpertBookmarkFragment.TAG).commit();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }



    /*
     * Class Methods
     */

    /**
     * Set up the toolbar and enable the back navigation.
     */
    public void setUpToolbar() {
        setSupportActionBar(findViewById(R.id.toolbar_bookmark_activity));
        ActionBar mActionBar = getSupportActionBar();
        if (mActionBar != null) {
            mActionBar.setDisplayHomeAsUpEnabled(true);
            mActionBar.setTitle(R.string.title_activity_experts_bookmarks);
        }
    }

    /*
     * Interface Methods
     */

    @Override
    public void onExpertBookmarkClicked(int id) {
        startActivity(new Intent(this, ExpertDetailActivity.class)
                .putExtra(EXTRA_ID, id)
                .putExtra(EXTRA_IS_BOOKMARK, true));
    }
}
