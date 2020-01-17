package de.fzi.dream.ploc.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import de.fzi.dream.ploc.R;
import de.fzi.dream.ploc.ui.activity.OnBoardingActivity;

import static de.fzi.dream.ploc.utility.Constants.EXTRA_ONBOARDING_SECTION;

/**
 * The OnBoardingFragment is embedded in the {@link OnBoardingActivity} and is a reused for each
 * page in the on boarding tutorial depending on the section number passed through the intent
 * extra.
 *
 * @author Felix Melcher
 */
public class OnBoardingFragment extends Fragment {
    /**
     * Public class identifier tag
     */
    public static final String TAG = OnBoardingFragment.class.getSimpleName();

    // View
    private int[] background_icons = new int[]{
            R.drawable.graphic_onboarding_slide_1_define_interests,
            R.drawable.graphic_onboarding_slide_2_publication_feed,
            R.drawable.graphic_onboarding_slide_3_expert_feed,
            R.drawable.graphic_onboarding_slide_4_find_experts};

    /**
     * Fragments require an empty public constructor.
     */
    public OnBoardingFragment() {
    }

    /**
     * Create a new instance of the OnBoardingFragment.
     *
     * @return OnBoardingFragment
     */
    public static OnBoardingFragment newInstance(int sectionNumber) {
        OnBoardingFragment fragment = new OnBoardingFragment();
        Bundle args = new Bundle();
        args.putInt(EXTRA_ONBOARDING_SECTION, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    /*
    Lifecycle Methods
    */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_on_boarding, container, false);
        if (getArguments() != null) {
            int position = getArguments().getInt(EXTRA_ONBOARDING_SECTION);
            TextView textView = rootView.findViewById(R.id.text_view_headline_on_boarding_fragment);
            TextView sectionText = rootView.findViewById(R.id.text_view_underline_on_boarding_fragment);
            if (position == 1) {
                textView.setText(getResources().getString(R.string.on_boarding_section_one_title));
                sectionText.setText(getResources().getString(R.string.on_boarding_section_one_text));
            } else if (position == 2) {
                textView.setText(getResources().getString(R.string.on_boarding_section_two_title));
                sectionText.setText(getResources().getString(R.string.on_boarding_section_two_text));
            } else if (position == 3) {
                textView.setText(getResources().getString(R.string.on_boarding_section_three_title));
                sectionText.setText(getResources().getString(R.string.on_boarding_section_three_text));
            } else if (position == 4) {
                textView.setText(getResources().getString(R.string.on_boarding_section_four_title));
                sectionText.setText(getResources().getString(R.string.on_boarding_section_four_text));
            }
            ImageView mImage = rootView.findViewById(R.id.image_view_on_boarding_fragment);
            mImage.setImageResource(background_icons[getArguments().getInt(EXTRA_ONBOARDING_SECTION) - 1]);
        }
        return rootView;
    }
}

