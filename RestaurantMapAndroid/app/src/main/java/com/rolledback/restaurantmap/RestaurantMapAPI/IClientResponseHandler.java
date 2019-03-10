package com.rolledback.restaurantmap.RestaurantMapAPI;

public interface IClientResponseHandler<ResponseT> {
    void onSuccess(ResponseT response);
    void onFailure(String error);
}
