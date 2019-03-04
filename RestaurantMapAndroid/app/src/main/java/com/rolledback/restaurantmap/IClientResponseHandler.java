package com.rolledback.restaurantmap;

public interface IClientResponseHandler<ResponseT> {
    void onSuccess(ResponseT response);
    void onFailure(String error);
}
