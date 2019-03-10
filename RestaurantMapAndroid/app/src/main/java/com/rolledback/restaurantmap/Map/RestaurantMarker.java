package com.rolledback.restaurantmap.Map;

import android.util.Pair;

import com.google.android.gms.maps.model.Marker;
import com.rolledback.restaurantmap.Filters.Models.CheckFilter;
import com.rolledback.restaurantmap.Filters.Models.FilterList;
import com.rolledback.restaurantmap.Filters.Models.IViewableFilter;
import com.rolledback.restaurantmap.Filters.Models.ToggleFilter;
import com.rolledback.restaurantmap.RestaurantMapAPI.Restaurant;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Iterator;
import java.util.Map;

public class RestaurantMarker {
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
        Iterator fIt = currentFilters.entrySet().iterator();
        boolean shouldShow = true;
        while (fIt.hasNext()) {
            Map.Entry<String, IViewableFilter> pair = (Map.Entry)fIt.next();
            IViewableFilter filter = pair.getValue();
            String field = pair.getKey();
            switch (field) {
                case "rating":
                    shouldShow = shouldShow && this._evalRatingFilter((FilterList)filter);
                    break;
                case "genre":
                    shouldShow = shouldShow && this._evalGenreFilter((FilterList)filter);
                    break;
                case "other":
                    shouldShow = shouldShow && this._evalOtherFilter((FilterList)filter);
                    break;
            }
        }
        return shouldShow;
    }

    private boolean _evalRatingFilter(FilterList<Pair<String, Boolean>, CheckFilter> filter) {
        boolean noneTrue = true;
        boolean currRatingSelected = false;
        ArrayList<Pair<String, Boolean>> value = filter.getValue();
        for (int i = 0; i < value.size(); i++) {
            Pair<String, Boolean> currValue = value.get(i);
            if (currValue.first.equalsIgnoreCase(this._restaurant.rating)) {
                currRatingSelected = currValue.second;
            }
            if (currValue.second) {
                noneTrue = false;
            }
        }
        return currRatingSelected || noneTrue;
    }

    private boolean _evalGenreFilter(FilterList<Pair<String, Boolean>, CheckFilter> filter) {
        boolean noneTrue = true;
        boolean currGenreSelected = false;
        ArrayList<Pair<String, Boolean>> value = filter.getValue();
        for (int i = 0; i < value.size(); i++) {
            Pair<String, Boolean> currValue = value.get(i);
            if (currValue.first.equalsIgnoreCase(this._restaurant.genre)) {
                currGenreSelected = currValue.second;
            }
            if (currValue.second) {
                noneTrue = false;
            }
        }
        return currGenreSelected || noneTrue;
    }

    private boolean _evalOtherFilter(FilterList<Pair<String, Boolean>, ToggleFilter> filter) {
        ArrayList<Pair<String, Boolean>> value = filter.getValue();
        return true;
    }
}
