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
import de.fzi.dream.ploc.data.structure.entity.Collection;

/**
 * The EditCollectionDialog provides a DialogFragment for editing the collection name.
 *
 * @author Felix Melcher
 */
public class EditCollectionDialog extends DialogFragment {

    /**
     * Public class identifier tag
     */
    public static final String TAG = EditCollectionDialog.class.getSimpleName();

    private Collection mCollection;
    private OnInteractionListener mListener;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // View
        View v = View.inflate(getActivity(), R.layout.dialog_add_edit_collection, null);
        Context context = v.getContext();
        EditText collectionName = v.findViewById(R.id.edit_text_collection_name_add_edit_dialog);
        TextInputLayout til = v.findViewById(R.id.input_layout_add_edit_dialog);
        collectionName.setText(mCollection.getName());
        collectionName.requestFocus();

        // Build Dialog Builder
        AlertDialog dialog = new AlertDialog.Builder(context, R.style.AlertDialogTheme)
                .setView(v)
                .setTitle(R.string.dialog_text_edit_collection_title)
                .setPositiveButton(R.string.dialog_text_edit_collection_positive, (d, id) -> {
                    mListener.onFinishEditCollectionDialog(
                            new Collection(mCollection.getId(), collectionName.getText().toString()));
                    d.dismiss();
                })
                .setNegativeButton(R.string.dialog_text_add_edit_collection_negative,
                        (d, id) -> d.dismiss())
                .setNeutralButton(R.string.dialog_text_edit_collection_neutral,
                        (d, id) -> mListener.onDeleteCollection(mCollection)).create();

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
        if (dialog.getWindow() != null) {
            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        }
        return dialog;
    }

    public void setCollection(Collection collection) {
        mCollection = collection;
    }

    public void setListener(OnInteractionListener l) {
        mListener = l;
    }

    public interface OnInteractionListener {
        /**
         * Triggered when a collection is edited after the dialog
         * is closed via the positive button and passes the updated collection
         * to the listener.
         *
         * @param collection The updated collection.
         */
        void onFinishEditCollectionDialog(Collection collection);

        /**
         * Triggered when a collection is edited after the dialog
         * is closed via the neutral button and passes the collection that will be
         * deleted to the listener.
         *
         * @param collection The collection to be deleted.
         */
        void onDeleteCollection(Collection collection);
    }
}
