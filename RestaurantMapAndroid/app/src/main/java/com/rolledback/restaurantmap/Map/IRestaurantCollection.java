package com.rolledback.restaurantmap.Map;

import com.rolledback.restaurantmap.Filters.Models.IViewableFilter;
import com.rolledback.restaurantmap.RestaurantMapAPI.Restaurant;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public interface IRestaurantCollection {
    void addItems(List<Restaurant> items);
    List<Restaurant> getItems();
    void clearItems();

    void saveToCache();
    void loadFromCache();

    ArrayList<String> availableGenres();
    ArrayList<String> availableSubGenres();
}
