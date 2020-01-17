package de.fzi.dream.ploc.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import de.fzi.dream.ploc.R;
import de.fzi.dream.ploc.ui.adapter.OnBoardingPagingAdapter;
import de.fzi.dream.ploc.utility.SharedPreferencesAccess;

import static de.fzi.dream.ploc.utility.Constants.EXTRA_NO_INTERESTS;
import static de.fzi.dream.ploc.utility.Constants.PREF_USER_FIRST_TIME;

/**
 * OnBoardingActivity is responsible for handling the fragment pager of the onboarding fragments,
 * including the navigation buttons and page indicators.
 *
 * @author Felix Melcher
 */
public class OnBoardingActivity extends AppCompatActivity {

    /**
     * Public class identifier tag
     */
    public static final String TAG = OnBoardingActivity.class.getSimpleName();

    private ViewPager mViewPager;
    private Button mNextButton;
    private Button mSkipButton, mFinishButton;
    private ImageView[] mIndicators;
    private int mCurrentPageIndex = 0;

    /*
     * Android Lifecycle Methods
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set fullscreen
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorAccent));

        // Layout
        setContentView(R.layout.activity_onboarding);
        mNextButton = findViewById(R.id.button_next_onboarding_activity);
        mSkipButton = findViewById(R.id.button_skip_onboarding_activity);
        mFinishButton = findViewById(R.id.button_finish_onboarding_activity);
        mIndicators = new ImageView[]{
                findViewById(R.id.image_view_indicator_one_onboarding_activity),
                findViewById(R.id.image_view_indicator_two_onboarding_activity),
                findViewById(R.id.image_view_indicator_three_onboarding_activity),
                findViewById(R.id.image_view_indicator_four_onboarding_activity)
        };

        // ViewPager
        mViewPager = findViewById(R.id.view_pager_onboarding_activity);
        mViewPager.setAdapter(new OnBoardingPagingAdapter(getSupportFragmentManager()));
        mViewPager.setCurrentItem(mCurrentPageIndex);
        mViewPager.setBackgroundColor(ContextCompat.getColor(this, R.color.colorWhite));
        updateIndicators(mCurrentPageIndex);

        //Listener
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                mCurrentPageIndex = position;
                updateIndicators(mCurrentPageIndex);
                mNextButton.setVisibility(position == 3 ? View.GONE : View.VISIBLE);
                mFinishButton.setVisibility(position == 3 ? View.VISIBLE : View.GONE);
                mSkipButton.setVisibility(position == 3 ? View.GONE : View.VISIBLE);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }
        });
        mNextButton.setOnClickListener(v -> mViewPager.setCurrentItem(mCurrentPageIndex += 1, true));
        mSkipButton.setOnClickListener(v -> finishOnBoarding());
        mFinishButton.setOnClickListener(v -> finishOnBoarding());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_on_boarding, menu);
        return true;
    }


    /*
     * Class Methods
     */

    /**
     * Finish the on boarding process after the user clicked the skip oder finish button. When the
     * onboarding is finished, the activity is destroyed and the {@link InterestsActivity} is started.
     */
    private void finishOnBoarding() {
        SharedPreferencesAccess.saveConstants(OnBoardingActivity.this, PREF_USER_FIRST_TIME, "false");
        startActivity(new Intent(this, InterestsActivity.class).putExtra(EXTRA_NO_INTERESTS, true));
        this.finish();
    }

    /**
     * Update the indicators at the bottom of the view, which show the current page the user is viewing.
     */
    void updateIndicators(int position) {
        for (int i = 0; i < mIndicators.length; i++) {
            mIndicators[i].setBackgroundResource(
                    i == position ? R.drawable.shape_indicator_selected : R.drawable.shape_indicator_unselected
            );
        }
    }

    /*
     * Interface Methods
     */

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}