package de.fzi.dream.ploc.utility.network;

public class NetworkState {
    public static final NetworkState LOADED;
    public static final NetworkState LOADING;
    public static final NetworkState ZERO;

    static {
        LOADED = new NetworkState(Status.SUCCESS, "Success");
        LOADING = new NetworkState(Status.RUNNING, "Running");
        ZERO = new NetworkState(Status.EMPTY, "Empty");
    }

    private final Status mStatus;
    private final String mMessage;

    public NetworkState(Status status, String message) {
        mStatus = status;
        mMessage = message;
    }

    public Status getStatus() {
        return mStatus;
    }

    public String getMsg() {
        return mMessage;
    }

    public enum Status {
        RUNNING,
        SUCCESS,
        EMPTY,
        FAILED
    }
}