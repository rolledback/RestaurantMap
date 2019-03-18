package com.rolledback.restaurantmap.RestaurantMapAPI;

public class ReauthRequest {
    public String username;
    public String accessToken;
    public String refreshToken;

    public ReauthRequest(String username, String accessToken, String refreshToken) {
        this.username = username;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
