package com.rolledback.restaurantmap.Filters;

import com.rolledback.restaurantmap.Filters.Models.IViewableFilter;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public interface IFiltersChangedListener {
    void onFiltersChanged(LinkedHashMap<String, IViewableFilter> filters);
}
