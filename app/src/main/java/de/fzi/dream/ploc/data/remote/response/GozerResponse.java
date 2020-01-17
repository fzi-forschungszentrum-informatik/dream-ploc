package de.fzi.dream.ploc.data.remote.response;

import java.io.IOException;

import retrofit2.Response;


/**
 * Generic class for handling responses from Retrofit
 * @param <T>
 */
public class GozerResponse<T> {

    public GozerResponse<T> create(Throwable error){
        return new ApiErrorResponse<>(error.getMessage().equals("") ? error.getMessage() : "Unknown error\nCheck network connection");
    }

    public GozerResponse<T> create(Response<T> response){
        if(response.isSuccessful()){
            T body = response.body();
//            if(body instanceof InterestResponse){
//                if(((InterestResponse) body).getRecordCount() == 0){
//                    String errorMsg = "No records found for your interests definition";
//                   //return new ApiErrorResponse<>(QUERY_EXHAUSTED);
//                    return new ApiErrorResponse<>(errorMsg);
//                }
//            }

            if(body == null || response.code() == 204){ // 204 is empty response
                return new ApiEmptyResponse<>();
            }
            else{
                return new ApiSuccessResponse<>(body);
            }
        }
        else{
            String errorMsg;
            try {
                errorMsg = response.errorBody().string();
            } catch (IOException e) {
                e.printStackTrace();
                errorMsg = response.message();
            }
            return new ApiErrorResponse<>(errorMsg);
        }
    }

    /**
     * Generic success response from api
     * @param <T>
     */
    public class ApiSuccessResponse<T> extends GozerResponse<T> {

        private T body;

        ApiSuccessResponse(T body) {
            this.body = body;
        }

        public T getBody() {
            return body;
        }

    }

    /**
     * Generic Error response from API
     * @param <T>
     */
    public class ApiErrorResponse<T> extends GozerResponse<T> {

        private String errorMessage;

        ApiErrorResponse(String errorMessage) {
            this.errorMessage = errorMessage;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

    }

    /**
     * separate class for HTTP 204 responses so that we can make ApiSuccessResponse's body non-null.
     */
    public class ApiEmptyResponse<T> extends GozerResponse<T> { }

}


