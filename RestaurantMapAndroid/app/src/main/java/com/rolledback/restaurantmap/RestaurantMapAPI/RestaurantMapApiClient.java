package com.rolledback.restaurantmap.RestaurantMapAPI;

import android.content.Context;

import java.io.IOException;
import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RestaurantMapApiClient {

    private IRestaurantMapService _refreshService;
    private IRestaurantMapService _nonRefreshService;
    private Context _context;

    public RestaurantMapApiClient(Context context) {
        Retrofit refreshRetrofit = new Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://restaurantmapapi.azurewebsites.net/")
            .client(new OkHttpClient.Builder().addInterceptor(new AuthInterceptor(this, context)).build())
            .build();
        this._refreshService = refreshRetrofit.create(IRestaurantMapService.class);

        Retrofit nonRefreshRetrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("https://restaurantmapapi.azurewebsites.net/")
                .build();
        this._nonRefreshService = nonRefreshRetrofit.create(IRestaurantMapService.class);

        this._context = context;
    }

    public void getRestaurants(IClientResponseHandler<List<Restaurant>> handler) {
        Call listRestaurantsCall = this._refreshService.listRestaurants();
        listRestaurantsCall.enqueue(new GenericCallback(handler));
    }

    public void login(LoginRequest request, IClientResponseHandler<AuthResult> handler) {
        Call loginCall = this._nonRefreshService.login(request);
        loginCall.enqueue(new GenericCallback(handler));
    }

    public void reauth(ReauthRequest request, IClientResponseHandler<AuthResult> handler) {
        Call reauthCall = this._nonRefreshService.reauth(request);
        reauthCall.enqueue(new GenericCallback(handler));
    }

    public AuthResult reauthSync(ReauthRequest request) throws ApiException, IOException {
        Call reauthCall = this._nonRefreshService.reauth(request);
        Response<AuthResult> response = reauthCall.execute();
        if (response.isSuccessful()) {
            return response.body();
        } else {
            throw new ApiException(response.message());
        }
    }


    public void addRestaurant(Restaurant restaurant, IClientResponseHandler<Void> handler) {
        String authToken = "Bearer " + AccountManager.getInstance().currentAuthToken(this._context);

        Call addRestaurantCall = this._refreshService.addRestaurant(authToken, restaurant);
        addRestaurantCall.enqueue(new GenericCallback(handler));
    }

    private class GenericCallback<T> implements Callback<T> {
        private IClientResponseHandler _handler;
        public GenericCallback(IClientResponseHandler<T> handler) {
            this._handler = handler;
        }

        @Override
        public void onResponse(Call<T> call, Response<T> response) {
            if (response.isSuccessful()) {
                _handler.onSuccess(response.body());
            } else {
                _handler.onFailure(response.message());
            }
        }
        @Override
        public void onFailure(Call<T> call, Throwable t) {
            _handler.onFailure(t.getLocalizedMessage());
        }
    }
}


