package de.fzi.dream.ploc.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import de.fzi.dream.ploc.R;
import de.fzi.dream.ploc.utility.Constants;
import de.fzi.dream.ploc.utility.SharedPreferencesAccess;
import de.fzi.dream.ploc.viewmodel.LoadingScreenViewModel;

import static de.fzi.dream.ploc.utility.Constants.PREF_USER_FIRST_TIME;

public class LoadingScreenActivity extends AppCompatActivity {

    /**
     * Public class identifier tag
     */
    public static final String TAG = LoadingScreenActivity.class.getSimpleName();

    private LoadingScreenViewModel mViewModel;
    private TextView mLoadingText;

    /*
     * Android Lifecycle Methods
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_screen);
        findViewById(R.id.progress_bar_loading_activity).setVisibility(View.VISIBLE);
        mLoadingText = findViewById(R.id.text_view_state_loading_activity);
        mLoadingText.setText(getString(R.string.message_loading_user));
        delayStartUp(500);
    }


    /*
     * Class Methods
     */

    /**
     * Delay the start up phase to show logo
     */
    private void delayStartUp(int milliseconds) {
        Runnable r = () -> {
            mViewModel = obtainViewModel();
            observeUser();
        };
        Handler mHandler = new Handler();
        mHandler.postDelayed(r, milliseconds);
    }

    /**
     * Helper method to get the lifecycle bound {@link androidx.lifecycle.ViewModel}, for data
     * retrieval and manipulation.
     */
    private LoadingScreenViewModel obtainViewModel() {
        return new ViewModelProvider(this).get(LoadingScreenViewModel.class);
    }

    /**
     * Observe changes to the user data set on startup, if there is no user create one else authenticate
     * the user to the backend. If the user is authenticated, start observing the subjects.
     */
    private void observeUser() {
        mViewModel.getUserProfile().observe(this, user -> {
            if (user != null && user.getGuid() != null) {
                mViewModel.authenticateUser(user);
                observerSubjects();
            } else {
                mViewModel.setUserProfile();
            }
        });
    }

    /**
     * Observe changes to the subjects keyword data set, if the subjects are loaded, check for onboarding.
     */
    private void observerSubjects() {
        mViewModel.getSubjects().observe(this, subjects -> {
            if (subjects.data != null) {
                switch (subjects.status) {
                    case LOADING: {
                        mLoadingText.setText(getString(R.string.message_loading_subjects));

                        break;
                    }
                    case ERROR: {
                        mLoadingText.setText(subjects.message);
                        break;
                    }
                    case SUCCESS: {
                        if (!subjects.data.isEmpty()) checkForOnBoarding();
                        break;
                    }
                }
            }
        });
    }

    /**
     * Helper method to check if the user has already seen the onboarding tutorial, if not show it
     * else directly start the main view.
     */
    private void checkForOnBoarding() {
        Intent afterLoadingIntent;
        if (Boolean.valueOf(SharedPreferencesAccess.readConstants(LoadingScreenActivity.this, PREF_USER_FIRST_TIME, "true"))) {
            afterLoadingIntent = new Intent(LoadingScreenActivity.this, OnBoardingActivity.class);
        } else {
            afterLoadingIntent = new Intent(LoadingScreenActivity.this, MainActivity.class);
        }
        startActivity(afterLoadingIntent);
        this.finish();
    }
}
