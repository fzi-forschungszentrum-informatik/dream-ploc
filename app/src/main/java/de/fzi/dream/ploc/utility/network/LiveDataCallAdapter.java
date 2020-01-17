package de.fzi.dream.ploc.utility.network;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import java.lang.reflect.Type;

import de.fzi.dream.ploc.data.remote.response.GozerResponse;
import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Callback;
import retrofit2.Response;

public class LiveDataCallAdapter<R> implements CallAdapter<R, LiveData<GozerResponse<R>>> {

    private Type responseType;

    LiveDataCallAdapter(Type responseType) {
        this.responseType = responseType;
    }

    @NonNull
    @Override
    public Type responseType() {
        return responseType;
    }

    @NonNull
    @Override
    public LiveData<GozerResponse<R>> adapt(@NonNull final Call<R> call) {
        return new LiveData<GozerResponse<R>>() {
            @Override
            protected void onActive() {
                super.onActive();
                final GozerResponse apiResponse = new GozerResponse();
                if (!call.isExecuted()) {
                    call.enqueue(new Callback<R>() {
                        @Override
                        public void onResponse(@NonNull Call<R> call, @NonNull Response<R> response) {
                            postValue(apiResponse.create(response));
                        }

                        @Override
                        public void onFailure(@NonNull Call<R> call, @NonNull Throwable t) {
                            postValue(apiResponse.create(t));
                        }
                    });
                }

            }
        };
    }

}
