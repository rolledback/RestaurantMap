package com.rolledback.restaurantmap;

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

    RestaurantMapApiClient() {
        this._accessToken = null;
        Retrofit retrofit = new Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://restaurantmapapi.azurewebsites.net/")
            .build();
        this._service = retrofit.create(IRestaurantMapService.class);
    }

    public void getRestaurants(IClientResponseHandler<List<Restaurant>> handler) {
        Call listRestaurantsCall = this._service.listRestaurants();
        listRestaurantsCall.enqueue(new Callback<List<Restaurant>>() {
            @Override
            public void onResponse(Call<List<Restaurant>> call, Response<List<Restaurant>> response) {
                if (response.isSuccessful()) {
                    handler.onSuccess(response.body());
                } else {
                    try {
                        handler.onFailure(response.errorBody().string());
                    } catch (IOException e ) {
                        handler.onFailure("Fatal error.");
                    }
                }
            }
            @Override
            public void onFailure(Call<List<Restaurant>> call, Throwable t) {
                handler.onFailure(t.getLocalizedMessage());
            }
        });
    }
}


