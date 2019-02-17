package com.rolledback.restaurantmap;

public interface IClientResponseHandler<ResponseT> {
    public void onSuccess(ResponseT response);
    public void onFailure(String error);
}
