package com.rolledback.restaurantmap.Lib;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.rolledback.restaurantmap.Filters.Models.IViewableFilter;
import com.rolledback.restaurantmap.RestaurantMapAPI.Location;
import com.rolledback.restaurantmap.RestaurantMapAPI.Restaurant;

import java.util.LinkedHashMap;

public class RestaurantMarker {

    public static float getGenreMarkerColor(Restaurant restaurant) {
        switch (restaurant.rating) {
            case "Ok":
                return BitmapDescriptorFactory.HUE_ORANGE;
            case "Good":
                return BitmapDescriptorFactory.HUE_AZURE;
            case "Better":
                return BitmapDescriptorFactory.HUE_YELLOW;
            case "Best":
                return BitmapDescriptorFactory.HUE_GREEN;
            case "Meh":
                return BitmapDescriptorFactory.HUE_RED;
            case "Want to Go":
                return BitmapDescriptorFactory.HUE_VIOLET;
            default:
                return BitmapDescriptorFactory.HUE_RED;
        }
    }

    private Marker _marker;
    private Restaurant _restaurant;

    public RestaurantMarker(Marker marker, Restaurant restaurant) {
        this._marker = marker;
        this._restaurant = restaurant;
    }

    public void show() {
        this._marker.setVisible(true);
    }

    public void hide() {
        this._marker.setVisible(false);
    }

    public void remove() {
        this._marker.remove();
    }

    public boolean shouldShow(LinkedHashMap<String, IViewableFilter> currentFilters) {
        return FilterEvaluator.shouldShow(this._restaurant, currentFilters);
    }

    public Location getLocation() {
        return this._restaurant.location;
    }

    public boolean isLocation(Location loc) {
        return loc.lat == this._restaurant.location.lat && loc.lng == this._restaurant.location.lng;
    }

    public void select() {
        this._marker.showInfoWindow();
    }

    public String getName() {
        return this._restaurant.name;
    }

    public String getRating() {
        return this._restaurant.rating;
    }

    public String getAddress() {
        return this._restaurant.location.address;
    }

    public String getGenre() {
        return this._restaurant.genre;
    }

    public String getSubGenre() {
        return this._restaurant.subGenre;
    }

    public boolean isVisible() {
        return this._marker.isVisible();
    }

    public Marker getMarker() {
        return this._marker;
    }

    public Restaurant getRestaurant() {
        return this._restaurant;
    }
}
