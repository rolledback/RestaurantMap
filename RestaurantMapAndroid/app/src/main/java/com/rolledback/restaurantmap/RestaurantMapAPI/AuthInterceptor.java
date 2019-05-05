package com.rolledback.restaurantmap.RestaurantMapAPI;

import android.accounts.Account;
import android.content.Context;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {
    private RestaurantMapApiClient apiClient;
    private AccountManager accountManager;
    private Context context;

    public AuthInterceptor(RestaurantMapApiClient apiClient, Context context) {
        this.apiClient = apiClient;
        this.accountManager = AccountManager.getInstance();
        this.context = context;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Response mainResponse = chain.proceed(chain.request());
        Request mainRequest = chain.request();

        // if response code is 401 or 403, 'mainRequest' has encountered authentication error
        if (mainResponse.code() == 401 || mainResponse.code() == 403) {
            // request to refresh API to get fresh token synchronously calling refres API
            try {
                AuthResult result = this.accountManager.reauthSync(this.context, this.apiClient);
                Request.Builder builder = mainRequest.newBuilder().header("Authorization", "Bearer " + result.accessToken).
                        method(mainRequest.method(), mainRequest.body());
                mainResponse = chain.proceed(builder.build());
            } catch (ApiException exception) {
                // shrug?
            }
        }

        return mainResponse;
    }
}