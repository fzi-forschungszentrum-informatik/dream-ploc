package de.fzi.dream.ploc.ui.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;

import de.fzi.dream.ploc.R;
import de.fzi.dream.ploc.ui.activity.FeedbackActivity;
import de.fzi.dream.ploc.viewmodel.FeedbackPreviewViewModel;

/**
 * The FeedbackSignInFragment is embedded in the {@link FeedbackActivity} and displays a simple
 * login page for mocking up a way to sign up the user to a research authentication page.
 *
 * @author Felix Melcher
 */
public class FeedbackSignInFragment extends Fragment {
    /**
     * Public class identifier tag
     */
    public static final String TAG = FeedbackSignInFragment.class.getSimpleName();

    private Context mContext;

    private EditText emailText;
    private EditText passwordText;
    private Button loginButton;
    private TextView signUpLink;

    private FeedbackPreviewViewModel mViewModel;

    private OnInteractionListener mListener;

    /**
     * Fragments require an empty public constructor.
     */
    public FeedbackSignInFragment() {
    }

    /**
     * Create a new instance of the FeedbackSignInFragment.
     *
     * @return FeedbackSignInFragment
     */
    public static FeedbackSignInFragment newInstance() {
        return new FeedbackSignInFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_sign_in, container, false);
        loginButton = rootView.findViewById(R.id.btn_login);
        signUpLink = rootView.findViewById(R.id.text_view_sign_up_sign_in_fragment);
        emailText = rootView.findViewById(R.id.input_email);
        passwordText = rootView.findViewById(R.id.input_password);
        mViewModel = obtainViewModel();
        loginButton.setOnClickListener(v -> login());
        signUpLink.setOnClickListener(v -> {
            // Redirect to the sign up page of the research authorization platform.
        });
        return rootView;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
        if (context instanceof OnInteractionListener) {
            mListener = (OnInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        super.onCreateOptionsMenu(menu, inflater);
    }

    /*
     ViewModel Methods
    */

    /**
     * Obtain the ViewModel instance responsible for getting the data from the remote or local
     * data source.
     *
     * @return FeedbackPreviewViewModel
     */
    private FeedbackPreviewViewModel obtainViewModel() {
        return new ViewModelProvider(this).get(FeedbackPreviewViewModel.class);
    }


    /*
        Fragment specific helper methods
    */

    /**
     * Start the login routine after the user clicked the login button.
     */
    private void login() {
        if (!validate()) {
            onLoginFailed();
            return;
        }
        loginButton.setEnabled(false);
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getResources().getString(R.string.progress_message_login));
        progressDialog.show();

        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();

        // TODO: Here comes the login logic.
        new android.os.Handler().postDelayed(() -> {
            onLoginSuccess();
            // onLoginFailed();
            progressDialog.dismiss();
        }, 3000);
    }

    /**
     * Process the successful login, this contains creating a new expert profile on the backend and
     * opening the {@link FeedbackPreviewFragment}.
     */
    private void onLoginSuccess() {
        loginButton.setEnabled(true);
        mViewModel.setExpertProfile(emailText.getText().toString());
        mListener.onLoginSuccessful();
    }

    /**
     * If the login failed, show an error message and reactivate the login button.
     */
    private void onLoginFailed() {
        showSnackbar(getResources().getString(R.string.snackbar_message_login_failed));
        loginButton.setEnabled(true);
    }

    /**
     * Validate if the username and password match our criteria. If not display the error message
     * on the affected TextViews.
     */
    private boolean validate() {
        boolean valid = true;
        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();
        if (email.isEmpty()) {
            emailText.setError(getResources().getString(R.string.layout_error_login_username));
            valid = false;
        } else if (email.contains("@") && !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailText.setError(getResources().getString(R.string.layout_error_login_username));
            valid = false;
        } else if (!email.contains("@") && email.length() < 19) {
            emailText.setError(getResources().getString(R.string.layout_error_login_username));
            valid = false;
        } else {
            emailText.setError(null);
        }
        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            passwordText.setError(getResources().getString(R.string.layout_error_login_password));
            valid = false;
        } else {
            passwordText.setError(null);
        }
        return valid;
    }

    /**
     * Show a snackbar within the current fragment.
     *
     * @param message The notification message to display in the snackbar.
     */
    private void showSnackbar(String message) {
        Snackbar.make(((Activity) mContext).findViewById(R.id.coordinator_main_activity), message, Snackbar.LENGTH_LONG).show();
    }

    /**
     * FeedbackSignIn Interaction Interface.
     */
    public interface OnInteractionListener {
        void onLoginSuccessful();
    }
}
