package de.fzi.dream.ploc.ui.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import de.fzi.dream.ploc.ui.activity.OnBoardingActivity;
import de.fzi.dream.ploc.ui.fragment.OnBoardingFragment;

/**
 * The OnBoardingPagingAdapter provides a simple pager for displaying multiple
 * {@link OnBoardingFragment}s within the {@link OnBoardingActivity}.
 *
 * @author Felix Melcher
 */
public class OnBoardingPagingAdapter extends FragmentStatePagerAdapter {

    /**
     * Public class identifier tag
     */
    public static final String TAG = OnBoardingPagingAdapter.class.getSimpleName();

    /**
     * Constructor for the OnBoardingPagingAdapter {@link FragmentStatePagerAdapter} that
     * sets the fragment manager for the adapter.
     *
     * @param fm fragment manager that will interact with this adapter.
     */
    public OnBoardingPagingAdapter(FragmentManager fm) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    }

    @Override
    public @NonNull
    Fragment getItem(int position) {
        return OnBoardingFragment.newInstance(position + 1);
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Define interests";
            case 1:
                return "Bookmark publications";
            case 2:
                return "Find experts";
            case 3:
                return "Bookmark experts";
        }
        return null;
    }
}
