package de.fzi.dream.ploc.data.remote.connection;

import android.text.TextUtils;

import java.util.concurrent.TimeUnit;

import de.fzi.dream.ploc.utility.network.AuthenticationInterceptor;
import de.fzi.dream.ploc.utility.network.LiveDataCallAdapterFactory;
import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static de.fzi.dream.ploc.utility.Constants.SERVER_ADDRESS;
/**
 * The GozerClient class defines the HTTP client to communicate with the gozer backend. It manages
 * the authentication, timeouts, logging and the target URL.
 *
 * @author Felix Melcher
 */
public class GozerClient {

    /** Public class identifier tag for logging */
    public static final String TAG = AuthenticationInterceptor.class.getSimpleName();

    /** Interface service class */
    private static GozerApi mApiService;

    /** The OkHttpClient with defined timeouts and logging */
    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder()
            .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .connectTimeout(1, TimeUnit.MINUTES)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS);

    /** The retrofit client builder with defined URL and GSON converter factory */
    private static Retrofit.Builder mBuilder = new Retrofit.Builder()
                    .baseUrl(SERVER_ADDRESS)
                    .addCallAdapterFactory(new LiveDataCallAdapterFactory())
                    .addConverterFactory(GsonConverterFactory.create());

    /** The retrofit client */
    private static Retrofit mClient = mBuilder.build();

    /**
     * The createService method takes the username and password to create the authentication
     * token for the initiation of the client.
     *
     * @param username username to authenticate with.
     * @param password password to authorize the connection of the given username.
     * @return <S>
     */
    public static <S> S createService(String username, String password) {
        if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)) {
            return createClient((Class<S>) GozerApi.class, Credentials.basic(username, password));
        }
        return createClient((Class<S>) GozerApi.class, null);
    }

    /**
     * The createClient method takes the created service api and authentication token to
     * initiate the client.
     *
     * @param serviceClass application programming interface
     * @param authToken token to authenticate the user with basic auth.
     * @return <S>
     */
    private static <S> S createClient(Class<S> serviceClass, final String authToken) {
        if (!TextUtils.isEmpty(authToken)) {
            AuthenticationInterceptor interceptor = new AuthenticationInterceptor(authToken);
            if (!httpClient.interceptors().contains(interceptor)) {
                httpClient.addInterceptor(interceptor);
                mBuilder.client(httpClient.build());
                mClient = mBuilder.build();
            }
        }
        mApiService = (GozerApi) mClient.create(serviceClass);
        return mClient.create(serviceClass);
    }

    /**
     * The createNonAuthenticatedClient establishes a connection to the backend without any
     * authentication, this is needed for the first handshake when the user is created.
     *
     * @return GozerApi
     */
    public static GozerApi createNonAuthenticatedClient() {
        return mBuilder.client(httpClient.build()).build().create(GozerApi.class);
    }

    /**
     * The getAuthenticatedClient method returns the created interface with
     * the authenticated client. If the client is not created this method returns null.
     *
     * @return GozerApi
     */
    public static GozerApi getAuthenticatedClient(){
        return mApiService;
    }
}
