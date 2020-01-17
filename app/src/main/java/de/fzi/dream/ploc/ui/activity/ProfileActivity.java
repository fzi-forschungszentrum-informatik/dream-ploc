package de.fzi.dream.ploc.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import de.fzi.dream.ploc.R;
import de.fzi.dream.ploc.ui.fragment.ProfileFragment;

/**
 * ProfileActivity, is the container for the ProfileFragment and is responsible for handling
 * the fragment callbacks.
 *
 * @author Felix Melcher
 */
public class ProfileActivity extends AppCompatActivity
        implements ProfileFragment.OnInteractionListener {

    /**
     * Public class identifier tag
     */
    public static final String TAG = ProfileActivity.class.getSimpleName();

    /*
     * Android Lifecycle Methods
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        setUpToolbar();
        setUpFragment();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }


    /*
     * Class Methods
     */

    private void setUpToolbar() {
        setSupportActionBar(findViewById(R.id.toolbar_profile_activity));
        ActionBar mActionBar = getSupportActionBar();
        if (mActionBar != null) {
            mActionBar.setDisplayHomeAsUpEnabled(true);
            mActionBar.setTitle(R.string.title_activity_profile);
        }
    }

    private void setUpFragment() {
        getSupportFragmentManager().beginTransaction()
                .add(R.id.frame_profile_activity, new ProfileFragment(), ProfileFragment.TAG).commit();
    }


    /*
     * Interface Methods
     */

    /**
     * Restart the application after the delete profile button is clicked
     */
    @Override
    public void onDeleteProfileClicked() {
        Intent restartIntent = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
        if (restartIntent != null) {
            restartIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(restartIntent);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
