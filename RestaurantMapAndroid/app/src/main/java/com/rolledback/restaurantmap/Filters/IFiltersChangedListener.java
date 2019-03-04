package com.rolledback.restaurantmap.Filters;

import com.rolledback.restaurantmap.Filters.Models.IViewableFilter;

import java.util.ArrayList;

public interface IFiltersChangedListener {
    void onFiltersChanged(ArrayList<IViewableFilter> filters);
}
