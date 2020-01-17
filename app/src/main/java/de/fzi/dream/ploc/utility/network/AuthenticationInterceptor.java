package de.fzi.dream.ploc.utility.network;

import androidx.annotation.NonNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * The Authentication Interceptor is created during the creation of the API service, and adds the
 * authentication token to the http header for basic authentication.
 *
 * @author Felix Melcher
 */
public class AuthenticationInterceptor implements Interceptor {

    /**
     * Public class identifier tag for logging
     */
    public static final String TAG = AuthenticationInterceptor.class.getSimpleName();

    private String mToken;

    /**
     * Constructor
     *
     * @param token http basic auth token
     */
    public AuthenticationInterceptor(String token) {
        this.mToken = token;
    }

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        return chain.proceed(chain.request().newBuilder()
                .header("Authorization", mToken).build());
    }
}