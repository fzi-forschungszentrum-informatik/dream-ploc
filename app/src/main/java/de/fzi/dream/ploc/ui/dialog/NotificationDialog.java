package de.fzi.dream.ploc.ui.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import de.fzi.dream.ploc.R;

/**
 * The NotificationDialog provides a DialogFragment for displaying an error message when a paper opened
 * for feedback does not provide a PDF link.
 *
 * @author Felix Melcher
 */
public class NotificationDialog extends DialogFragment {

    /**
     * Public class identifier tag
     */
    public static final String TAG = NotificationDialog.class.getSimpleName();

    private String mText;
    private String mTitle;

    /**
     * Constructor
     *
     * @param title The title of the dialog.
     * @param text  The text displayed below the title.
     */
    public NotificationDialog(String title, String text) {
        mText = text;
        mTitle = title;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View v = View.inflate(getActivity(), R.layout.dialog_no_results, null);
        ((TextView) v.findViewById(R.id.text_view_dialogs)).setText(mText);
        return new AlertDialog.Builder(v.getContext())
                .setView(v)
                .setTitle(mTitle)
                .setIcon(R.drawable.ic_error_black_24dp)
                .setPositiveButton("OK", (dialog, id) -> dialog.dismiss())
                .create();
    }
}
