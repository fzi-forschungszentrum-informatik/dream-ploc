package de.fzi.dream.ploc.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;

import de.fzi.dream.ploc.R;
import de.fzi.dream.ploc.data.structure.entity.ExpertProfile;
import de.fzi.dream.ploc.databinding.FragmentProfileBinding;
import de.fzi.dream.ploc.ui.activity.ProfileActivity;
import de.fzi.dream.ploc.utility.SharedPreferencesAccess;
import de.fzi.dream.ploc.viewmodel.ProfileViewModel;

import static de.fzi.dream.ploc.utility.Constants.PREF_USER_FIRST_TIME;

/**
 * The ProfileFragment is embedded in the {@link ProfileActivity} and shows information about the
 * current user profile and gives the option to delete this user profile.
 *
 * @author Felix Melcher
 */
public class ProfileFragment extends Fragment {

    /**
     * Public class identifier tag
     */
    public static final String TAG = ProfileFragment.class.getSimpleName();

    // Context
    private Context mContext;

    // Binding
    private FragmentProfileBinding mBinding;

    // ViewModel
    private ProfileViewModel mViewModel;

    // Activity Interface
    private OnInteractionListener mListener;

    /**
     * Fragments require an empty public constructor.
     */
    public ProfileFragment() {
    }

    /**
     * Create a new instance of the ProfileFragment.
     *
     * @return ProfileFragment
     */
    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    /*
    Lifecycle Methods
    */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
        mListener = (OnInteractionListener) context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false);
        View view = mBinding.getRoot();
        Button mButtonDelete = view.findViewById(R.id.button_delete_profile_fragment);
        mButtonDelete.setOnClickListener(v -> {
            Snackbar.make(v, "YOUR ACCOUNT WILL BE DELETED", Snackbar.LENGTH_LONG).show();
            mViewModel.deleteProfile();
            SharedPreferencesAccess.saveConstants(mContext, PREF_USER_FIRST_TIME, "true");
        });
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewModel = obtainViewModel();
        mViewModel.readUserProfile().observe(getViewLifecycleOwner(), user -> {
            if (user == null) {
                mListener.onDeleteProfileClicked();
            }
        });
        observeExpertProfile();
        observeUserProfile();
    }

    /*
    ViewModel Methods
    */
    private ProfileViewModel obtainViewModel() {
        return new ViewModelProvider(this).get(ProfileViewModel.class);
    }

    /**
     * Observe changes to the user related expert profile and update the data binding of the view
     * after a change occurs.
     */
    private void observeExpertProfile() {
        mViewModel.readExpertProfile().observe(getViewLifecycleOwner(), expertProfile -> {
            if (expertProfile != null) {
                mBinding.setExpertProfile(expertProfile);
            } else {
                mBinding.setExpertProfile(new ExpertProfile("None"));
            }
        });
    }

    /**
     * Observe changes to the user related expert profile and update the data binding of the view
     * after a change occurs.
     */
    private void observeUserProfile() {
        mViewModel.readUserProfile().observe(getViewLifecycleOwner(), userProfile -> {
            if (userProfile != null) {
                mBinding.setUserProfile(userProfile);
            }
        });
    }

    /**
     * Fragment Interaction Interface.
     */
    public interface OnInteractionListener {
        void onDeleteProfileClicked();
    }
}
