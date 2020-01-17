package de.fzi.dream.ploc.ui.activity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import de.fzi.dream.ploc.R;
import de.fzi.dream.ploc.ui.fragment.FeedbackPDFViewFragment;
import de.fzi.dream.ploc.ui.fragment.FeedbackRatingFragment;

import static de.fzi.dream.ploc.utility.Constants.EXTRA_ID;

public class FeedbackActivity extends AppCompatActivity
        implements FeedbackPDFViewFragment.OnInteractionListener {

    public static final String FRAGMENT_PDF_RENDERER_BASIC = "pdf_renderer_basic";
    private int recordID;
    private boolean onRating = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_view);
        recordID = getIntent().getIntExtra(EXTRA_ID, 0);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.frame_pdf_view_activity, FeedbackPDFViewFragment.newInstance(recordID),
                            FRAGMENT_PDF_RENDERER_BASIC)
                    .commit();
        }
        setSupportActionBar(findViewById(R.id.pdf_toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.activity_feedback_pdf_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                if (onRating) {
                    onOpenFeedbackPDF();
                } else {
                    onBackPressed();
                }
                return true;
            }
            case R.id.action_next_step: {
                onOpenFeedbackRating();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onOpenFeedbackRating() {
        setTitle("Give Feedback");
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_pdf_view_activity, FeedbackRatingFragment.newInstance(recordID), FeedbackRatingFragment.TAG).commit();
        onRating = true;
    }

    @Override
    public void onOpenFeedbackPDF() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_pdf_view_activity, new FeedbackPDFViewFragment(),
                        FRAGMENT_PDF_RENDERER_BASIC)
                .commit();
        onRating = false;
    }
}
