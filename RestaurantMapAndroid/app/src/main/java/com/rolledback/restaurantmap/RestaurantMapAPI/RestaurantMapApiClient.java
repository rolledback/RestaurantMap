package com.rolledback.restaurantmap.RestaurantMapAPI;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RestaurantMapApiClient {

    private String _accessToken;
    private IRestaurantMapService _service;

    public RestaurantMapApiClient() {
        this._accessToken = null;
        Retrofit retrofit = new Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://restaurantmapapi.azurewebsites.net/")
            .build();
        this._service = retrofit.create(IRestaurantMapService.class);
    }

    public void getRestaurants(IClientResponseHandler<List<Restaurant>> handler) {
        Call listRestaurantsCall = this._service.listRestaurants();
        listRestaurantsCall.enqueue(new GenericCallback(handler));
    }

    public void login(LoginRequest request, IClientResponseHandler<LoginResult> handler) {
        Call loginCall = this._service.login(request);
        loginCall.enqueue(new GenericCallback(handler));
    }

    private class GenericCallback<T> implements Callback<T> {
        private IClientResponseHandler _handler;
        public GenericCallback(IClientResponseHandler<T> handler) {
            this._handler = handler;
        }

        @Override
        public void onResponse(Call<T> call, Response<T> response) {
            if (response.isSuccessful()) {
                _handler.onSuccess((response.body()));
            } else {
                try {
                    _handler.onFailure(response.errorBody().string());
                } catch (IOException e ) {
                    _handler.onFailure("Fatal error.");
                }
            }
        }
        @Override
        public void onFailure(Call<T> call, Throwable t) {
            _handler.onFailure(t.getLocalizedMessage());
        }
    }
}


