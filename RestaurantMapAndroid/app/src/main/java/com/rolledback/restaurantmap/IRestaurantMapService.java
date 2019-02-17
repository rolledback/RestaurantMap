package com.rolledback.restaurantmap;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface IRestaurantMapService {

    @GET("api/restaurants")
    Call<List<Restaurant>> listRestaurants();
}
