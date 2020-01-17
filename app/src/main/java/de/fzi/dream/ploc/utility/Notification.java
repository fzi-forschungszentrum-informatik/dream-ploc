package de.fzi.dream.ploc.utility;

/**
 * General notification class to be observed by lifecycle owners for delivering messages from
 * the ViewModel to the owner.
 *
 * @author Felix Melcher
 */
public class Notification {
    private String message;
    private boolean hasReloadAction;
    private boolean hasUndoAction;
    private boolean isDislike;

    public Notification(String message, boolean hasReloadAction, boolean hasUndoAction, boolean isDislike) {
        this.message = message;
        this.hasReloadAction = hasReloadAction;
        this.hasUndoAction = hasUndoAction;
        this.isDislike = isDislike;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean hasReloadAction() {
        return hasReloadAction;
    }

    public boolean isDislike(){
        return isDislike;
    }

    public boolean hasUndoAction() {
        return hasUndoAction;
    }
}
