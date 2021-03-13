package com.dazzlerr_usa.utilities.retrofit;

import org.jetbrains.annotations.NotNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

/**
 * Created by Arvind on 25/10/19.
 */

public abstract class RetryableCallback<T> implements Callback<T> {

    private int totalRetries = 3;
    private static final String TAG = RetryableCallback.class.getSimpleName();
    private final Call<T> call;
    private int retryCount = 0;

    public RetryableCallback(Call<T> call, int totalRetries) {
        this.call = call;
        this.totalRetries = totalRetries;
    }

    @Override
    public void onResponse(@NotNull Call<T> call, @NotNull Response<T> response) {
        if (!APIHelper.isCallSuccess(response))
            if (retryCount++ < totalRetries) {
                Timber.e(TAG+" "+ "Retrying API Call -  (" + retryCount + " / " + totalRetries + ")");
                retry();
            } else
                onFinalResponse(call, response);
        else
            onFinalResponse(call,response);
    }

    @Override
    public void onFailure(@NotNull Call<T> call, Throwable t) {
        Timber.e(TAG+" "+ t.getMessage());
        if (retryCount++ < totalRetries) {
            Timber.e(TAG+" Failure "+ "Retrying API Call -  (" + retryCount + " / " + totalRetries + ")");
            retry();
        } else
            onFinalFailure(call, t);
    }

    public void onFinalResponse(Call<T> call, Response<T> response) {

    }

    public void onFinalFailure(Call<T> call, Throwable t) {
    }

    private void retry() {
        call.clone().enqueue(this);
    }
}