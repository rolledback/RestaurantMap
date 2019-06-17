package com.rolledback.restaurantmap.Lib;

import android.util.Pair;

import com.rolledback.restaurantmap.Filters.Models.CheckFilter;
import com.rolledback.restaurantmap.Filters.Models.FilterList;
import com.rolledback.restaurantmap.Filters.Models.IViewableFilter;
import com.rolledback.restaurantmap.Filters.Models.ToggleFilter;
import com.rolledback.restaurantmap.RestaurantMapAPI.Restaurant;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class FilterEvaluator {

    public static boolean shouldShow(Restaurant restaurant, LinkedHashMap<String, IViewableFilter> currentFilters) {
        Iterator fIt = currentFilters.entrySet().iterator();
        boolean shouldShow = true;
        while (fIt.hasNext()) {
            Map.Entry<String, IViewableFilter> pair = (Map.Entry)fIt.next();
            IViewableFilter filter = pair.getValue();
            String field = pair.getKey();
            switch (field) {
                case "rating":
                    shouldShow = shouldShow && FilterEvaluator._evalRatingFilter(restaurant, (FilterList)filter);
                    break;
                case "genre":
                    shouldShow = shouldShow && FilterEvaluator._evalGenreFilter(restaurant, (FilterList)filter);
                    break;
                case "other":
                    shouldShow = shouldShow && FilterEvaluator._evalOtherFilter(restaurant, (FilterList)filter);
                    break;
            }
        }
        return shouldShow;
    }

    private static boolean _evalRatingFilter(Restaurant restaurant, FilterList<Pair<String, Boolean>, CheckFilter> filter) {
        boolean noneTrue = true;
        boolean currRatingSelected = false;
        ArrayList<Pair<String, Boolean>> value = filter.getValue();
        for (int i = 0; i < value.size(); i++) {
            Pair<String, Boolean> currValue = value.get(i);
            if (currValue.first.equalsIgnoreCase(restaurant.rating)) {
                currRatingSelected = currValue.second;
            }
            if (currValue.second) {
                noneTrue = false;
            }
        }

        return currRatingSelected || noneTrue;
    }

    private static boolean _evalGenreFilter(Restaurant restaurant, FilterList<Pair<String, Boolean>, CheckFilter> filter) {
        boolean noneTrue = true;
        boolean currGenreSelected = false;
        ArrayList<Pair<String, Boolean>> value = filter.getValue();
        for (int i = 0; i < value.size(); i++) {
            Pair<String, Boolean> currValue = value.get(i);
            if (currValue.first.equalsIgnoreCase(restaurant.genre)) {
                currGenreSelected = currValue.second;
            }
            if (currValue.second) {
                noneTrue = false;
            }
        }
        return currGenreSelected || noneTrue;
    }

    private static boolean _evalOtherFilter(Restaurant restaurant, FilterList<Pair<String, Boolean>, ToggleFilter> filter) {
        ArrayList<Pair<String, Boolean>> value = filter.getValue();
        return true;
    }
}
