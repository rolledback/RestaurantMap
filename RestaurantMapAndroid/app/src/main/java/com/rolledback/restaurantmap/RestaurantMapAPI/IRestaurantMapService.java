package com.rolledback.restaurantmap.RestaurantMapAPI;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface IRestaurantMapService {

    @GET("api/restaurants")
    Call<List<Restaurant>> listRestaurants();

    @POST("api/auth/login")
    Call<AuthResult> login(@Body LoginRequest request);

    @POST("api/auth/refresh")
    Call<AuthResult> reauth(@Body ReauthRequest request);

    @POST("api/restaurants")
    Call<Void> addRestaurant(@Header ("Authorization") String token, @Body Restaurant restaurant);
}
