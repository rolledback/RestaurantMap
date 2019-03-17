package com.rolledback.restaurantmap.RestaurantMapAPI;

import android.content.Context;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RestaurantMapApiClient {

    private IRestaurantMapService _service;
    private Context _context;

    public RestaurantMapApiClient(Context context) {
        Retrofit retrofit = new Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://restaurantmapapi.azurewebsites.net/")
            .build();
        this._context = context;
        this._service = retrofit.create(IRestaurantMapService.class);
    }

    public void getRestaurants(IClientResponseHandler<List<Restaurant>> handler) {
        Call listRestaurantsCall = this._service.listRestaurants();
        listRestaurantsCall.enqueue(new GenericCallback(handler));
    }

    public void login(LoginRequest request, IClientResponseHandler<Account> handler) {
        Call loginCall = this._service.login(request);
        loginCall.enqueue(new GenericCallback(handler));
    }

    public void addRestaurant(Restaurant restaurant, IClientResponseHandler<Void> handler) {
        String authToken ="Bearaer " + AccountManager.getInstance().currentUser(this._context).token;

        Call addRestaurantCall = this._service.addRestaurant(authToken, restaurant);
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
                _handler.onSuccess((response.body()));
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


