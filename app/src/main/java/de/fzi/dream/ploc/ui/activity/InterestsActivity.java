package de.fzi.dream.ploc.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import de.fzi.dream.ploc.R;
import de.fzi.dream.ploc.ui.fragment.InterestsFragment;

import static de.fzi.dream.ploc.utility.Constants.EXTRA_NO_INTERESTS;

public class InterestsActivity extends AppCompatActivity
        implements InterestsFragment.OnInteractionListener, KeyEvent.Callback {

    /**
     * Public class identifier tag
     */
    public static final String TAG = InterestsActivity.class.getSimpleName();

    // Handle cases if the user has no interests defined
    private boolean mNoInterests;
    // The toolbar option item that needs to be enabled or disabled depending on the count of interests
    private MenuItem mActionDone;

    /*
     * Android Lifecycle Methods
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interest);
        mNoInterests = getIntent().getBooleanExtra(EXTRA_NO_INTERESTS, false);
        setUpToolbar();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.frame_interest_activity, new InterestsFragment(), InterestsFragment.TAG).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (mNoInterests) {
            getMenuInflater().inflate(R.menu.activity_interests_toolbar_on_start, menu);
            mActionDone = menu.findItem(R.id.action_done).setEnabled(false);
        }
        return true;
    }

    /*
     * Class Methods
     */

    /**
     * Set up the toolbar and enable the back navigation.
     */
    public void setUpToolbar() {
        setSupportActionBar(findViewById(R.id.toolbar_interest_activity));
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null && mNoInterests) {
            actionBar.setDisplayHomeAsUpEnabled(false);
            actionBar.setTitle(R.string.title_activity_no_interests);
        } else if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.title_activity_interests);
        }
    }

    /*
     * Interface Methods
     */

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_done) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (mNoInterests) {
            startActivity(new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME));
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onInterestAdded(int totalCount) {
        if (totalCount > 0 && mNoInterests) {
            mActionDone.setEnabled(true);
        }
    }

    @Override
    public void onInterestRemoved(int totalCount) {
        if (totalCount == 0 && mNoInterests) {
            mActionDone.setEnabled(false);
        }
    }
}
