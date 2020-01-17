package de.fzi.dream.ploc.data.structure;

public class Option {
    private String mIdentifier;
    private String mPreview;
    private boolean mAvailable = false;

    public Option(String identifier, String preview, boolean available) {
        mIdentifier = identifier;
        mPreview = preview;
        mAvailable = available;
    }

    public String getIdentifier() {
        return mIdentifier;
    }

    public String getPreview() {
        return mPreview;
    }

    public boolean isAvailable() {
        return mAvailable;
    }
}
