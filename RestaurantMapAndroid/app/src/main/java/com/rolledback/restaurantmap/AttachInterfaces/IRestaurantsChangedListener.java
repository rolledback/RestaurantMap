package com.rolledback.restaurantmap.AttachInterfaces;

import com.rolledback.restaurantmap.RestaurantMapAPI.Restaurant;
import java.util.List;

public interface IRestaurantsChangedListener {
    void onRestaurantsChanged(List<Restaurant> restaurants);
}
