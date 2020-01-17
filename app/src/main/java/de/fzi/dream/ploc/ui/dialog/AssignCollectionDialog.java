package de.fzi.dream.ploc.ui.dialog;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import de.fzi.dream.ploc.R;
import de.fzi.dream.ploc.data.structure.entity.Collection;
import de.fzi.dream.ploc.data.structure.entity.RecordBookmark;

/**
 * The AssignCollectionDialog provides a DialogFragment for assigning bookmarks to an set of
 * user defined collections. This Dialog is displayed after a set of bookmarks was selected.
 *
 * @author Felix Melcher
 */
public class AssignCollectionDialog extends DialogFragment {

    private List<Collection> mCollections;
    private Set<Integer> mCollectionIDs = new HashSet<>();
    private OnInteractionListener mListener;
    private ArrayList<RecordBookmark> mBookmarks = new ArrayList<>();

    /**
     * Constructor, init a dialog with a set of selectable collections.
     *
     * @param c The set of collections.
     */
    public AssignCollectionDialog(List<Collection> c, OnInteractionListener listener) {
        mCollections = c;
        mListener = listener;
    }

    /**
     * Pass the selected items to the dialog to preselect the corresponding collections in the
     * dialog.
     *
     * @param selectedItems The set of selected record bookmarks.
     */
    public void setDialogData(ArrayList<RecordBookmark> selectedItems) {
        mBookmarks.clear();
        mCollectionIDs.clear();
        for (RecordBookmark item : selectedItems) {
            if (item.getCollectionIDs() != null) mCollectionIDs.addAll(item.getCollectionIDs());
        }
        mBookmarks.addAll(selectedItems);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        // If there are collections build the select boxes, else show the error dialog.
        if (mCollections != null && mCollections.size() > 0) {
            builder.setTitle(R.string.dialog_text_assign_collections_title);
            // Add the negative button for closing the dialog.
            builder.setNegativeButton(R.string.dialog_text_assign_collections_negative, (dialog, which) -> dialog.dismiss());
            final boolean[] checkedCollections = new boolean[mCollections.size()];
            // If there are already collections assigned to bookmarks, preselect the corresponding
            // collections.
            if (mCollectionIDs != null && mCollectionIDs.size() > 0) {
                for (Collection collection : mCollections) {
                    for (Integer id : mCollectionIDs) {
                        if (id == collection.getId()) {
                            checkedCollections[mCollections.indexOf(collection)] = true;
                        }
                    }
                }
            }
            // Add all collections as CheckBoxes
            builder.setMultiChoiceItems(mCollections.stream().map(Collection::getName)
                    .toArray(String[]::new), checkedCollections, (dialog, which, isChecked) -> checkedCollections[which] = isChecked);
            // Add the positive button an d register the listener
            builder.setPositiveButton(R.string.button_ok, (dialog, id) -> {
                dialog.dismiss();
                List<Integer> selectedCollectionIDs = new ArrayList<>();
                for (int i = 0; i < checkedCollections.length; i++) {
                    if (checkedCollections[i]) {
                        selectedCollectionIDs.add(mCollections.get(i).getId());
                    }
                }
                mListener.onCollectionsAssigned(mBookmarks.stream().map(RecordBookmark::getId).collect(Collectors.toList()), selectedCollectionIDs);
            });
        } else {
            builder.setTitle(R.string.dialog_text_assign_collections_error_title);
            builder.setMessage(R.string.dialog_text_no_collections)
                    .setPositiveButton(R.string.button_ok, (dialog, id) -> dialog.dismiss());
        }
        return builder.create();
    }

    /**
     * Add the listener to the dialog to pass through the interactions within the dialog.
     *
     * @param l The listener to be fired on dialog interactions.
     */
    public void setListener(OnInteractionListener l) {
        this.mListener = l;
    }

    public interface OnInteractionListener {
        void onCollectionsAssigned(List<Integer> bookmark, List<Integer> collectionIDs);
    }

}
