package com.rolledback.restaurantmap.RestaurantMapAPI;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface IRestaurantMapService {

    @GET("api/restaurants")
    Call<List<Restaurant>> listRestaurants();

    @POST("api/login")
    Call<Account> login(@Body LoginRequest request);
}
