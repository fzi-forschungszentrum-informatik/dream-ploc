package de.fzi.dream.ploc.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.textfield.TextInputLayout;

import de.fzi.dream.ploc.R;

/**
 * The AddCollectionDialog provides a DialogFragment for adding new collections to the user profile.
 *
 * @author Felix Melcher
 */
public class AddCollectionDialog extends DialogFragment {

    /**
     * Public class identifier tag
     */
    public static final String TAG = AddCollectionDialog.class.getSimpleName();

    private OnInteractionListener mListener;

    public AddCollectionDialog(OnInteractionListener listener){
        mListener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // View
        View v = View.inflate(getActivity(), R.layout.dialog_add_edit_collection, null);
        Context context = v.getContext();
        EditText collectionName = v.findViewById(R.id.edit_text_collection_name_add_edit_dialog);
        TextInputLayout til = v.findViewById(R.id.input_layout_add_edit_dialog);
        collectionName.requestFocus();

        // Build Dialog
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setView(v)
                .setTitle(R.string.dialog_text_add_collection_title)
                .setPositiveButton(R.string.dialog_text_add_collection_positive,
                        (d, id) -> mListener.onFinishAddCollectionDialog(collectionName.getText().toString()))
                .setNegativeButton(R.string.dialog_text_add_edit_collection_negative, (d, id) -> d.dismiss())
                .create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        }

        // Register Listener
        collectionName.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s)) {
                    til.setError(context.getString(R.string.dialog_text_add_edit_collection_error));
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                } else {
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                }
            }
        });
        dialog.setOnShowListener(d -> ((AlertDialog) d).getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false));
        return dialog;
    }

    public interface OnInteractionListener {
        /**
         * Triggered when a new collection is added after the dialog
         * is closed via the positive button and passes the new collection name
         * to the listener.
         *
         * @param collectionName The new collections name.
         */
        void onFinishAddCollectionDialog(String collectionName);
    }
}
